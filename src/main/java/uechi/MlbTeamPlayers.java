package uechi;

import java.util.List;

/**
 * 【ソースファイル名】
 * MlbTeamPlayers.java
 *
 * 【概要】
 * MLBの1チーム分の出場選手名を保持するクラスです。
 * 打順と交代順で分けた野手、登板順で分けた投手を詳細画面に表示するために使用します。
 *
 * 【作成日】2026-11-19
 * 【最終更新日】2026-11-19
 *
 * @author masa
 * @version 1.0
 */
public class MlbTeamPlayers {

    /** 打順枠の昇順で保持する先発野手。 */
    private final List<MlbBatter> startingBatters;

    /** 打順枠と交代順の昇順で保持する途中出場野手。 */
    private final List<MlbBatter> substituteBatters;

    /** 先発投手名。取得できない場合はハイフン。 */
    private final String startingPitcher;

    /** 登板順で保持する中継ぎ投手名。 */
    private final List<String> reliefPitchers;

    /** 最終登板投手名。先発のみの場合や取得できない場合はハイフン。 */
    private final String finalPitcher;

    /**
     * 1チーム分の先発野手、途中出場野手と投手の名前を受け取って保持します。
     *
     * @param startingBatters 打順枠の昇順で並んだ先発野手
     * @param substituteBatters 打順枠と交代順の昇順で並んだ途中出場野手
     * @param startingPitcher 先発投手名。取得できない場合はハイフン
     * @param reliefPitchers 登板順で並んだ中継ぎ投手名
     * @param finalPitcher 最終登板投手名。該当しない場合はハイフン
     */
    public MlbTeamPlayers(List<MlbBatter> startingBatters, List<MlbBatter> substituteBatters, String startingPitcher,
            List<String> reliefPitchers, String finalPitcher) {
        // 選手名のリストを変更できない形で保持し、先発と最終登板投手の名前を設定します。
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
     * 先発投手名を返します。
     *
     * @return 先発投手名。取得できない場合はハイフン
     */
    public String getStartingPitcher() {
        // 保持している先発投手名を返します。
        return startingPitcher;
    }

    /**
     * 登板順で並んだ中継ぎ投手名を返します。
     *
     * @return 先発と最終登板を除く投手名。該当者がいない場合は空リスト
     */
    public List<String> getReliefPitchers() {
        // 保持している中継ぎ投手名のリストを返します。
        return reliefPitchers;
    }

    /**
     * その試合で最後に登板した投手名を返します。
     *
     * @return 最終登板投手名。先発のみの場合や取得できない場合はハイフン
     */
    public String getFinalPitcher() {
        // 保持している最終登板投手名を返します。
        return finalPitcher;
    }
}
