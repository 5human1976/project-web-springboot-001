package uechi;

/**
 * 【ソースファイル名】
 * MlbPlayerPitchingSeason.java
 *
 * 【概要】
 * MLB選手の年度別投手成績を保持するクラスです。
 * 投球回などの表記を維持し、各年度の成績を選手詳細画面へ渡します。
 *
 * 【作成日】2026-11-24
 * 【最終更新日】2026-11-24
 *
 * @author masa
 * @version 1.0
 */
public class MlbPlayerPitchingSeason {

    /** 年度。未設定の場合はハイフン。 */
    private final String season;

    /** 登板数。未設定の場合はnull。 */
    private final Integer gamesPlayed;

    /** 先発数。未設定の場合はnull。 */
    private final Integer gamesStarted;

    /** 勝利数。未設定の場合はnull。 */
    private final Integer wins;

    /** 敗戦数。未設定の場合はnull。 */
    private final Integer losses;

    /** セーブ数。未設定の場合はnull。 */
    private final Integer saves;

    /** 防御率の表示文字列。未設定の場合はハイフン。 */
    private final String era;

    /** 野球表記の投球回。未設定の場合はハイフン。 */
    private final String inningsPitched;

    /** 被安打数。未設定の場合はnull。 */
    private final Integer hits;

    /** 自責点。未設定の場合はnull。 */
    private final Integer earnedRuns;

    /** 四球数。未設定の場合はnull。 */
    private final Integer baseOnBalls;

    /** 奪三振数。未設定の場合はnull。 */
    private final Integer strikeOuts;

    /** WHIPの表示文字列。未設定の場合はハイフン。 */
    private final String whip;

    /**
     * 1年度分の投手成績を保持します。
     *
     * @param season 年度。未設定の場合はハイフン
     * @param gamesPlayed 登板数。未設定の場合はnull
     * @param gamesStarted 先発数。未設定の場合はnull
     * @param wins 勝利数。未設定の場合はnull
     * @param losses 敗戦数。未設定の場合はnull
     * @param saves セーブ数。未設定の場合はnull
     * @param era 防御率の表示文字列。未設定の場合はハイフン
     * @param inningsPitched 野球表記の投球回。未設定の場合はハイフン
     * @param hits 被安打数。未設定の場合はnull
     * @param earnedRuns 自責点。未設定の場合はnull
     * @param baseOnBalls 四球数。未設定の場合はnull
     * @param strikeOuts 奪三振数。未設定の場合はnull
     * @param whip WHIPの表示文字列。未設定の場合はハイフン
     */
    public MlbPlayerPitchingSeason(String season, Integer gamesPlayed, Integer gamesStarted,
            Integer wins, Integer losses, Integer saves, String era, String inningsPitched,
            Integer hits, Integer earnedRuns, Integer baseOnBalls, Integer strikeOuts, String whip) {
        // 取得済みの年度と投手成績を各フィールドに設定します。
        this.season = season;
        this.gamesPlayed = gamesPlayed;
        this.gamesStarted = gamesStarted;
        this.wins = wins;
        this.losses = losses;
        this.saves = saves;
        this.era = era;
        this.inningsPitched = inningsPitched;
        this.hits = hits;
        this.earnedRuns = earnedRuns;
        this.baseOnBalls = baseOnBalls;
        this.strikeOuts = strikeOuts;
        this.whip = whip;
    }

    /**
     * 年度を返します。
     *
     * @return 年度。未設定の場合はハイフン
     */
    public String getSeason() {
        // 保持している年度を返します。
        return season;
    }

    /**
     * 登板数を返します。
     *
     * @return 登板数。未設定の場合はnull
     */
    public Integer getGamesPlayed() {
        // 保持している登板数を返します。
        return gamesPlayed;
    }

    /**
     * 先発数を返します。
     *
     * @return 先発数。未設定の場合はnull
     */
    public Integer getGamesStarted() {
        // 保持している先発数を返します。
        return gamesStarted;
    }

    /**
     * 勝利数を返します。
     *
     * @return 勝利数。未設定の場合はnull
     */
    public Integer getWins() {
        // 保持している勝利数を返します。
        return wins;
    }

    /**
     * 敗戦数を返します。
     *
     * @return 敗戦数。未設定の場合はnull
     */
    public Integer getLosses() {
        // 保持している敗戦数を返します。
        return losses;
    }

    /**
     * セーブ数を返します。
     *
     * @return セーブ数。未設定の場合はnull
     */
    public Integer getSaves() {
        // 保持しているセーブ数を返します。
        return saves;
    }

    /**
     * 防御率の表示文字列を返します。
     *
     * @return 防御率の表示文字列。未設定の場合はハイフン
     */
    public String getEra() {
        // 保持している防御率の表示文字列を返します。
        return era;
    }

    /**
     * 野球表記の投球回を返します。
     *
     * @return 野球表記の投球回。未設定の場合はハイフン
     */
    public String getInningsPitched() {
        // 保持している野球表記の投球回を返します。
        return inningsPitched;
    }

    /**
     * 被安打数を返します。
     *
     * @return 被安打数。未設定の場合はnull
     */
    public Integer getHits() {
        // 保持している被安打数を返します。
        return hits;
    }

    /**
     * 自責点を返します。
     *
     * @return 自責点。未設定の場合はnull
     */
    public Integer getEarnedRuns() {
        // 保持している自責点を返します。
        return earnedRuns;
    }

    /**
     * 四球数を返します。
     *
     * @return 四球数。未設定の場合はnull
     */
    public Integer getBaseOnBalls() {
        // 保持している四球数を返します。
        return baseOnBalls;
    }

    /**
     * 奪三振数を返します。
     *
     * @return 奪三振数。未設定の場合はnull
     */
    public Integer getStrikeOuts() {
        // 保持している奪三振数を返します。
        return strikeOuts;
    }

    /**
     * WHIPの表示文字列を返します。
     *
     * @return WHIPの表示文字列。未設定の場合はハイフン
     */
    public String getWhip() {
        // 保持しているWHIPの表示文字列を返します。
        return whip;
    }
}
