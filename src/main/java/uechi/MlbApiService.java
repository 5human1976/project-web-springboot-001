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
 * MlbApiService.java
 *
 * 【概要】
 * MLB Stats APIから対象日の試合一覧や1試合の基本詳細情報を取得するクラスです。
 * API通信とJSON解析、日時変換、重複排除を担当します。
 *
 * 【作成日】2026-10-27
 * 【最終更新日】2026-11-19
 *
 * @author masa
 * @version 1.0
 */
@Service
public class MlbApiService {

    // MLBの試合日程を取得するAPIのURLを定義します。
    private static final String SCHEDULE_URL = "https://statsapi.mlb.com/api/v1/schedule?sportId=1&date=";

    // 試合IDを指定して詳細情報を取得するAPIのベースURLを定義します。
    private static final String GAME_DETAIL_URL = "https://statsapi.mlb.com/api/v1.1/game/";

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

    /**
     * 指定された試合の基本詳細情報を取得し、開始日時を日本時間にして返します。
     * イニング別得点とR・H・Eを含め、未設定の数値はnullを保持します。
     *
     * @param gamePk MLB APIで試合を識別するID
     * @return 詳細画面で使用する試合の基本情報
     * @throws IllegalStateException API通信、JSON解析、日時変換に失敗した場合
     */
    public MlbGameDetail getGameDetail(long gamePk) {
        try {
            // 既存のHTTPクライアントで詳細APIへアクセスし、20秒のタイムアウトを設定します。
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GAME_DETAIL_URL + gamePk + "/feed/live"))
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            // HTTPステータスが200以外の場合は取得失敗として処理します。
            if (response.statusCode() != 200) {
                throw new IOException("MLB APIのHTTPステータス: " + response.statusCode());
            }

            // JSONを解析し、必須の試合IDが含まれていることを確認します。
            JsonNode root = jsonMapper.readTree(response.body());
            if (root == null || !root.path("gamePk").isIntegralNumber()
                    || root.path("gamePk").asLong(0) <= 0) {
                throw new IOException("MLB APIの試合IDがありません。");
            }
            long responseGamePk = root.path("gamePk").asLong(0);
            JsonNode gameData = root.path("gameData");

            // 必須のUTC開始日時を日本時間へ変換し、一覧と同じ書式に整形します。
            String dateTime = gameData.path("datetime").path("dateTime").asString("");
            String gameDateJst = Instant.parse(dateTime).atZone(JST).format(JST_FORMAT);

            // 試合の基本情報を取得し、未設定の文字列にはハイフンを設定します。
            String awayTeam = gameData.path("teams").path("away").path("name").asString("-");
            String homeTeam = gameData.path("teams").path("home").path("name").asString("-");
            String status = gameData.path("status").path("detailedState").asString("-");
            String venue = gameData.path("venue").path("name").asString("-");

            // 得点が整数で存在する場合だけ値を取得し、未設定の場合はnullを保持します。
            JsonNode linescore = root.path("liveData").path("linescore");
            JsonNode scoreTeams = linescore.path("teams");
            JsonNode awayRuns = scoreTeams.path("away").path("runs");
            JsonNode homeRuns = scoreTeams.path("home").path("runs");
            Integer awayScore = awayRuns.isIntegralNumber() ? awayRuns.asInt() : null;
            Integer homeScore = homeRuns.isIntegralNumber() ? homeRuns.asInt() : null;

            // 両チームの安打数と失策数を取得し、未設定の場合はnullを保持します。
            JsonNode awayHitsNode = scoreTeams.path("away").path("hits");
            JsonNode awayErrorsNode = scoreTeams.path("away").path("errors");
            JsonNode homeHitsNode = scoreTeams.path("home").path("hits");
            JsonNode homeErrorsNode = scoreTeams.path("home").path("errors");
            Integer awayHits = awayHitsNode.isIntegralNumber() ? awayHitsNode.asInt() : null;
            Integer awayErrors = awayErrorsNode.isIntegralNumber() ? awayErrorsNode.asInt() : null;
            Integer homeHits = homeHitsNode.isIntegralNumber() ? homeHitsNode.asInt() : null;
            Integer homeErrors = homeErrorsNode.isIntegralNumber() ? homeErrorsNode.asInt() : null;

            // APIのイニング配列を順番に読み取り、延長回も含めて得点をリストに追加します。
            List<MlbInningScore> innings = new ArrayList<>();
            JsonNode inningNodes = linescore.path("innings");
            if (inningNodes.isArray()) {
                for (JsonNode inningNode : inningNodes) {
                    // 各回の番号と両チームの得点を取得し、未設定の得点はnullにします。
                    int inning = inningNode.path("num").asInt();
                    JsonNode inningAwayRuns = inningNode.path("away").path("runs");
                    JsonNode inningHomeRuns = inningNode.path("home").path("runs");
                    Integer awayInningRuns = inningAwayRuns.isIntegralNumber() ? inningAwayRuns.asInt() : null;
                    Integer homeInningRuns = inningHomeRuns.isIntegralNumber() ? inningHomeRuns.asInt() : null;
                    innings.add(new MlbInningScore(inning, awayInningRuns, homeInningRuns));
                }
            }

