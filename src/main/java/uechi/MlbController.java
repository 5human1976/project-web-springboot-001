package uechi;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 【ソースファイル名】
 * MlbController.java
 *
 * 【概要】
 * /mlbへのGETリクエストに対してMLB試合情報の画面を表示するクラスです。
 * 対象日の試合一覧や指定された試合の詳細情報を取得し、Thymeleafの画面に渡します。
 * 選手詳細画面にはプロフィールと元のチームや試合へ戻るための情報を渡します。
 *
 * 【作成日】2026-10-21
 * 【最終更新日】2026-11-25
 *
 * @author masa
 * @version 1.0
 */
@Controller
public class MlbController {

    // MLB試合情報の取得を担当するServiceを保持します。
    private final MlbApiService mlbApiService;

    /**
     * 試合情報の取得に使用するServiceを受け取ります。
     *
     * @param mlbApiService Springが注入するMLB APIのService
     */
    public MlbController(MlbApiService mlbApiService) {
        // Springから渡されたServiceをフィールドに設定します。
        this.mlbApiService = mlbApiService;
    }

    /**
     * 対象日と試合の取得結果を画面に渡し、MLB試合情報のビュー名を返します。
     *
     * @param targetDate 指定された対象日。未指定の場合はnull
     * @param model 画面に渡すデータを保持するモデル
     * @return MLB試合情報のテンプレートを指定するビュー名
     */
    @GetMapping("/mlb")
    public String mlb(
            @RequestParam(name = "targetDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate,
            Model model) {
        // 受け取った対象日をモデルに設定し、入力欄と対象日の表示に使用します。
        model.addAttribute("targetDate", targetDate);

        // 日付が指定された場合だけServiceを呼び出し、初回表示時のAPI通信を回避します。
        if (targetDate != null) {
            try {
                // 日本時間で対象日に開始する試合一覧と件数をモデルに設定します。
                List<MlbGame> games = mlbApiService.getGames(targetDate);
                model.addAttribute("games", games);
                model.addAttribute("gameCount", games.size());
            } catch (IllegalStateException e) {
                // 取得に失敗した場合は画面に表示するメッセージを設定します。
                model.addAttribute("errorMessage", "MLB試合情報の取得に失敗しました。");
            }
        }

        // Thymeleafでtemplates/mlb.htmlを表示するためのビュー名を返します。
        return "mlb";
    }

    /**
     * 指定された試合の詳細情報と一覧へ戻るための対象日を画面に渡します。
     *
     * @param gamePk URLのパスに含まれるMLBの試合識別ID
     * @param targetDate 一覧へ戻る際の検索対象日。未指定の場合はnull
     * @param model 画面に渡すデータを保持するモデル
     * @return 試合詳細のテンプレートを指定するビュー名
     */
    @GetMapping("/mlb/game/{gamePk}")
    public String detail(
            @PathVariable("gamePk") long gamePk,
            @RequestParam(name = "targetDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate,
            Model model) {
        // 一覧へ戻る際の対象日を、詳細取得の成否にかかわらず画面へ渡します。
        model.addAttribute("targetDate", targetDate);

        try {
            // URLの試合IDをServiceへ渡し、取得した詳細情報をモデルに設定します。
            MlbGameDetail gameDetail = mlbApiService.getGameDetail(gamePk);
            model.addAttribute("gameDetail", gameDetail);
        } catch (IllegalStateException e) {
            // 取得に失敗した場合は試合IDとエラーメッセージを詳細画面へ渡します。
            model.addAttribute("gamePk", gamePk);
            model.addAttribute("errorMessage", "MLB試合詳細情報の取得に失敗しました。");
        }

        // Thymeleafでtemplates/mlb-detail.htmlを表示するためのビュー名を返します。
        return "mlb-detail";
    }

    /**
     * 選手プロフィールと元のチームや試合へ戻るための情報を選手詳細画面に渡します。
     *
     * @param playerId URLのパスに含まれるMLBの選手識別ID
     * @param teamId 戻り先のチームID。未指定の場合はnull
     * @param season チーム詳細で選択した年度。未指定の場合はnull
     * @param gamePk 戻り先の試合ID。未指定の場合はnull
     * @param targetDate 一覧へ戻る際の検索対象日。未指定の場合はnull
     * @param model 画面に渡すデータを保持するモデル
     * @return 選手詳細のテンプレートを指定するビュー名
     */
    @GetMapping("/mlb/player/{playerId}")
    public String player(
            @PathVariable("playerId") long playerId,
            @RequestParam(name = "teamId", required = false) Long teamId,
            @RequestParam(name = "season", required = false) Integer season,
            @RequestParam(name = "gamePk", required = false) Long gamePk,
            @RequestParam(name = "targetDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate,
            Model model) {
        // 選手IDと戻り先のチームID、選択年度、試合ID、検索対象日を画面へ渡します。
        model.addAttribute("playerId", playerId);
        model.addAttribute("teamId", teamId);
        model.addAttribute("season", season);
        model.addAttribute("gamePk", gamePk);
        model.addAttribute("targetDate", targetDate);

        try {
            // 選手IDでプロフィールを取得し、選手詳細画面に渡します。
            MlbPlayerProfile playerProfile = mlbApiService.getPlayerProfile(playerId);
            model.addAttribute("playerProfile", playerProfile);
        } catch (IllegalStateException e) {
            // 取得失敗を画面に表示し、設定済みの戻り先情報を保持します。
            model.addAttribute("errorMessage", "MLB選手プロフィールの取得に失敗しました。");
        }

        try {
            // プロフィールとは独立して年度別打撃成績を取得し、0件も正常結果として渡します。
            List<MlbPlayerHittingSeason> hittingSeasons =
                    mlbApiService.getPlayerHittingYearByYear(playerId);
            model.addAttribute("hittingSeasons", hittingSeasons);
        } catch (IllegalStateException e) {
            // 成績だけの取得失敗を表示し、プロフィールと戻り先情報を保持します。
            model.addAttribute("hittingErrorMessage", "年度別打撃成績の取得に失敗しました。");
        }


        try {
            // 年度別投手成績を独立して取得し、投手成績のない選手も正常結果として扱います。
            List<MlbPlayerPitchingSeason> pitchingSeasons =
                    mlbApiService.getPlayerPitchingYearByYear(playerId);
            model.addAttribute("pitchingSeasons", pitchingSeasons);
        } catch (IllegalStateException e) {
            // 投手成績だけの取得失敗を表示し、他の成績と戻り先情報を保持します。
            model.addAttribute("pitchingErrorMessage", "年度別投手成績の取得に失敗しました。");
        }


        // プロフィール、年度別成績、エラーメッセージを表示するビュー名を返します。
        return "mlb-player";
    }

    /**
     * チーム基本情報と指定年度の選手一覧、戻り先を画面に渡します。
     *
     * @param teamId URLのチーム識別ID
     * @param season 指定年度。未指定の場合は対象日または現在年を使用
     * @param gamePk 戻り先の試合ID
     * @param targetDate 一覧へ戻る際の検索対象日
     * @param model 画面に渡すモデル
     * @return チーム詳細のビュー名
     */
    @GetMapping("/mlb/team/{teamId}")
    public String team(
            @PathVariable("teamId") long teamId,
            @RequestParam(name = "season", required = false) String season,
            @RequestParam(name = "gamePk", required = false) Long gamePk,
            @RequestParam(name = "targetDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate,
            Model model) {
        // 取得の成否によらず戻り先と年度入力の値を保持します。
        int currentYear = LocalDate.now(ZoneId.of("Asia/Tokyo")).getYear();
        String selectedSeason = season != null ? season
                : String.valueOf(targetDate != null ? targetDate.getYear() : currentYear);
        model.addAttribute("teamId", teamId);
        model.addAttribute("gamePk", gamePk);
        model.addAttribute("targetDate", targetDate);
        model.addAttribute("season", selectedSeason);
        model.addAttribute("currentYear", currentYear);

        try {
            // ロスター取得とは独立してチーム基本情報を設定します。
            model.addAttribute("teamProfile", mlbApiService.getTeamProfile(teamId));
        } catch (IllegalStateException e) {
            // 基本情報だけの取得失敗を表示します。
            model.addAttribute("errorMessage", "チーム基本情報の取得に失敗しました。");
        }

        try {
            // 明示された年度を優先し、整数の範囲を確認してから一覧を取得します。
            int year = Integer.parseInt(selectedSeason);
            if (year < 1876 || year > currentYear) {
                throw new NumberFormatException("年度が範囲外です。");
            }
            model.addAttribute("rosterPlayers", mlbApiService.getTeamRoster(teamId, year));
        } catch (NumberFormatException e) {
            // 不正な入力を画面に表示し、プロフィールと戻り先を保持します。
            model.addAttribute("rosterErrorMessage", "年度は1876年から" + currentYear + "年までの整数で指定してください。");
        } catch (IllegalStateException e) {
            // ロスターのみ失敗した場合も取得済みのプロフィールを保持します。
            model.addAttribute("rosterErrorMessage", "年度別選手一覧の取得に失敗しました。");
        }

        // 基本情報と年度別選手一覧を表示するビュー名を返します。
        return "mlb-team";
    }


    /**
     * 2025年のレギュラーシーズン順位表を画面に渡します。
     *
     * @param model 画面に渡すデータを保持するモデル
     * @return 順位表のテンプレートを指定するビュー名
     */
    @GetMapping("/mlb/standings")
    public String standings(Model model) {
        // 今回は2025年を固定し、取得結果にかかわらず画面に年度を渡します。
        int season = 2025;
        model.addAttribute("season", season);
        try {
            // AL・NLの地区別順位表を取得してモデルへ設定します。
            List<MlbStandingDivision> divisions = mlbApiService.getStandings(season);
            model.addAttribute("divisions", divisions);
        } catch (IllegalStateException e) {
            // 取得失敗時も戻るリンクを利用できるよう、同じ画面でエラーを表示します。
            model.addAttribute("errorMessage", "MLBレギュラーシーズン順位情報の取得に失敗しました。");
        }
        // レギュラーシーズン順位表のビュー名を返します。
        return "mlb-standings";
    }

}