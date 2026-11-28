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
import java.util.List;

import org.springframework.stereotype.Service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * 【ソースファイル名】
 * MlbStandingsService.java
 *
 * 【概要】
 * MLB Stats APIから指定年度のレギュラーシーズン順位情報を取得するクラスです。
 * JSONを解析し、地区順・地区順位順の表示用データを返します。
 *
 * 【作成日】2026-11-28
 * 【最終更新日】2026-11-28
 *
 * @author masa
 * @version 1.0
 */
@Service
public class MlbStandingsService {

    // 年度の検証に使用する日本のタイムゾーンを定義します。
    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");

    // 接続タイムアウト10秒のHTTPクライアントを作成し、再利用します。
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Springが用意するJSON解析用オブジェクトを保持します。
    private final JsonMapper jsonMapper;

    /**
     * 順位情報の解析に使用するJSON解析用オブジェクトを受け取ります。
     *
     * @param jsonMapper Springが注入するJSON解析用オブジェクト
     */
    public MlbStandingsService(JsonMapper jsonMapper) {
        // 注入されたJSON解析用オブジェクトを設定します。
        this.jsonMapper = jsonMapper;
    }

    /**
     * AL・NLのレギュラーシーズン順位表を地区順・順位順で返します。
     *
     * @param season 取得するシーズンの年度
     * @return APIから取得できた地区の順位表。該当する地区がない場合は空リスト
     * @throws IllegalStateException 年度が不正、API通信やJSON解析に失敗した場合
     */
    public List<MlbStandingDivision> getStandings(int season) {
        // 不正な年度でAPIへアクセスしないように確認します。
        if (season < 1876 || season > LocalDate.now(JST).getYear()) {
            throw new IllegalStateException("年度が不正です。");
        }
        try {
            // 既存のHTTPクライアントでAL・NLのレギュラーシーズン順位表を取得します。
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://statsapi.mlb.com/api/v1/standings?leagueId=103,104&season="
                            + season + "&standingsTypes=regularSeason"))
                    .timeout(Duration.ofSeconds(20)).GET().build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            // HTTPエラーと不正なJSON構造を、正常な空結果と区別します。
            if (response.statusCode() != 200) {
                throw new IOException("MLB APIのHTTPステータス: " + response.statusCode());
            }
            JsonNode root = jsonMapper.readTree(response.body());
            if (root == null || !root.path("records").isArray()) {
                throw new IOException("MLB APIのrecords配列がありません。");
            }
            List<MlbStandingDivision> divisions = new ArrayList<>();
            List<Integer> divisionOrder = List.of(201, 202, 200, 204, 205, 203);
            for (JsonNode record : root.path("records")) {
                // 地区を識別する整数IDと、チーム成績の配列を確認します。
                JsonNode leagueNode = record.path("league").path("id");
                JsonNode divisionNode = record.path("division").path("id");
                if (!leagueNode.isIntegralNumber() || !leagueNode.canConvertToInt()
                        || !divisionNode.isIntegralNumber() || !divisionNode.canConvertToInt()
                        || !record.path("teamRecords").isArray()) {
                    throw new IOException("MLB APIの地区情報の構造が不正です。");
                }
                int leagueId = leagueNode.asInt();
                int divisionId = divisionNode.asInt();
                if (!divisionOrder.contains(divisionId) || (leagueId != 103 && leagueId != 104)) {
                    continue;
                }
                // 地区IDに対応する表示名を設定します。
                String divisionName = switch (divisionId) {
                    case 201 -> "ア・リーグ 東地区";
                    case 202 -> "ア・リーグ 中地区";
                    case 200 -> "ア・リーグ 西地区";
                    case 204 -> "ナ・リーグ 東地区";
                    case 205 -> "ナ・リーグ 中地区";
                    case 203 -> "ナ・リーグ 西地区";
                    default -> throw new IOException("対象外の地区IDです。");
                };
                List<MlbStandingTeam> teams = new ArrayList<>();
                for (JsonNode teamRecord : record.path("teamRecords")) {
                    // チームを特定できない行は、架空のIDを補わず取得失敗として扱います。
                    JsonNode id = teamRecord.path("team").path("id");
                    if (!id.isIntegralNumber() || !id.canConvertToLong() || id.asLong() <= 0) {
                        throw new IOException("MLB APIのチームIDが不正です。");
                    }
                    // 順位の文字列を変換し、勝敗の0と欠損値を区別して保持します。
                    teams.add(new MlbStandingTeam(id.asLong(),
                            getProfileText(teamRecord.path("team").path("name")),
                            parseDivisionRank(teamRecord.path("divisionRank")),
                            getBattingStat(teamRecord, "wins"), getBattingStat(teamRecord, "losses"),
                            getProfileText(teamRecord.path("winningPercentage")),
                            getProfileText(teamRecord.path("gamesBack"))));
                }
                // 不正・欠損順位は末尾に置き、空のteamRecordsも正常結果として保持します。
                teams.sort(Comparator.comparing(MlbStandingTeam::getDivisionRank,
                        Comparator.nullsLast(Comparator.naturalOrder())));
                divisions.add(new MlbStandingDivision(leagueId, divisionId, divisionName, teams));
            }
            // AL東・中・西、NL東・中・西の順序をJava側で保証します。
            divisions.sort(Comparator.comparingInt(d -> divisionOrder.indexOf(d.getDivisionId())));
            return divisions;
        } catch (InterruptedException e) {
            // 通信の中断状態を復元し、呼び出し元へ失敗を通知します。
            Thread.currentThread().interrupt();
            throw new IllegalStateException("MLB APIへの通信が中断されました。", e);
        } catch (IOException | JacksonException e) {
            // 既存の例外形式で順位情報の取得失敗をControllerへ通知します。
            throw new IllegalStateException("MLB順位情報の取得に失敗しました。", e);
        }
    }

    private Integer parseDivisionRank(JsonNode node) {
        // APIの文字列または整数の順位だけを受け入れます。
        if (!node.isString() && !node.isIntegralNumber()) {
            return null;
        }
        try {
            // 正の整数に変換できない順位を欠損値として扱います。
            int rank = node.isIntegralNumber() && node.canConvertToInt()
                    ? node.asInt() : Integer.parseInt(node.asString("").strip());
            return rank > 0 ? rank : null;
        } catch (NumberFormatException e) {
            // 不正形式や整数範囲外の順位でも、他のチームの表示を継続します。
            return null;
        }
    }

    private String getProfileText(JsonNode node) {
        // 文字列だけを取り出して前後の空白を除き、表示できない値をハイフンにします。
        String value = node.isString() ? node.asString("").strip() : "";
        return value.isEmpty() ? "-" : value;
    }

    private Integer getBattingStat(JsonNode batting, String fieldName) {
        // 整数型とIntegerの範囲を確認し、欠損や不正値を0に変換せずnullとして扱います。
        JsonNode value = batting.path(fieldName);
        return value.isIntegralNumber() && value.canConvertToInt() ? value.asInt() : null;
    }
}
