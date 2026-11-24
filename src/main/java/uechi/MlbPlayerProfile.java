package uechi;

/**
 * 【ソースファイル名】
 * MlbPlayerProfile.java
 *
 * 【概要】
 * MLB選手の基本情報を保持するクラスです。
 * APIから取得したプロフィールを選手詳細画面へ渡すために使用します。
 *
 * 【作成日】2026-11-24
 * 【最終更新日】2026-11-24
 *
 * @author masa
 * @version 1.0
 */
public class MlbPlayerProfile {

    /** MLBの選手識別ID。 */
    private final long playerId;

    /** 選手名。 */
    private final String fullName;

    /** 背番号。 */
    private final String primaryNumber;

    /** 現在の所属チーム。 */
    private final String currentTeam;

    /** 主なポジション。 */
    private final String primaryPosition;

    /** 打ち方の表示名。 */
    private final String batSide;

    /** 投げ方の表示名。 */
    private final String pitchHand;

    /** 生年月日。 */
    private final String birthDate;

    /** 現在の満年齢。計算できない場合はnull。 */
    private final Integer age;

    /** 出身地。 */
    private final String birthPlace;

    /** 身長（cm）。変換できない場合はnull。 */
    private final Integer heightCm;

    /** 体重（kg）。未設定の場合はnull。 */
    private final Integer weightKg;

    /** MLBデビュー日。 */
    private final String mlbDebutDate;

    /**
     * 選手詳細画面に表示するプロフィールを保持します。
     *
     * @param playerId MLBの選手識別ID
     * @param fullName 選手名
     * @param primaryNumber 背番号
     * @param currentTeam 現在の所属チーム
     * @param primaryPosition 主なポジション
     * @param batSide 打ち方の表示名
     * @param pitchHand 投げ方の表示名
     * @param birthDate 生年月日
     * @param age 現在の満年齢。計算できない場合はnull
     * @param birthPlace 出身地
     * @param heightCm 身長（cm）。変換できない場合はnull
     * @param weightKg 体重（kg）。未設定の場合はnull
     * @param mlbDebutDate MLBデビュー日
     */
    public MlbPlayerProfile(long playerId, String fullName, String primaryNumber, String currentTeam,
            String primaryPosition, String batSide, String pitchHand, String birthDate, Integer age,
            String birthPlace, Integer heightCm, Integer weightKg, String mlbDebutDate) {
        // 取得・整形済みのプロフィールを各フィールドに設定します。
        this.playerId = playerId;
        this.fullName = fullName;
        this.primaryNumber = primaryNumber;
        this.currentTeam = currentTeam;
        this.primaryPosition = primaryPosition;
        this.batSide = batSide;
        this.pitchHand = pitchHand;
        this.birthDate = birthDate;
        this.age = age;
        this.birthPlace = birthPlace;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.mlbDebutDate = mlbDebutDate;
    }

    /**
     * MLBの選手識別IDを返します。
     *
     * @return MLBの選手識別ID
     */
    public long getPlayerId() {
        // 保持しているMLBの選手識別IDを返します。
        return playerId;
    }

    /**
     * 選手名を返します。
     *
     * @return 選手名
     */
    public String getFullName() {
        // 保持している選手名を返します。
        return fullName;
    }

    /**
     * 背番号を返します。
     *
     * @return 背番号
     */
    public String getPrimaryNumber() {
        // 保持している背番号を返します。
        return primaryNumber;
    }

    /**
     * 現在の所属チームを返します。
     *
     * @return 現在の所属チーム
     */
    public String getCurrentTeam() {
        // 保持している現在の所属チームを返します。
        return currentTeam;
    }

    /**
     * 主なポジションを返します。
     *
     * @return 主なポジション
     */
    public String getPrimaryPosition() {
        // 保持している主なポジションを返します。
        return primaryPosition;
    }

    /**
     * 打ち方の表示名を返します。
     *
     * @return 打ち方の表示名
     */
    public String getBatSide() {
        // 保持している打ち方の表示名を返します。
        return batSide;
    }

    /**
     * 投げ方の表示名を返します。
     *
     * @return 投げ方の表示名
     */
    public String getPitchHand() {
        // 保持している投げ方の表示名を返します。
        return pitchHand;
    }

    /**
     * 生年月日を返します。
     *
     * @return 生年月日
     */
    public String getBirthDate() {
        // 保持している生年月日を返します。
        return birthDate;
    }

    /**
     * 日本時間の当日時点の満年齢を返します。
     *
     * @return 満年齢。計算できない場合はnull
     */
    public Integer getAge() {
        // 保持している満年齢を返します。
        return age;
    }


    /**
     * 出身地を返します。
     *
     * @return 出身地
     */
    public String getBirthPlace() {
        // 保持している出身地を返します。
        return birthPlace;
    }

    /**
     * 身長（cm）を返します。
     *
     * @return 身長（cm）。変換できない場合はnull
     */
    public Integer getHeightCm() {
        // 保持している身長（cm）を返します。
        return heightCm;
    }

    /**
     * 体重（kg）を返します。
     *
     * @return 体重（kg）。未設定の場合はnull
     */
    public Integer getWeightKg() {
        // 保持している体重（kg）を返します。
        return weightKg;
    }

    /**
     * MLBデビュー日を返します。
     *
     * @return MLBデビュー日
     */
    public String getMlbDebutDate() {
        // 保持しているMLBデビュー日を返します。
        return mlbDebutDate;
    }
}
