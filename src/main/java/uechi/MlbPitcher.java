package uechi;

/**
 * 【ソースファイル名】
 * MlbPitcher.java
 *
 * 【概要】
 * MLBの投手1人分の選手名と、その試合の投球成績を保持するクラスです。
 * 登板区分ごとの投手一覧に投球回と被安打、失点などを表示するために使用します。
 *
 * 【作成日】2026-11-23
 * 【最終更新日】2026-11-23
 *
 * @author masa
 * @version 1.0
 */
public class MlbPitcher {

    /** 選手名。 */
    private final String playerName;

    /** 野球独自の表記による投球回。未設定の場合はハイフン。 */
    private final String inningsPitched;

    /** 被安打。未設定の場合はnull。 */
    private final Integer hits;

    /** 失点。未設定の場合はnull。 */
    private final Integer runs;

    /** 自責点。未設定の場合はnull。 */
    private final Integer earnedRuns;

    /** 四球。未設定の場合はnull。 */
    private final Integer baseOnBalls;

    /** 奪三振。未設定の場合はnull。 */
    private final Integer strikeOuts;

    /**
     * 投手名と試合別の投球成績を受け取って保持します。
     *
     * @param playerName 選手名
     * @param inningsPitched 野球独自の表記による投球回。未設定の場合はハイフン
     * @param hits 被安打。未設定の場合はnull
     * @param runs 失点。未設定の場合はnull
     * @param earnedRuns 自責点。未設定の場合はnull
     * @param baseOnBalls 四球。未設定の場合はnull
     * @param strikeOuts 奪三振。未設定の場合はnull
     */
    public MlbPitcher(String playerName, String inningsPitched, Integer hits, Integer runs,
            Integer earnedRuns, Integer baseOnBalls, Integer strikeOuts) {
        // 引数で受け取った投手名と投球成績を各フィールドに設定します。
        this.playerName = playerName;
        this.inningsPitched = inningsPitched;
        this.hits = hits;
        this.runs = runs;
        this.earnedRuns = earnedRuns;
        this.baseOnBalls = baseOnBalls;
        this.strikeOuts = strikeOuts;
    }

    /**
     * 選手名を返します。
     *
     * @return 選手名
     */
    public String getPlayerName() {
        // 保持している選手名を返します。
        return playerName;
    }

    /**
     * 投球回の文字列を返します。
     *
     * @return 野球独自の表記による投球回。未設定の場合はハイフン
     */
    public String getInningsPitched() {
        // 保持している投球回の文字列を返します。
        return inningsPitched;
    }

    /**
     * 被安打を返します。
     *
     * @return 被安打。未設定の場合はnull
     */
    public Integer getHits() {
        // 保持している被安打を返します。
        return hits;
    }

    /**
     * 失点を返します。
     *
     * @return 失点。未設定の場合はnull
     */
    public Integer getRuns() {
        // 保持している失点を返します。
        return runs;
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
     * 四球を返します。
     *
     * @return 四球。未設定の場合はnull
     */
    public Integer getBaseOnBalls() {
        // 保持している四球を返します。
        return baseOnBalls;
    }

    /**
     * 奪三振を返します。
     *
     * @return 奪三振。未設定の場合はnull
     */
    public Integer getStrikeOuts() {
        // 保持している奪三振を返します。
        return strikeOuts;
    }
}
