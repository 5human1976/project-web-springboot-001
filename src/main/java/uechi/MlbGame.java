package uechi;

/**
 * 【ソースファイル名】
 * MlbGame.java
 *
 * 【概要】
 * MLBの試合1件分の情報を保持するクラスです。
 * Serviceが取得した試合情報をControllerや画面へ渡すために使用します。
 *
 * 【作成日】2026-10-27
 * 【最終更新日】2026-10-27
 *
 * @author masa
 * @version 1.0
 */
public class MlbGame {

    /** MLB APIで試合を識別するID。 */
    private final long gamePk;

    /** UTCでの試合開始日時。 */
    private final String gameDate;

    /** 日本時間での試合開始日時。 */
    private final String gameDateJst;

    /** ビジターチーム名。 */
    private final String awayTeam;

    /** ホームチーム名。 */
    private final String homeTeam;

    /** 試合の進行状況。 */
    private final String status;

    /** 試合会場名。 */
    private final String venue;

    /** シリーズの説明。 */
    private final String seriesDescription;

    /** シリーズ内の試合番号。未設定の場合はnull。 */
    private final Integer seriesGameNumber;

    /**
     * 試合1件分の情報を受け取って保持します。
     *
     * @param gamePk MLB APIで試合を識別するID
     * @param gameDate UTCでの試合開始日時
     * @param gameDateJst 日本時間での試合開始日時
     * @param awayTeam ビジターチーム名
     * @param homeTeam ホームチーム名
     * @param status 試合の進行状況
     * @param venue 試合会場名
     * @param seriesDescription シリーズの説明
     * @param seriesGameNumber シリーズ内の試合番号。未設定の場合はnull
     */
    public MlbGame(long gamePk, String gameDate, String gameDateJst,
            String awayTeam, String homeTeam, String status, String venue,
            String seriesDescription, Integer seriesGameNumber) {
        // 引数で受け取った試合情報を各フィールドに設定します。
        this.gamePk = gamePk;
        this.gameDate = gameDate;
        this.gameDateJst = gameDateJst;
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        this.status = status;
        this.venue = venue;
        this.seriesDescription = seriesDescription;
        this.seriesGameNumber = seriesGameNumber;
    }

    /**
     * MLB APIで試合を識別するIDを返します。
     *
     * @return MLB APIで試合を識別するID
     */
    public long getGamePk() {
        // 保持しているMLB APIで試合を識別するIDを返します。
        return gamePk;
    }

    /**
     * UTCでの試合開始日時を返します。
     *
     * @return UTCでの試合開始日時
     */
    public String getGameDate() {
        // 保持しているUTCでの試合開始日時を返します。
        return gameDate;
    }

    /**
     * 日本時間での試合開始日時を返します。
     *
     * @return 日本時間での試合開始日時
     */
    public String getGameDateJst() {
        // 保持している日本時間での試合開始日時を返します。
        return gameDateJst;
    }

    /**
     * ビジターチーム名を返します。
     *
     * @return ビジターチーム名
     */
    public String getAwayTeam() {
        // 保持しているビジターチーム名を返します。
        return awayTeam;
    }

    /**
     * ホームチーム名を返します。
     *
     * @return ホームチーム名
     */
    public String getHomeTeam() {
        // 保持しているホームチーム名を返します。
        return homeTeam;
    }

    /**
     * 試合の進行状況を返します。
     *
     * @return 試合の進行状況
     */
    public String getStatus() {
        // 保持している試合の進行状況を返します。
        return status;
    }

    /**
     * 試合会場名を返します。
     *
     * @return 試合会場名
     */
    public String getVenue() {
        // 保持している試合会場名を返します。
        return venue;
    }

    /**
     * シリーズの説明を返します。
     *
     * @return シリーズの説明
     */
    public String getSeriesDescription() {
        // 保持しているシリーズの説明を返します。
        return seriesDescription;
    }

    /**
     * シリーズ内の試合番号を返します。
     *
     * @return シリーズ内の試合番号。未設定の場合はnull
     */
    public Integer getSeriesGameNumber() {
        // 保持しているシリーズ内の試合番号を返します。
        return seriesGameNumber;
    }
}
