package uechi;

/**
 * 【ソースファイル名】
 * MlbTeamProfile.java
 *
 * 【概要】
 * チーム基本情報を保持し、チーム詳細画面の表示に使用します。
 *
 * 【作成日】2026-11-25
 * 【最終更新日】2026-11-25
 *
 * @author masa
 * @version 1.0
 */
public class MlbTeamProfile {
    private final long teamId;
    private final String name;
    private final String abbreviation;
    private final String league;
    private final String division;
    private final String venue;

    /**
     * 表示に使用する情報を受け取って保持します。
     *
     * @param teamId チームID
     * @param name チーム名
     * @param abbreviation 略称
     * @param league リーグ
     * @param division 地区
     * @param venue 本拠地球場
     */
    public MlbTeamProfile(long teamId, String name, String abbreviation, String league, String division, String venue) {
        // 受け取った情報を各フィールドに設定します。
        this.teamId = teamId;
        this.name = name;
        this.abbreviation = abbreviation;
        this.league = league;
        this.division = division;
        this.venue = venue;
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
    public String getName() {
        // 保持しているチーム名を返します。
        return name;
    }

    /**
     * 略称を返します。
     *
     * @return 略称
     */
    public String getAbbreviation() {
        // 保持している略称を返します。
        return abbreviation;
    }

    /**
     * リーグを返します。
     *
     * @return リーグ
     */
    public String getLeague() {
        // 保持しているリーグを返します。
        return league;
    }

    /**
     * 地区を返します。
     *
     * @return 地区
     */
    public String getDivision() {
        // 保持している地区を返します。
        return division;
    }

    /**
     * 本拠地球場を返します。
     *
     * @return 本拠地球場
     */
    public String getVenue() {
        // 保持している本拠地球場を返します。
        return venue;
    }
}
