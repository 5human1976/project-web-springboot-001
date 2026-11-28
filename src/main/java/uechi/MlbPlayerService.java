package uechi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * 【ソースファイル名】
 * MlbPlayerService.java
 *
 * 【概要】
 * MLB Stats APIから選手プロフィールと年度別打撃・投手成績を取得するクラスです。
 * 年齢や身長・体重を表示用に変換し、成績を年度順で返します。
 *
 * 【作成日】2026-11-28
 * 【最終更新日】2026-11-28
 *
 * @author masa
 * @version 1.0
 */
@Service
public class MlbPlayerService {

    // 選手情報を取得するAPIのベースURLを定義します。
    private static final String PLAYER_PROFILE_URL = "https://statsapi.mlb.com/api/v1/people/";

    // 年齢計算に使用する日本のタイムゾーンを定義します。
    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");

    // 接続タイムアウト10秒のHTTPクライアントを作成して再利用します。
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Springが用意するJSON解析用オブジェクトを保持します。
    private final JsonMapper jsonMapper;

    /**
     * 選手情報の解析に使用するJSON解析用オブジェクトを受け取ります。
     *
     * @param jsonMapper Springが注入するJSON解析用オブジェクト
     */
    public MlbPlayerService(JsonMapper jsonMapper) {
        // 注入されたJSON解析用オブジェクトを設定します。
        this.jsonMapper = jsonMapper;
    }

    /**
     * 指定された選手の基本情報を取得し、画面表示用のプロフィールを返します。
     *
     * @param playerId MLBの選手識別ID（正の整数）
     * @return 欠損値と投打表記を整えた選手プロフィール
     * @throws IllegalStateException IDが不正な場合、またはAPI通信・JSON解析・必須情報の取得に失敗した場合
     */
    public MlbPlayerProfile getPlayerProfile(long playerId) {
        // 不正な選手IDではAPI通信を行わず、取得失敗として通知します。
        if (playerId <= 0) {
            throw new IllegalStateException("選手IDは正の整数で指定してください。");
        }

        try {
            // 現在の所属チームを含める指定で、プロフィールのGETリクエストを作成します。
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PLAYER_PROFILE_URL + playerId + "?hydrate=currentTeam"))
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();

            // 既存のHTTPクライアントでAPIを呼び出し、UTF-8の本文を取得します。
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            // HTTPステータスが200以外の場合は取得失敗として処理します。
            if (response.statusCode() != 200) {
                throw new IOException("MLB APIのHTTPステータス: " + response.statusCode());
            }

            // people配列の先頭を取得し、要求した選手の正しいIDが含まれることを確認します。
            JsonNode root = jsonMapper.readTree(response.body());
            if (root == null || !root.path("people").isArray() || root.path("people").isEmpty()) {
                throw new IOException("MLB APIの選手情報がありません。");
            }
            JsonNode person = root.path("people").path(0);
            JsonNode id = person.path("id");
            if (!id.isIntegralNumber() || !id.canConvertToLong() || id.asLong() != playerId) {
                throw new IOException("MLB APIの選手IDが不正です。");
            }

            // 出身地の存在する文字列だけを順に結合し、不要なカンマを除きます。
            List<String> birthParts = new ArrayList<>();
            for (String field : List.of("birthCity", "birthStateProvince", "birthCountry")) {
                String part = getProfileText(person.path(field));
                if (!"-".equals(part)) {
                    birthParts.add(part);
                }
            }
            String birthPlace = birthParts.isEmpty() ? "-" : String.join(", ", birthParts);

            // APIの投打コードを画面用の日本語に変換し、不明なコードはハイフンにします。
            String batSide = switch (getProfileText(person.path("batSide").path("code"))) {
                case "R" -> "右打";
                case "L" -> "左打";
                case "S" -> "両打";
                default -> "-";
            };
            String pitchHand = switch (getProfileText(person.path("pitchHand").path("code"))) {
                case "R" -> "右投";
                case "L" -> "左投";
                default -> "-";
            };

            // 体重はIntegerで表現できる整数だけを取得し、欠損や不正な型の場合はnullにします。
            JsonNode weightNode = person.path("weight");
            Integer weightLb = weightNode.isIntegralNumber() && weightNode.canConvertToInt()
                    ? weightNode.asInt() : null;

            // 身長と体重をcm・kgの整数に変換し、変換できない値はnullにします。
            Integer heightCm = convertHeightToCm(getProfileText(person.path("height")));
            Integer weightKg = convertWeightToKg(weightLb);

            // 生年月日から日本時間の当日時点の満年齢を計算します。
            String birthDate = getProfileText(person.path("birthDate"));
            Integer age = calculateAge(birthDate);