            // 投手結果を取得します。未確定やセーブなしの場合は「-」にします。
            JsonNode decisions = root.path("liveData").path("decisions");
            String winningPitcher = decisions.path("winner").path("fullName").asString("-");
            String losingPitcher = decisions.path("loser").path("fullName").asString("-");
            String savePitcher = decisions.path("save").path("fullName").asString("-");

            // 同じレスポンスのboxscoreから両チームの出場選手名を取得します。
            JsonNode boxscoreTeams = root.path("liveData").path("boxscore").path("teams");
            MlbTeamPlayers awayPlayers = getTeamPlayers(boxscoreTeams.path("away"));
            MlbTeamPlayers homePlayers = getTeamPlayers(boxscoreTeams.path("home"));

            // 基本情報、スコアボード、投手結果、出場選手を詳細画面用にまとめて返します。
            return new MlbGameDetail(responseGamePk, gameDateJst, awayTeam, awayScore,
                    homeTeam, homeScore, status, venue, innings,
                    awayHits, awayErrors, homeHits, homeErrors,
                    winningPitcher, losingPitcher, savePitcher, awayPlayers, homePlayers);
        } catch (InterruptedException e) {
            // 通信の中断状態を復元し、呼び出し元が扱える例外として通知します。
            Thread.currentThread().interrupt();
            throw new IllegalStateException("MLB APIへの通信が中断されました。", e);
        } catch (IOException | JacksonException | DateTimeException e) {
            // 通信、JSON解析、日時変換の失敗をControllerに通知します。
            throw new IllegalStateException("MLB試合詳細情報の取得に失敗しました。", e);
        }
    }

    /**
     * 1チーム分のboxscoreから選手名を取得し、投手を元の登板順で分類します。
     * 名前が欠けた投手がいても、他の投手を先発や最終登板へ繰り上げません。
     */
    private MlbTeamPlayers getTeamPlayers(JsonNode team) {
        // 先発野手、途中出場野手、投手名を保持するリストと重複確認用の集合を用意します。
        JsonNode players = team.path("players");
        List<MlbBatter> startingBatters = new ArrayList<>();
        List<MlbBatter> substituteBatters = new ArrayList<>();
        Set<Long> batterIds = new HashSet<>();
        List<String> reliefPitchers = new ArrayList<>();
        Set<Long> pitcherIds = new HashSet<>();
        String startingPitcher = "-";
        String finalPitcher = "-";

        // battersの選手名とbattingOrderを取得し、打順情報がない選手や不正な値を除外します。
        JsonNode batters = team.path("batters");
        if (batters.isArray()) {
            for (JsonNode id : batters) {
                String name = getPlayerName(players, id);
                if (name == null) {
                    continue;
                }
                String battingOrder = players.path("ID" + id.asLong()).path("battingOrder").asString("");
                int order;
                try {
                    // 打順情報を整数に変換し、変換できない場合はその選手だけをスキップします。
                    order = Integer.parseInt(battingOrder);
                } catch (NumberFormatException e) {
                    continue;
                }

                // 打順枠と交代順を分離し、1〜9以外の枠や同じ選手IDの重複を除外します。
                int battingSlot = order / 100;
                int substitutionOrder = order % 100;
                if (battingSlot < 1 || battingSlot > 9 || !batterIds.add(id.asLong())) {
                    continue;
                }

                // 交代順が0なら先発、それ以外は途中出場として別のリストに追加します。
                MlbBatter batter = new MlbBatter(battingSlot, name, substitutionOrder > 0, substitutionOrder);
                if (batter.isSubstitute()) {
                    substituteBatters.add(batter);
                } else {
                    startingBatters.add(batter);
                }
            }
        }

        // pitchersの元の位置で先発・中継ぎ・最終登板を分け、1人の場合は先発だけにします。
        JsonNode pitchers = team.path("pitchers");
        if (pitchers.isArray()) {
            for (int index = 0; index < pitchers.size(); index++) {
                JsonNode id = pitchers.path(index);
                String name = getPlayerName(players, id);
                if (name == null || !pitcherIds.add(id.asLong())) {
                    continue;
                }
                if (index == 0) {
                    startingPitcher = name;
                } else if (index == pitchers.size() - 1) {
                    finalPitcher = name;
                } else {
                    reliefPitchers.add(name);
                }
            }
        }

        // 先発を打順枠順、途中出場を打順枠と交代順で並べ、投手は登板順のまま保持します。
        startingBatters.sort(Comparator.comparingInt(MlbBatter::getBattingSlot));
        substituteBatters.sort(Comparator.comparingInt(MlbBatter::getBattingSlot)
                .thenComparingInt(MlbBatter::getSubstitutionOrder));

        // 情報がない場合も空リストとハイフンを持つチーム情報を返します。
        return new MlbTeamPlayers(startingBatters, substituteBatters, startingPitcher, reliefPitchers, finalPitcher);
    }

    /**
     * 選手IDに対応する名前を返し、IDや名前が取得できない場合はnullを返します。
     */
    private String getPlayerName(JsonNode players, JsonNode id) {
        // 正の整数として取得できない選手IDはスキップ対象にします。
        if (!id.isIntegralNumber() || id.asLong() <= 0) {
            return null;
        }

        // playersのID付きキーから氏名を取得し、空白のみの名前もスキップ対象にします。
        String name = players.path("ID" + id.asLong()).path("person").path("fullName").asString("");
        return name.isBlank() ? null : name;
    }
}
