package uechi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;

import tools.jackson.databind.json.JsonMapper;

/**
 * 【ソースファイル名】
 * MlbPlayerServiceTests.java
 *
 * 【概要】
 * 選手Serviceの年齢・単位変換と独立したエラー処理を確認するクラスです。
 *
 * 【作成日】2026-11-28
 * 【最終更新日】2026-11-28
 *
 * @author masa
 * @version 1.0
 */
class MlbPlayerServiceTests {
    @Test
    void preservesAgeAndUnitConversions() {
        // 日本時間の当日を基準に、誕生日前後と不正な日付の処理を確認します。
        var service = new MlbPlayerService(new JsonMapper());
        var today = LocalDate.now(ZoneId.of("Asia/Tokyo"));
        assertEquals(Integer.valueOf(Period.between(LocalDate.of(1994, 7, 5), today).getYears()),
                ReflectionTestUtils.<Integer>invokeMethod(service, "calculateAge", "1994-07-05"));
        assertEquals(Integer.valueOf(20),
                ReflectionTestUtils.<Integer>invokeMethod(service, "calculateAge", today.minusYears(20).toString()));
        assertNull(ReflectionTestUtils.invokeMethod(service, "calculateAge", today.plusDays(1).toString()));
        assertNull(ReflectionTestUtils.invokeMethod(service, "calculateAge", "2025-02-30"));

        // 身長と体重の四捨五入、不正形式と欠損値を確認します。
        assertEquals(Integer.valueOf(193),
                ReflectionTestUtils.<Integer>invokeMethod(service, "convertHeightToCm", "6' 4\""));
        assertEquals(Integer.valueOf(95),
                ReflectionTestUtils.<Integer>invokeMethod(service, "convertWeightToKg", 210));
        assertNull(ReflectionTestUtils.invokeMethod(service, "convertHeightToCm", "6' 12\""));
        assertNull(ReflectionTestUtils.invokeMethod(service, "convertHeightToCm", "-"));
        assertNull(ReflectionTestUtils.invokeMethod(service, "convertWeightToKg", (Object) null));
        assertNull(ReflectionTestUtils.invokeMethod(service, "parseSeasonYear", "unknown"));
    }

    @Test
    void keepsIndependentErrorsAndNavigation() {
        // 各APIだけを順に失敗させ、残りの取得処理と戻り先情報が維持されることを確認します。
        for (int failed = 0; failed < 3; failed++) {
            var api = mock(MlbGameService.class);
            var standings = mock(MlbStandingsService.class);
            var team = mock(MlbTeamService.class);
            var player = mock(MlbPlayerService.class);
            var profile = mock(MlbPlayerProfile.class);
            when(player.getPlayerProfile(660271)).thenReturn(profile);
            when(player.getPlayerHittingYearByYear(660271)).thenReturn(List.of());
            when(player.getPlayerPitchingYearByYear(660271)).thenReturn(List.of());
            if (failed == 0) {
                when(player.getPlayerProfile(660271)).thenThrow(new IllegalStateException());
            } else if (failed == 1) {
                when(player.getPlayerHittingYearByYear(660271)).thenThrow(new IllegalStateException());
            } else {
                when(player.getPlayerPitchingYearByYear(660271)).thenThrow(new IllegalStateException());
            }
            var model = new ExtendedModelMap();
            var controller = new MlbController(api, standings, team, player);
            assertEquals("mlb-player", controller.player(660271, 119L, 2025, "standings",
                    824474L, LocalDate.of(2026, 8, 21), model));
            assertEquals(failed == 0, model.containsAttribute("errorMessage"));
            assertEquals(failed == 1, model.containsAttribute("hittingErrorMessage"));
            assertEquals(failed == 2, model.containsAttribute("pitchingErrorMessage"));
            assertEquals(failed != 0, model.containsAttribute("playerProfile"));
            assertEquals(failed != 1, model.containsAttribute("hittingSeasons"));
            assertEquals(failed != 2, model.containsAttribute("pitchingSeasons"));
            assertEquals(119L, model.get("teamId"));
            assertEquals(2025, model.get("season"));
            assertEquals("standings", model.get("from"));
            assertEquals(824474L, model.get("gamePk"));
            assertEquals(LocalDate.of(2026, 8, 21), model.get("targetDate"));
            verify(player).getPlayerProfile(660271);
            verify(player).getPlayerHittingYearByYear(660271);
            verify(player).getPlayerPitchingYearByYear(660271);
            verifyNoInteractions(api, standings, team);
        }
    }
}