package uechi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * 【ソースファイル名】
 * MlbApiService.java
 *
 * 【概要】
 * MLB Stats APIから試合情報を取得し、日本時間で対象日に開始する試合を返すクラスです。
 * API通信とJSON解析、日時変換、重複排除を担当します。
 *
 * 【作成日】2026-10-27
 * 【最終更新日】2026-10-27
 *
 * @author masa
 * @version 1.0
 */
@Service
public class MlbApiService {

    // MLBの試合日程を取得するAPIのURLを定義します。
    private static final String SCHEDULE_URL = "https://statsapi.mlb.com/api/v1/schedule?sportId=1&date=";

    // 試合の開始日時を変換する日本のタイムゾーンを定義します。
    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");

    // 画面で利用しやすい日本時間の日時書式を定義します。
    private static final DateTimeFormatter JST_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 接続タイムアウトを設定したHTTPクライアントを1つ作成し、API通信で再利用します。
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Springが用意するJacksonのJSON解析用オブジェクトを保持します。
    private final JsonMapper jsonMapper;

    /**
     * MLB APIのレスポンス解析に使用するJacksonのオブジェクトを受け取ります。
     *
     * @param jsonMapper Springが注入するJSON解析用オブジェクト
     */
    public MlbApiService(JsonMapper jsonMapper) {
        // Springから渡されたJSON解析用オブジェクトをフィールドに設定します。
        this.jsonMapper = jsonMapper;
    }

    /**
     * 日本時間で対象日に開始するMLB試合一覧を返します。
     * 試合が存在しない場合は空のリストを返します。
     *
     * @param targetDate 日本時間での検索対象日
     * @return 試合IDの重複を除いた対象日の試合一覧
     * @throws IllegalStateException API通信、JSON解析、日時変換に失敗した場合
     */
    public List<MlbGame> getGames(LocalDate targetDate) {
        // 検索結果のリストと、採用済みの試合IDを記録する集合を用意します。
        List<MlbGame> games = new ArrayList<>();
        Set<Long> gamePks = new HashSet<>();

        try {
            // APIの日付と日本時間のずれを考慮し、前日と当日の2日分を順に取得します。
            for (LocalDate apiDate : List.of(targetDate.minusDays(1), targetDate)) {
                // リクエスト全体のタイムアウトを設定し、指定日の試合日程を取得します。
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(SCHEDULE_URL + apiDate))
                        .timeout(Duration.ofSeconds(20))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

                // HTTPステータスが200以外の場合は、正常な試合情報として扱わず例外にします。
                if (response.statusCode() != 200) {
                    throw new IOException("MLB APIのHTTPステータス: " + response.statusCode());
                }

                // JSONのdates配列を取得し、想定した形式でない場合は取得失敗として処理します。
                JsonNode root = jsonMapper.readTree(response.body());
                if (root == null || !root.path("dates").isArray()) {
                    throw new IOException("MLB APIのdates配列がありません。");
                }

                // dates配列に含まれる各日付のgames配列を順に読み取ります。
                for (JsonNode dateNode : root.path("dates")) {
                    // games配列が欠けている場合は、0試合と区別するため例外にします。
                    if (!dateNode.path("games").isArray()) {
                        throw new IOException("MLB APIのgames配列がありません。");
                    }
                    for (JsonNode gameNode : dateNode.path("games")) {
                        // 抽出と重複排除に必要な試合IDと開始日時が存在することを確認します。
                        long gamePk = gameNode.path("gamePk").asLong(0);
                        String gameDate = gameNode.path("gameDate").asString("");
                        if (gamePk <= 0 || gameDate.isBlank()) {
                            throw new IOException("MLB APIの試合IDまたは開始日時がありません。");
                        }

                        // UTCの開始日時を日本時間に変換し、対象日以外の試合を除外します。
                        ZonedDateTime gameDateJst = Instant.parse(gameDate).atZone(JST);
                        if (!gameDateJst.toLocalDate().equals(targetDate)) {
                            continue;
                        }

                        // 試合IDを集合に追加し、すでに採用済みの試合は重複として除外します。
                        if (!gamePks.add(gamePk)) {
                            continue;
                        }

                        // シリーズ内の試合番号を取得し、存在しない場合はnullにします。
                        JsonNode numberNode = gameNode.path("seriesGameNumber");
                        Integer seriesGameNumber = numberNode.isIntegralNumber() ? numberNode.asInt() : null;

                        // JSONの各項目と日本時間の開始日時から試合オブジェクトを作成します。
                        MlbGame game = new MlbGame(gamePk, gameDate, gameDateJst.format(JST_FORMAT),
                                gameNode.path("teams").path("away").path("team").path("name").asString("-"),
                                gameNode.path("teams").path("home").path("team").path("name").asString("-"),
                                gameNode.path("status").path("detailedState").asString("-"),
                                gameNode.path("venue").path("name").asString("-"),
                                gameNode.path("seriesDescription").asString("-"), seriesGameNumber);

                        // 対象日に該当し、重複していない試合を検索結果に追加します。
                        games.add(game);
                    }
                }
            }
        } catch (InterruptedException e) {
            // 通信の中断状態を復元し、呼び出し元が扱える例外として通知します。
            Thread.currentThread().interrupt();
            throw new IllegalStateException("MLB APIへの通信が中断されました。", e);
        } catch (IOException | JacksonException | DateTimeException e) {
            // 通信、JSON解析、日時変換の失敗をControllerに通知します。
            throw new IllegalStateException("MLB試合情報の取得に失敗しました。", e);
        }

        // 該当する試合がなければ空のリストを正常結果として返します。
        return games;
    }
}
