package uechi;

/**
 * 【ソースファイル名】
 * MlbTeamRosterPlayer.java
 *
 * 【概要】
 * 年度別ロスターの選手情報を保持し、選手一覧の表示に使用します。
 *
 * 【作成日】2026-11-25
 * 【最終更新日】2026-11-25
 *
 * @author masa
 * @version 1.0
 */
public class MlbTeamRosterPlayer {
    private final Long playerId;
    private final String playerName;
    private final String jerseyNumber;
    private final String position;

    /**
     * 表示に使用する情報を受け取って保持します。
     *
     * @param playerId 選手ID。取得できない場合はnull
     * @param playerName 選手名
     * @param jerseyNumber 背番号
     * @param position ポジション
     */
    public MlbTeamRosterPlayer(Long playerId, String playerName, String jerseyNumber, String position) {
        // 受け取った情報を各フィールドに設定します。
        this.playerId = playerId;
        this.playerName = playerName;
        this.jerseyNumber = jerseyNumber;
        this.position = position;
    }

    /**
     * 選手IDを返します。
     *
     * @return 選手ID。取得できない場合はnull
     */
    public Long getPlayerId() {
        // 保持している選手IDを返します。
        return playerId;
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
     * 背番号を返します。
     *
     * @return 背番号
     */
    public String getJerseyNumber() {
        // 保持している背番号を返します。
        return jerseyNumber;
    }

    /**
     * ポジションを返します。
     *
     * @return ポジション
     */
    public String getPosition() {
        // 保持しているポジションを返します。
        return position;
    }
}