            // 文字列の欠損をハイフンにそろえ、プロフィールとしてまとめて返します。
            return new MlbPlayerProfile(id.asLong(), getProfileText(person.path("fullName")),
                    getProfileText(person.path("primaryNumber")),
                    getProfileText(person.path("currentTeam").path("name")),
                    getProfileText(person.path("primaryPosition").path("name")),
                    batSide, pitchHand, birthDate, age, birthPlace,
                    heightCm, weightKg,
                    getProfileText(person.path("mlbDebutDate")));
        } catch (InterruptedException e) {
            // 割り込み状態を復元し、通信が中断されたことをControllerへ通知します。
            Thread.currentThread().interrupt();
            throw new IllegalStateException("MLB APIへの通信が中断されました。", e);
        } catch (IOException | JacksonException e) {
            // 通信やJSON解析の失敗を、画面で扱う例外としてControllerへ通知します。
            throw new IllegalStateException("MLB選手プロフィールの取得に失敗しました。", e);
        }
    }


    /**
     * 選手の年度別打撃成績を古い年度から順に返します。
     * 成績がない場合は空のリストを返します。
     *
     * @param playerId MLBの選手識別ID（正の整数）
     * @return 年度昇順の打撃成績。解釈できない年度は末尾に配置します。
     * @throws IllegalStateException IDが不正な場合、またはAPI通信・JSON解析に失敗した場合
     */
    public List<MlbPlayerHittingSeason> getPlayerHittingYearByYear(long playerId) {
        // 不正な選手IDではAPI通信を行わず、取得失敗として通知します。
        if (playerId <= 0) {
            throw new IllegalStateException("選手IDは正の整数で指定してください。");
        }

        try {
            // プロフィールとは別に、指定選手の年度別打撃成績を取得します。
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PLAYER_PROFILE_URL + playerId
                            + "/stats?stats=yearByYear&group=hitting"))
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            // HTTPステータスが200以外の場合は取得失敗として処理します。
            if (response.statusCode() != 200) {
                throw new IOException("MLB APIのHTTPステータス: " + response.statusCode());
            }

            // stats配列を確認し、空配列と不正なレスポンスを区別します。
            JsonNode root = jsonMapper.readTree(response.body());
            if (root == null || !root.path("stats").isArray()) {
                throw new IOException("MLB APIのstats配列がありません。");
            }
            List<MlbPlayerHittingSeason> seasons = new ArrayList<>();
            for (JsonNode stats : root.path("stats")) {
                JsonNode splits = stats.path("splits");
                if (!splits.isArray()) {
                    throw new IOException("MLB APIのsplits配列がありません。");
                }

                // 各年度の成績を取得し、項目の欠損はnullまたはハイフンで保持します。
                for (JsonNode split : splits) {
                    JsonNode stat = split.path("stat");
                    seasons.add(new MlbPlayerHittingSeason(getProfileText(split.path("season")),
                            getBattingStat(stat, "gamesPlayed"), getBattingStat(stat, "atBats"),
                            getBattingStat(stat, "hits"), getBattingStat(stat, "homeRuns"),
                            getBattingStat(stat, "rbi"), getBattingStat(stat, "baseOnBalls"),
                            getBattingStat(stat, "strikeOuts"), getBattingStat(stat, "stolenBases"),
                            getProfileText(stat.path("avg")), getProfileText(stat.path("ops"))));
                }
            }

            // 数値として読める年度を昇順に並べ、解釈できない年度は末尾に配置します。
            seasons.sort(Comparator.comparing(
                    (MlbPlayerHittingSeason season) -> parseSeasonYear(season.getSeason()),
                    Comparator.nullsLast(Comparator.naturalOrder())));
            return seasons;
        } catch (InterruptedException e) {
            // 割り込み状態を復元し、通信の中断をControllerへ通知します。
            Thread.currentThread().interrupt();
            throw new IllegalStateException("MLB APIへの通信が中断されました。", e);
        } catch (IOException | JacksonException e) {
            // 通信やJSON解析の失敗を画面で扱う例外として通知します。
            throw new IllegalStateException("年度別打撃成績の取得に失敗しました。", e);
        }
    }

    /**
     * 選手の年度別投手成績を古い年度から順に返します。
     * 成績がない場合は空のリストを返します。
     *
     * @param playerId MLBの選手識別ID（正の整数）
     * @return 年度昇順の投手成績。解釈できない年度は末尾に配置します。
     * @throws IllegalStateException IDが不正な場合、またはAPI通信・JSON解析に失敗した場合
     */
    public List<MlbPlayerPitchingSeason> getPlayerPitchingYearByYear(long playerId) {
        // 不正な選手IDではAPI通信を行わず、取得失敗として通知します。
        if (playerId <= 0) {
            throw new IllegalStateException("選手IDは正の整数で指定してください。");
        }

        try {
            // プロフィールとは別に、指定選手の年度別投手成績を取得します。
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PLAYER_PROFILE_URL + playerId
                            + "/stats?stats=yearByYear&group=pitching"))
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            // HTTPステータスが200以外の場合は取得失敗として処理します。
            if (response.statusCode() != 200) {
                throw new IOException("MLB APIのHTTPステータス: " + response.statusCode());
            }

            // stats配列を確認し、空配列と不正なレスポンスを区別します。
            JsonNode root = jsonMapper.readTree(response.body());
            if (root == null || !root.path("stats").isArray()) {
                throw new IOException("MLB APIのstats配列がありません。");
            }
            List<MlbPlayerPitchingSeason> seasons = new ArrayList<>();
            for (JsonNode stats : root.path("stats")) {
                JsonNode splits = stats.path("splits");
                if (!splits.isArray()) {
                    throw new IOException("MLB APIのsplits配列がありません。");
                }

                // 各年度の成績を取得し、項目の欠損はnullまたはハイフンで保持します。
                for (JsonNode split : splits) {
                    JsonNode stat = split.path("stat");
                    seasons.add(new MlbPlayerPitchingSeason(getProfileText(split.path("season")),
                            getBattingStat(stat, "gamesPlayed"), getBattingStat(stat, "gamesStarted"),
                            getBattingStat(stat, "wins"), getBattingStat(stat, "losses"),
                            getBattingStat(stat, "saves"), getProfileText(stat.path("era")),
                            getProfileText(stat.path("inningsPitched")), getBattingStat(stat, "hits"),
                            getBattingStat(stat, "earnedRuns"), getBattingStat(stat, "baseOnBalls"),
                            getBattingStat(stat, "strikeOuts"), getProfileText(stat.path("whip"))));
                }
            }

            // 数値として読める年度を昇順に並べ、解釈できない年度は末尾に配置します。
            seasons.sort(Comparator.comparing(
                    (MlbPlayerPitchingSeason season) -> parseSeasonYear(season.getSeason()),
                    Comparator.nullsLast(Comparator.naturalOrder())));
            return seasons;
        } catch (InterruptedException e) {
            // 割り込み状態を復元し、通信の中断をControllerへ通知します。
            Thread.currentThread().interrupt();
            throw new IllegalStateException("MLB APIへの通信が中断されました。", e);
        } catch (IOException | JacksonException e) {
            // 通信やJSON解析の失敗を画面で扱う例外として通知します。
            throw new IllegalStateException("年度別投手成績の取得に失敗しました。", e);
        }
    }

    /**
     * 並び替え用の年度を返し、整数として解釈できない場合はnullを返します。
     */
    private Integer parseSeasonYear(String season) {
        // 年度の数値変換を試み、不正な年度でも一覧全体をエラーにしないようにします。
        try {
            return Integer.valueOf(season);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 日本時間の当日を基準に満年齢を計算し、未設定・不正形式・未来日はnullを返します。
     */
    private Integer calculateAge(String birthDate) {
        // yyyy-MM-dd形式でない値は、日付解析を行わず未設定として扱います。
        if (birthDate == null || !birthDate.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            return null;
        }
        try {
            // 実在する生年月日を読み取り、日本時間の当日までの満年齢を計算します。
            LocalDate birthday = LocalDate.parse(birthDate);
            LocalDate today = LocalDate.now(JST);
            return birthday.isAfter(today) ? null : Period.between(birthday, today).getYears();
        } catch (DateTimeException e) {
            // 存在しない日付の場合は、プロフィール全体をエラーにせずnullを返します。
            return null;
        }
    }


    /**
     * フィート・インチ形式の身長をcmに換算し、四捨五入した整数を返します。
     * 未設定や不正形式、整数の範囲を超える場合はnullを返します。
     */
    private Integer convertHeightToCm(String height) {
        // 未設定の値を除外し、フィートとインチを表す整数を取り出します。
        if (height == null) {
            return null;
        }
        Matcher matcher = Pattern.compile("^\\s*(\\d+)'\\s*(\\d+)\"\\s*$").matcher(height);
        if (!matcher.matches()) {
            return null;
        }

        try {
            // インチが0〜11であることと、身長が正の値であることを確認します。
            int feet = Integer.parseInt(matcher.group(1));
            int inches = Integer.parseInt(matcher.group(2));
            if (inches > 11 || (feet == 0 && inches == 0)) {
                return null;
            }

            // 総インチをcmへ変換して四捨五入し、Integerに収まる場合だけ返します。
            long heightCm = Math.round((feet * 12.0 + inches) * 2.54);
            return heightCm <= Integer.MAX_VALUE ? (int) heightCm : null;
        } catch (NumberFormatException e) {
            // 数字が大きすぎて読み取れない場合は、変換不可としてnullを返します。
            return null;
        }
    }

    /**
     * lb単位の体重をkgに換算し、四捨五入した整数を返します。
     * 未設定の場合はnullを返します。
     */
    private Integer convertWeightToKg(Integer weightLb) {
        // 未設定の値は保持し、設定された体重をkgに変換して四捨五入します。
        return weightLb == null ? null : (int) Math.round(weightLb * 0.45359237);
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
