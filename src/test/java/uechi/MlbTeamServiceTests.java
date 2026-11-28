package uechi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;

import tools.jackson.databind.json.JsonMapper;

/**
 * 【ソースファイル名】
 * MlbTeamServiceTests.java
 *
 * 【概要】
 * チームService分離後の欠損値・重複排除・並び順・エラー処理を確認します。
 *
 * 【作成日】2026-11-28
 * 【最終更新日】2026-11-28
 *
 * @author masa
 * @version 1.0
 */
class MlbTeamServiceTests {

    @Test
    void preservesProfileAndRosterBehavior() throws Exception {
        // 通信をモックに置き換え、プロフィールの必須IDと欠損項目を確認します。
        var service = new MlbTeamService(new JsonMapper());
        var client = mock(HttpClient.class);
        ReflectionTestUtils.setField(service, "httpClient", client);
        HttpResponse<String> response = mock(HttpResponse.class);
        when(client.send(any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("""
                {"teams":[{"id":119,"name":"Dodgers"}]}
                """);
        assertEquals("Dodgers", service.getTeamProfile(119).getName());
        assertEquals("-", service.getTeamProfile(119).getVenue());
        assertThrows(IllegalStateException.class, () -> service.getTeamProfile(120));
        assertThrows(IllegalStateException.class, () -> service.getTeamProfile(0));

        // 重複IDを除外し、ID不明でも名前がある選手を保持することを確認します。
        when(response.body()).thenReturn("""
                {"roster":[
                  {"person":{"id":2,"fullName":"Zed"}},
                  {"person":{"id":2,"fullName":"Duplicate"}},
                  {"person":{"fullName":"Amy"}},
                  {}
                ]}
                """);
        var players = service.getTeamRoster(119, 2025);
        assertEquals(List.of("Amy", "Zed"),
                players.stream().map(MlbTeamRosterPlayer::getPlayerName).toList());
        assertNull(players.get(0).getPlayerId());
        assertEquals("-", players.get(1).getJerseyNumber());
        assertEquals("-", players.get(1).getPosition());
        verify(client).send(argThat(request ->
                request.uri().toString().equals("https://statsapi.mlb.com/api/v1/teams/119/roster?rosterType=fullSeason&season=2025")
                && request.timeout().orElseThrow().equals(Duration.ofSeconds(20))),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        // 正常な空配列と不正構造、HTTPエラーを区別することを確認します。
        when(response.body()).thenReturn("{\"roster\":[]}");
        assertTrue(service.getTeamRoster(119, 2024).isEmpty());
        when(response.body()).thenReturn("{}");
        assertThrows(IllegalStateException.class, () -> service.getTeamRoster(119, 2024));
        when(response.statusCode()).thenReturn(500);
        assertThrows(IllegalStateException.class, () -> service.getTeamProfile(119));
        assertThrows(IllegalStateException.class, () -> service.getTeamRoster(119, 2024));
    }

    @Test
    void controllerKeepsProfileAndNavigationWhenRosterFails() {
        // ロスターのみ失敗した場合もプロフィールと戻り先を保持することを確認します。
        var api = mock(MlbGameService.class);
        var standings = mock(MlbStandingsService.class);
        var team = mock(MlbTeamService.class);
        var profile = new MlbTeamProfile(119, "Dodgers", "LAD", "NL", "West", "Stadium");
        when(team.getTeamProfile(119)).thenReturn(profile);
        when(team.getTeamRoster(119, 2025)).thenThrow(new IllegalStateException());
        var model = new ExtendedModelMap();
        var controller = new MlbController(api, standings, team, mock(MlbPlayerService.class));
        assertEquals("mlb-team", controller.team(119, "2025", "standings", 824474L,
                LocalDate.of(2026, 8, 21), model));
        assertSame(profile, model.get("teamProfile"));
        assertTrue(model.containsAttribute("rosterErrorMessage"));
        assertEquals("2025", model.get("season"));
        assertEquals("standings", model.get("from"));
        assertEquals(824474L, model.get("gamePk"));
        assertEquals(LocalDate.of(2026, 8, 21), model.get("targetDate"));
        verifyNoInteractions(api, standings);
    }
}