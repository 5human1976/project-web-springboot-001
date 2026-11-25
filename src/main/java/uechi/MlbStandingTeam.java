package uechi;

/**
 * 【ソースファイル名】
 * MlbStandingTeam.java
 *
 * 【概要】
 * MLBの1チーム分の地区順位と勝敗を保持し、順位表画面の表示に使用します。
 *
 * 【作成日】2026-11-25
 * 【最終更新日】2026-11-25
 *
 * @author masa
 * @version 1.0
 */
public class MlbStandingTeam {
    private final long teamId;
    private final String teamName;
    private final Integer divisionRank;
    private final Integer wins;
    private final Integer losses;
    private final String winningPercentage;
    private final String gamesBack;

    /**
     * 順位表の表示に使用する情報を受け取って保持します。
     *
     * @param teamId チームID
     * @param teamName チーム名
     * @param divisionRank 地区順位
     * @param wins 勝利数
     * @param losses 敗戦数
     * @param winningPercentage 勝率
     * @param gamesBack ゲーム差
     */
    public MlbStandingTeam(long teamId, String teamName, Integer divisionRank, Integer wins, Integer losses, String winningPercentage, String gamesBack) {
        // 受け取った情報を設定し、リストがある場合は変更できない形で保持します。
        this.teamId = teamId;
        this.teamName = teamName;
        this.divisionRank = divisionRank;
        this.wins = wins;
        this.losses = losses;
        this.winningPercentage = winningPercentage;
        this.gamesBack = gamesBack;
    }

    /**
     * チームIDを返します。
     *
     * @return チームID
     */
    public long getTeamId() {
        // 保持しているチームIDを返します。
        return teamId;
    }

    /**
     * チーム名を返します。
     *
     * @return チーム名
     */
    public String getTeamName() {
        // 保持しているチーム名を返します。
        return teamName;
    }

    /**
     * 地区順位を返します。
     *
     * @return 地区順位。取得できない場合はnull
     */
    public Integer getDivisionRank() {
        // 保持している地区順位を返します。
        return divisionRank;
    }

    /**
     * 勝利数を返します。
     *
     * @return 勝利数。取得できない場合はnull
     */
    public Integer getWins() {
        // 保持している勝利数を返します。
        return wins;
    }

    /**
     * 敗戦数を返します。
     *
     * @return 敗戦数。取得できない場合はnull
     */
    public Integer getLosses() {
        // 保持している敗戦数を返します。
        return losses;
    }

    /**
     * 勝率を返します。
     *
     * @return 勝率
     */
    public String getWinningPercentage() {
        // 保持している勝率を返します。
        return winningPercentage;
    }

    /**
     * ゲーム差を返します。
     *
     * @return ゲーム差
     */
    public String getGamesBack() {
        // 保持しているゲーム差を返します。
        return gamesBack;
    }
}
