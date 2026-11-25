package uechi;

import java.util.List;

/**
 * 【ソースファイル名】
 * MlbStandingDivision.java
 *
 * 【概要】
 * MLBの1地区分の順位表を保持し、リーグ・地区別の表示に使用します。
 *
 * 【作成日】2026-11-25
 * 【最終更新日】2026-11-25
 *
 * @author masa
 * @version 1.0
 */
public class MlbStandingDivision {
    private final int leagueId;
    private final int divisionId;
    private final String divisionName;
    private final List<MlbStandingTeam> teams;

    /**
     * 順位表の表示に使用する情報を受け取って保持します。
     *
     * @param leagueId リーグID
     * @param divisionId 地区ID
     * @param divisionName 地区名
     * @param teams 順位順のチーム一覧
     */
    public MlbStandingDivision(int leagueId, int divisionId, String divisionName, List<MlbStandingTeam> teams) {
        // 受け取った情報を設定し、リストがある場合は変更できない形で保持します。
        this.leagueId = leagueId;
        this.divisionId = divisionId;
        this.divisionName = divisionName;
        this.teams = List.copyOf(teams);
    }

    /**
     * リーグIDを返します。
     *
     * @return リーグID
     */
    public int getLeagueId() {
        // 保持しているリーグIDを返します。
        return leagueId;
    }

    /**
     * 地区IDを返します。
     *
     * @return 地区ID
     */
    public int getDivisionId() {
        // 保持している地区IDを返します。
        return divisionId;
    }

    /**
     * 地区名を返します。
     *
     * @return 地区名
     */
    public String getDivisionName() {
        // 保持している地区名を返します。
        return divisionName;
    }

    /**
     * 順位順のチーム一覧を返します。
     *
     * @return 順位順のチーム一覧。外部から変更できないリスト
     */
    public List<MlbStandingTeam> getTeams() {
        // 保持している順位順のチーム一覧を返します。
        return teams;
    }
}
