package uechi;

/**
 * 【ソースファイル名】
 * MlbPlayerHittingSeason.java
 *
 * 【概要】
 * MLB選手の年度別打撃成績を保持するクラスです。
 * APIから取得した各年度の成績を選手詳細画面へ渡すために使用します。
 *
 * 【作成日】2026-11-24
 * 【最終更新日】2026-11-24
 *
 * @author masa
 * @version 1.0
 */
public class MlbPlayerHittingSeason {

    /** 年度。 */
    private final String season;

    /** 試合数。未設定の場合はnull。 */
    private final Integer gamesPlayed;

    /** 打数。未設定の場合はnull。 */
    private final Integer atBats;

    /** 安打数。未設定の場合はnull。 */
    private final Integer hits;

    /** 本塁打数。未設定の場合はnull。 */
    private final Integer homeRuns;

    /** 打点。未設定の場合はnull。 */
    private final Integer rbi;

    /** 四球数。未設定の場合はnull。 */
    private final Integer baseOnBalls;

    /** 三振数。未設定の場合はnull。 */
    private final Integer strikeOuts;

    /** 盗塁数。未設定の場合はnull。 */
    private final Integer stolenBases;

    /** 打率の表示文字列。 */
    private final String avg;

    /** OPSの表示文字列。 */
    private final String ops;

    /**
     * 1年度分の打撃成績を保持します。
     *
     * @param season 年度
     * @param gamesPlayed 試合数。未設定の場合はnull
     * @param atBats 打数。未設定の場合はnull
     * @param hits 安打数。未設定の場合はnull
     * @param homeRuns 本塁打数。未設定の場合はnull
     * @param rbi 打点。未設定の場合はnull
     * @param baseOnBalls 四球数。未設定の場合はnull
     * @param strikeOuts 三振数。未設定の場合はnull
     * @param stolenBases 盗塁数。未設定の場合はnull
     * @param avg 打率の表示文字列
     * @param ops OPSの表示文字列
     */
    public MlbPlayerHittingSeason(String season, Integer gamesPlayed, Integer atBats, Integer hits,
            Integer homeRuns, Integer rbi, Integer baseOnBalls, Integer strikeOuts,
            Integer stolenBases, String avg, String ops) {
        // 取得済みの年度と成績を各フィールドに設定し、数値の欠損はnullで保持します。
        this.season = season;
        this.gamesPlayed = gamesPlayed;
        this.atBats = atBats;
        this.hits = hits;
        this.homeRuns = homeRuns;
        this.rbi = rbi;
        this.baseOnBalls = baseOnBalls;
        this.strikeOuts = strikeOuts;
        this.stolenBases = stolenBases;
        this.avg = avg;
        this.ops = ops;
    }

    /**
     * 年度を返します。
     *
     * @return 年度
     */
    public String getSeason() {
        // 保持している年度を返します。
        return season;
    }

    /**
     * 試合数を返します。
     *
     * @return 試合数。未設定の場合はnull
     */
    public Integer getGamesPlayed() {
        // 保持している試合数を返します。
        return gamesPlayed;
    }

    /**
     * 打数を返します。
     *
     * @return 打数。未設定の場合はnull
     */
    public Integer getAtBats() {
        // 保持している打数を返します。
        return atBats;
    }

    /**
     * 安打数を返します。
     *
     * @return 安打数。未設定の場合はnull
     */
    public Integer getHits() {
        // 保持している安打数を返します。
        return hits;
    }

    /**
     * 本塁打数を返します。
     *
     * @return 本塁打数。未設定の場合はnull
     */
    public Integer getHomeRuns() {
        // 保持している本塁打数を返します。
        return homeRuns;
    }

    /**
     * 打点を返します。
     *
     * @return 打点。未設定の場合はnull
     */
    public Integer getRbi() {
        // 保持している打点を返します。
        return rbi;
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
     * 三振数を返します。
     *
     * @return 三振数。未設定の場合はnull
     */
    public Integer getStrikeOuts() {
        // 保持している三振数を返します。
        return strikeOuts;
    }

    /**
     * 盗塁数を返します。
     *
     * @return 盗塁数。未設定の場合はnull
     */
    public Integer getStolenBases() {
        // 保持している盗塁数を返します。
        return stolenBases;
    }

    /**
     * 打率の表示文字列を返します。
     *
     * @return 打率の表示文字列
     */
    public String getAvg() {
        // 保持している打率の表示文字列を返します。
        return avg;
    }

    /**
     * OPSの表示文字列を返します。
     *
     * @return OPSの表示文字列
     */
    public String getOps() {
        // 保持しているOPSの表示文字列を返します。
        return ops;
    }
}
