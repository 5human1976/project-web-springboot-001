package uechi;

/**
 * 【ソースファイル名】
 * MlbInningScore.java
 *
 * 【概要】
 * MLBの1イニングの番号と両チームの得点を保持するクラスです。
 * 詳細画面でイニング別スコアボードを表示するために使用します。
 *
 * 【作成日】2026-11-17
 * 【最終更新日】2026-11-17
 *
 * @author masa
 * @version 1.0
 */
public class MlbInningScore {

    /** イニング番号。 */
    private final int inning;

    /** ビジターチームの得点。未設定の場合はnull。 */
    private final Integer awayRuns;

    /** ホームチームの得点。未設定の場合はnull。 */
    private final Integer homeRuns;

    /**
     * 1イニングの番号と両チームの得点を受け取って保持します。
     *
     * @param inning イニング番号
     * @param awayRuns ビジターチームの得点。未設定の場合はnull
     * @param homeRuns ホームチームの得点。未設定の場合はnull
     */
    public MlbInningScore(int inning, Integer awayRuns, Integer homeRuns) {
        // 引数で受け取ったイニング番号と両チームの得点を各フィールドに設定します。
        this.inning = inning;
        this.awayRuns = awayRuns;
        this.homeRuns = homeRuns;
    }

    /**
     * イニング番号を返します。
     *
     * @return イニング番号
     */
    public int getInning() {
        // 保持しているイニング番号を返します。
        return inning;
    }

    /**
     * ビジターチームの得点を返します。
     *
     * @return ビジターチームの得点。未設定の場合はnull
     */
    public Integer getAwayRuns() {
        // 保持しているビジターチームの得点を返します。
        return awayRuns;
    }

    /**
     * ホームチームの得点を返します。
     *
     * @return ホームチームの得点。未設定の場合はnull
     */
    public Integer getHomeRuns() {
        // 保持しているホームチームの得点を返します。
        return homeRuns;
    }
}
