package uechi;

import java.util.List;

/**
 * 【ソースファイル名】
 * MlbGameDetail.java
 *
 * 【概要】
 * MLBの1試合の基本詳細情報を保持するクラスです。
 * Serviceが取得した基本情報とイニング別得点、R・H・Eを詳細画面へ渡すために使用します。
 *
 * 【作成日】2026-11-17
 * 【最終更新日】2026-11-17
 *
 * @author masa
 * @version 1.0
 */
public class MlbGameDetail {

    /** MLB APIで試合を識別するID。 */
    private final long gamePk;

    /** 日本時間での試合開始日時。 */
    private final String gameDateJst;

    /** ビジターチーム名。 */
    private final String awayTeam;

    /** ビジターチームの得点。未設定の場合はnull。 */
    private final Integer awayScore;

    /** ホームチーム名。 */
    private final String homeTeam;

    /** ホームチームの得点。未設定の場合はnull。 */
    private final Integer homeScore;

    /** 試合の進行状況。 */
    private final String status;

    /** 試合会場名。 */
    private final String venue;

    /** APIの順序で保持するイニング別得点。 */
    private final List<MlbInningScore> innings;

    /** ビジターチームの安打数。未設定の場合はnull。 */
    private final Integer awayHits;

    /** ビジターチームの失策数。未設定の場合はnull。 */
    private final Integer awayErrors;

    /** ホームチームの安打数。未設定の場合はnull。 */
    private final Integer homeHits;

    /** ホームチームの失策数。未設定の場合はnull。 */
    private final Integer homeErrors;

    /** 勝利投手名。 */
    private final String winningPitcher;

    /** 敗戦投手名。 */
    private final String losingPitcher;

    /** セーブ投手名。 */
    private final String savePitcher;

    /**
     * 詳細画面で使用する1試合の基本情報を受け取って保持します。
     *
     * @param gamePk MLB APIで試合を識別するID
     * @param gameDateJst 日本時間での試合開始日時
     * @param awayTeam ビジターチーム名
     * @param awayScore ビジターチームの得点。未設定の場合はnull
     * @param homeTeam ホームチーム名
     * @param homeScore ホームチームの得点。未設定の場合はnull
     * @param status 試合の進行状況
     * @param venue 試合会場名
     * @param innings APIの順序で並んだイニング別得点。未開始の場合は空リスト
     * @param awayHits ビジターチームの安打数。未設定の場合はnull
     * @param awayErrors ビジターチームの失策数。未設定の場合はnull
     * @param homeHits ホームチームの安打数。未設定の場合はnull
     * @param homeErrors ホームチームの失策数。未設定の場合はnull
     * @param winningPitcher 勝利投手名
     * @param losingPitcher 敗戦投手名
     * @param savePitcher セーブ投手名
     */
    public MlbGameDetail(long gamePk, String gameDateJst, String awayTeam, Integer awayScore,
            String homeTeam, Integer homeScore, String status, String venue,
            List<MlbInningScore> innings, Integer awayHits, Integer awayErrors,
            Integer homeHits, Integer homeErrors,
            String winningPitcher, String losingPitcher, String savePitcher) {
        // 引数で受け取った試合の基本情報を各フィールドに設定します。
        this.gamePk = gamePk;
        this.gameDateJst = gameDateJst;
        this.awayTeam = awayTeam;
        this.awayScore = awayScore;
        this.homeTeam = homeTeam;
        this.homeScore = homeScore;
        this.status = status;
        this.venue = venue;

        // イニング別得点を変更できないリストとして保持し、安打数と失策数を設定します。
        this.innings = List.copyOf(innings);
        this.awayHits = awayHits;
        this.awayErrors = awayErrors;
        this.homeHits = homeHits;
        this.homeErrors = homeErrors;
        this.winningPitcher = winningPitcher;
        this.losingPitcher = losingPitcher;
        this.savePitcher = savePitcher;
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
     * ビジターチームの得点を返します。
     *
     * @return ビジターチームの得点。未設定の場合はnull
     */
    public Integer getAwayScore() {
        // 保持しているビジターチームの得点を返します。
        return awayScore;
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
     * ホームチームの得点を返します。
     *
     * @return ホームチームの得点。未設定の場合はnull
     */
    public Integer getHomeScore() {
        // 保持しているホームチームの得点を返します。
        return homeScore;
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
     * APIの順序で並んだイニング別得点を返します。
     *
     * @return イニング別得点。未開始の場合は空リスト
     */
    public List<MlbInningScore> getInnings() {
        // 保持しているイニング別得点のリストを返します。
        return innings;
    }

    /**
     * ビジターチームの安打数を返します。
     *
     * @return ビジターチームの安打数。未設定の場合はnull
     */
    public Integer getAwayHits() {
        // 保持しているビジターチームの安打数を返します。
        return awayHits;
    }

    /**
     * ビジターチームの失策数を返します。
     *
     * @return ビジターチームの失策数。未設定の場合はnull
     */
    public Integer getAwayErrors() {
        // 保持しているビジターチームの失策数を返します。
        return awayErrors;
    }

    /**
     * ホームチームの安打数を返します。
     *
     * @return ホームチームの安打数。未設定の場合はnull
     */
    public Integer getHomeHits() {
        // 保持しているホームチームの安打数を返します。
        return homeHits;
    }

    /**
     * ホームチームの失策数を返します。
     *
     * @return ホームチームの失策数。未設定の場合はnull
     */
    public Integer getHomeErrors() {
        // 保持しているホームチームの失策数を返します。
        return homeErrors;
    }

    public String getWinningPitcher() {
        return winningPitcher;
    }

    public String getLosingPitcher() {
        return losingPitcher;
    }

    public String getSavePitcher() {
        return savePitcher;
    }
}
