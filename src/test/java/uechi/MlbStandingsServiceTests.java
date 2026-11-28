package uechi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;

import tools.jackson.databind.json.JsonMapper;

/**
 * 【ソースファイル名】
 * MlbStandingsServiceTests.java
 *
 * 【概要】
 * 順位表Serviceの分離後も並び順・欠損値・例外処理を維持することを確認します。
 *
 * 【作成日】2026-11-28
 * 【最終更新日】2026-11-28
 *
 * @author masa
 * @version 1.0
 */
class MlbStandingsServiceTests {

    @Test
    void preservesSortingMissingValuesAndRequest() throws Exception {
        // HTTP通信をモックに置き換え、順序を入れ替えたレスポンスを返します。
        var service = new MlbStandingsService(new JsonMapper());
        var client = mock(HttpClient.class);
        ReflectionTestUtils.setField(service, "httpClient", client);
        HttpResponse<String> response = mock(HttpResponse.class);
        when(client.send(any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("""
                {"records":[
                  {"league":{"id":104},"division":{"id":203},"teamRecords":[]},
                  {"league":{"id":103},"division":{"id":201},"teamRecords":[
                    {"team":{"id":1,"name":"Missing"},"wins":0},
                    {"team":{"id":2,"name":"Second"},"divisionRank":"2","wins":94},
                    {"team":{"id":3,"name":"First"},"divisionRank":1,"wins":94},
                    {"team":{"id":4,"name":"Invalid"},"divisionRank":"abc"}
                  ]}
                ]}
                """);

        // 地区順・順位順、欠損と0、空地区、不変リストを確認します。
        var result = service.getStandings(2025);
        assertEquals(List.of(201, 203), result.stream().map(MlbStandingDivision::getDivisionId).toList());
        var teams = result.get(0).getTeams();
        assertEquals(List.of("First", "Second", "Missing", "Invalid"),
                teams.stream().map(MlbStandingTeam::getTeamName).toList());
        assertEquals(0, teams.get(2).getWins());
        assertNull(teams.get(2).getLosses());
        assertNull(teams.get(3).getDivisionRank());
        assertEquals("-", teams.get(2).getWinningPercentage());
        assertTrue(result.get(1).getTeams().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> teams.clear());
        verify(client).send(argThat(request ->
                request.uri().toString().equals("https://statsapi.mlb.com/api/v1/standings?leagueId=103,104&season=2025&standingsTypes=regularSeason")
                && request.timeout().orElseThrow().equals(Duration.ofSeconds(20))),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        // 空結果は成功、不正構造とHTTPエラーは取得失敗になることを確認します。
        when(response.body()).thenReturn("{\"records\":[]}");
        assertTrue(service.getStandings(2025).isEmpty());
        when(response.body()).thenReturn("{\"records\":[{}]}");
        assertThrows(IllegalStateException.class, () -> service.getStandings(2025));
        when(response.statusCode()).thenReturn(500);
        assertThrows(IllegalStateException.class, () -> service.getStandings(2025));
    }

    @Test
    void restoresInterruptStatus() throws Exception {
        // 通信の割り込みを再現し、復元されたフラグを確認してから解除します。
        var service = new MlbStandingsService(new JsonMapper());
        var client = mock(HttpClient.class);
        ReflectionTestUtils.setField(service, "httpClient", client);
        when(client.send(any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenThrow(new InterruptedException());
        try {
            assertThrows(IllegalStateException.class, () -> service.getStandings(2025));
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void controllerUsesNewServiceAndKeepsYearValidation() {
        // 新Serviceだけで順位を取得し、既存Serviceを呼ばないことを確認します。
        var api = mock(MlbGameService.class);
        var standings = mock(MlbStandingsService.class);
        var controller = new MlbController(api, standings, mock(MlbTeamService.class), mock(MlbPlayerService.class));
        when(standings.getStandings(2025)).thenReturn(List.of());
        var model = new ExtendedModelMap();
        assertEquals("mlb-standings", controller.standings(null, model));
        assertEquals(2025, model.get("season"));
        verify(standings).getStandings(2025);
        verifyNoInteractions(api);

        // 入力範囲外では追加通信せず、エラーをモデルに設定します。
        var invalid = new ExtendedModelMap();
        controller.standings(1999, invalid);
        assertTrue(invalid.containsAttribute("errorMessage"));
        verifyNoMoreInteractions(standings);
    }
}