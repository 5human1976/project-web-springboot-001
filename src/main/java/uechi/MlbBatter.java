package uechi;

/**
 * 【ソースファイル名】
 * MlbBatter.java
 *
 * 【概要】
 * MLBの野手1人分の選手名と打順、交代順、試合別打撃成績を保持するクラスです。
 * 詳細画面で先発野手と途中出場野手を分けて表示するために使用します。
 *
 * 【作成日】2026-11-19
 * 【最終更新日】2026-11-23
 *
 * @author masa
 * @version 1.0
 */
public class MlbBatter {

    /** 1〜9の打順枠。 */
    private final int battingSlot;

    /** 選手名。 */
    private final String playerName;

    /** 途中出場の場合はtrue。 */
    private final boolean substitute;

    /** 同じ打順枠での交代順。先発の場合は0。 */
    private final int substitutionOrder;

    /** 試合での打数。未設定の場合はnull。 */
    private final Integer atBats;

    /** 試合での安打数。未設定の場合はnull。 */
    private final Integer hits;

    /** 試合での本塁打数。未設定の場合はnull。 */
    private final Integer homeRuns;

    /** 試合での四球数。未設定の場合はnull。 */
    private final Integer baseOnBalls;

    /** 試合での盗塁数。未設定の場合はnull。 */
    private final Integer stolenBases;

    /**
     * 野手の選手名と打順、交代順、試合別打撃成績を保持します。
     *
     * @param battingSlot 1〜9の打順枠
     * @param playerName 選手名
     * @param substitute 途中出場の場合はtrue
     * @param substitutionOrder 同じ打順枠での交代順。先発の場合は0
     * @param atBats 試合での打数。未設定の場合はnull
     * @param hits 試合での安打数。未設定の場合はnull
     * @param homeRuns 試合での本塁打数。未設定の場合はnull
     * @param baseOnBalls 試合での四球数。未設定の場合はnull
     * @param stolenBases 試合での盗塁数。未設定の場合はnull
     */
    public MlbBatter(int battingSlot, String playerName, boolean substitute, int substitutionOrder,
            Integer atBats, Integer hits, Integer homeRuns, Integer baseOnBalls, Integer stolenBases) {
        // 引数で受け取った選手名と打順、途中出場の判定、交代順を各フィールドに設定します。
        this.battingSlot = battingSlot;
        this.playerName = playerName;
        this.substitute = substitute;
        this.substitutionOrder = substitutionOrder;

        // 試合別打撃成績を設定し、未設定の項目はnullのまま保持します。
        this.atBats = atBats;
        this.hits = hits;
        this.homeRuns = homeRuns;
        this.baseOnBalls = baseOnBalls;
        this.stolenBases = stolenBases;
    }

    /**
     * 打順枠を返します。
     *
     * @return 1〜9の打順枠
     */
    public int getBattingSlot() {
        // 保持している打順枠を返します。
        return battingSlot;
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
     * 途中出場かどうかを返します。
     *
     * @return 途中出場の場合はtrue、先発の場合はfalse
     */
    public boolean isSubstitute() {
        // 保持している途中出場の判定を返します。
        return substitute;
    }

    /**
     * 同じ打順枠での交代順を返します。
     *
     * @return 交代順。先発の場合は0
     */
    public int getSubstitutionOrder() {
        // 保持している交代順を返します。
        return substitutionOrder;
    }

    /**
     * 試合での打数を返します。
     *
     * @return 打数。未設定の場合はnull
     */
    public Integer getAtBats() {
        // 保持している打数を返します。
        return atBats;
    }

    /**
     * 試合での安打数を返します。
     *
     * @return 安打数。未設定の場合はnull
     */
    public Integer getHits() {
        // 保持している安打数を返します。
        return hits;
    }

    /**
     * 試合での本塁打数を返します。
     *
     * @return 本塁打数。未設定の場合はnull
     */
    public Integer getHomeRuns() {
        // 保持している本塁打数を返します。
        return homeRuns;
    }

    /**
     * 試合での四球数を返します。
     *
     * @return 四球数。未設定の場合はnull
     */
    public Integer getBaseOnBalls() {
        // 保持している四球数を返します。
        return baseOnBalls;
    }

    /**
     * 試合での盗塁数を返します。
     *
     * @return 盗塁数。未設定の場合はnull
     */
    public Integer getStolenBases() {
        // 保持している盗塁数を返します。
        return stolenBases;
    }
}
