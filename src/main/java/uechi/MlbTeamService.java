package uechi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * 【ソースファイル名】
 * MlbTeamService.java
 *
 * 【概要】
 * MLB Stats APIからチーム基本情報と年度別ロスターを取得するクラスです。
 * JSONを解析し、チーム詳細画面の表示用データを返します。
 *
 * 【作成日】2026-11-28
 * 【最終更新日】2026-11-28
 *
 * @author masa
 * @version 1.0
 */
@Service
public class MlbTeamService {

    // チーム基本情報と年度別ロスターの取得先を定義します。
    private static final String TEAM_URL = "https://statsapi.mlb.com/api/v1/teams/";

    // 年度の検証に使用する日本のタイムゾーンを定義します。
    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");

    // 接続タイムアウト10秒のHTTPクライアントを作成して再利用します。
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Springが用意するJSON解析用オブジェクトを保持します。
    private final JsonMapper jsonMapper;

    /**
     * チーム情報の解析に使用するJSON解析用オブジェクトを受け取ります。
     *
     * @param jsonMapper Springが注入するJSON解析用オブジェクト
     */
    public MlbTeamService(JsonMapper jsonMapper) {
        // 注入されたJSON解析用オブジェクトを設定します。
        this.jsonMapper = jsonMapper;
    }

    /**
     * チームIDに対応する基本情報を返します。
     *
     * @param teamId 正のチーム識別ID
     * @return チーム詳細画面に表示する基本情報
     * @throws IllegalStateException IDが不正、対象が存在しない、または取得に失敗した場合
     */
    public MlbTeamProfile getTeamProfile(long teamId) {
        // 正のチームIDであることを通信前に確認します。
        if (teamId <= 0) {
            throw new IllegalStateException("チームIDが不正です。");
        }
        try {
            // チーム基本情報を20秒のタイムアウト付きGETで取得します。
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TEAM_URL + teamId))
                    .timeout(Duration.ofSeconds(20)).GET().build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            // HTTPエラーと、対象チームが存在しないレスポンスを取得失敗として処理します。
            if (response.statusCode() != 200) {
                throw new IOException("MLB APIのHTTPステータス: " + response.statusCode());
            }
            JsonNode root = jsonMapper.readTree(response.body());
            if (root == null || !root.path("teams").isArray() || root.path("teams").isEmpty()) {
                throw new IOException("対象チームがありません。");
            }
            JsonNode team = root.path("teams").get(0);
            JsonNode id = team.path("id");
            if (!id.isIntegralNumber() || !id.canConvertToLong() || id.asLong() != teamId) {
                throw new IOException("チームIDが一致しません。");
            }
            // 欠損した文字列をハイフンに補完し、画面用の基本情報を作成します。
            return new MlbTeamProfile(teamId, getProfileText(team.path("name")),
                    getProfileText(team.path("abbreviation")), getProfileText(team.path("league").path("name")),
                    getProfileText(team.path("division").path("name")), getProfileText(team.path("venue").path("name")));
        } catch (InterruptedException e) {
            // 中断状態を復元し、呼び出し元へ取得失敗を通知します。
            Thread.currentThread().interrupt();
            throw new IllegalStateException("MLB APIへの通信が中断されました。", e);
        } catch (IOException | JacksonException e) {
            // 通信とJSON解析の失敗を既存の例外形式で通知します。
            throw new IllegalStateException("チーム基本情報の取得に失敗しました。", e);
        }
    }

    /**
     * 指定年度のfullSeasonロスターを選手名の昇順で返します。
     *
     * @param teamId 正のチーム識別ID
     * @param season 1876年から日本時間の現在年までの年度
     * @return 重複した選手IDを除いた選手一覧。該当者がいない場合は空リスト
     * @throws IllegalStateException IDや年度が不正、または取得に失敗した場合
     */
    public List<MlbTeamRosterPlayer> getTeamRoster(long teamId, int season) {
        // 極端な年度や未来年、不正なチームIDでの通信を防止します。
        if (teamId <= 0 || season < 1876 || season > LocalDate.now(JST).getYear()) {
            throw new IllegalStateException("チームIDまたは年度が不正です。");
        }
        try {
            // 指定年度のシーズン全体のロスターをGETで取得します。
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TEAM_URL + teamId + "/roster?rosterType=fullSeason&season=" + season))
                    .timeout(Duration.ofSeconds(20)).GET().build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            // HTTPエラーや配列の欠損を、正常な0件と区別します。
            if (response.statusCode() != 200) {
                throw new IOException("MLB APIのHTTPステータス: " + response.statusCode());
            }
            JsonNode root = jsonMapper.readTree(response.body());
            if (root == null || !root.path("roster").isArray()) {
                throw new IOException("MLB APIのroster配列がありません。");
            }
            List<MlbTeamRosterPlayer> players = new ArrayList<>();
            Set<Long> playerIds = new HashSet<>();
            for (JsonNode entry : root.path("roster")) {
                // IDが不明でも名前があれば保持し、情報のない行だけ除外します。
                JsonNode id = entry.path("person").path("id");
                Long playerId = id.isIntegralNumber() && id.canConvertToLong() && id.asLong() > 0
                        ? id.asLong() : null;
                String name = getProfileText(entry.path("person").path("fullName"));
                if (playerId == null && "-".equals(name)) {
                    continue;
                }
                // 有効な選手IDが重複する行を除外します。
                if (playerId != null && !playerIds.add(playerId)) {
                    continue;
                }
                players.add(new MlbTeamRosterPlayer(playerId, name,
                        getProfileText(entry.path("jerseyNumber")),
                        getProfileText(entry.path("position").path("name"))));
            }
            // APIの返却順に依存せず、選手名の昇順に並べて返します。
            players.sort(Comparator.comparing(MlbTeamRosterPlayer::getPlayerName));
            return players;
        } catch (InterruptedException e) {
            // 中断状態を復元し、呼び出し元へ取得失敗を通知します。
            Thread.currentThread().interrupt();
            throw new IllegalStateException("MLB APIへの通信が中断されました。", e);
        } catch (IOException | JacksonException e) {
            // 通信とJSON解析の失敗を既存の例外形式で通知します。
            throw new IllegalStateException("年度別選手一覧の取得に失敗しました。", e);
        }
    }

    private String getProfileText(JsonNode node) {
        // 文字列だけを取り出して前後の空白を除き、表示できない値をハイフンにします。
        String value = node.isString() ? node.asString("").strip() : "";
        return value.isEmpty() ? "-" : value;
    }
}
