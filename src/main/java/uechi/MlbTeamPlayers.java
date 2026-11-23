package uechi;

import java.util.List;

/**
 * 【ソースファイル名】
 * MlbTeamPlayers.java
 *
 * 【概要】
 * MLBの1チーム分の野手情報と投手の試合別成績を保持するクラスです。
 * 打順と交代順で分けた野手、登板順で分けた投手を詳細画面に表示するために使用します。
 *
 * 【作成日】2026-11-19
 * 【最終更新日】2026-11-23
 *
 * @author masa
 * @version 1.0
 */
public class MlbTeamPlayers {

    /** 打順枠の昇順で保持する先発野手。 */
    private final List<MlbBatter> startingBatters;

    /** 打順枠と交代順の昇順で保持する途中出場野手。 */
    private final List<MlbBatter> substituteBatters;

    /** 先発投手の情報。取得できない場合はnull。 */
    private final MlbPitcher startingPitcher;

    /** 登板順で保持する中継ぎ投手の情報。 */
    private final List<MlbPitcher> reliefPitchers;

    /** 最終登板投手の情報。先発のみの場合や取得できない場合はnull。 */
    private final MlbPitcher finalPitcher;

    /**
     * 1チーム分の先発野手、途中出場野手と投手の試合別成績を受け取って保持します。
     *
     * @param startingBatters 打順枠の昇順で並んだ先発野手
     * @param substituteBatters 打順枠と交代順の昇順で並んだ途中出場野手
     * @param startingPitcher 先発投手の情報。取得できない場合はnull
     * @param reliefPitchers 登板順で並んだ中継ぎ投手の情報
     * @param finalPitcher 最終登板投手の情報。該当しない場合はnull
     */
    public MlbTeamPlayers(List<MlbBatter> startingBatters, List<MlbBatter> substituteBatters, MlbPitcher startingPitcher,
            List<MlbPitcher> reliefPitchers, MlbPitcher finalPitcher) {
        // 野手と中継ぎ投手のリストを変更できない形で保持し、先発と最終登板投手の情報を設定します。
        this.startingBatters = List.copyOf(startingBatters);
        this.substituteBatters = List.copyOf(substituteBatters);
        this.startingPitcher = startingPitcher;
        this.reliefPitchers = List.copyOf(reliefPitchers);
        this.finalPitcher = finalPitcher;
    }

    /**
     * 打順枠の昇順で並んだ先発野手を返します。
     *
     * @return 先発野手。該当者がいない場合は空リスト
     */
    public List<MlbBatter> getStartingBatters() {
        // 保持している先発野手のリストを返します。
        return startingBatters;
    }

    /**
     * 打順枠と交代順の昇順で並んだ途中出場野手を返します。
     *
     * @return 途中出場野手。該当者がいない場合は空リスト
     */
    public List<MlbBatter> getSubstituteBatters() {
        // 保持している途中出場野手のリストを返します。
        return substituteBatters;
    }

    /**
     * 先発投手の名前と試合別成績を返します。
     *
     * @return 先発投手の情報。取得できない場合はnull
     */
    public MlbPitcher getStartingPitcher() {
        // 保持している先発投手の情報を返します。
        return startingPitcher;
    }

    /**
     * 登板順で並んだ中継ぎ投手の名前と試合別成績を返します。
     *
     * @return 先発と最終登板を除く投手の情報。該当者がいない場合は空リスト
     */
    public List<MlbPitcher> getReliefPitchers() {
        // 保持している中継ぎ投手のリストを返します。
        return reliefPitchers;
    }

    /**
     * その試合で最後に登板した投手の名前と試合別成績を返します。
     *
     * @return 最終登板投手の情報。先発のみの場合や取得できない場合はnull
     */
    public MlbPitcher getFinalPitcher() {
        // 保持している最終登板投手の情報を返します。
        return finalPitcher;
    }
}
