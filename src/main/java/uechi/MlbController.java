package uechi;

import java.time.LocalDate;
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
 * 対象日の検索結果やURLで指定された試合IDをThymeleafの画面に渡します。
 *
 * 【作成日】2026-10-21
 * 【最終更新日】2026-11-16
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
     * URLで指定された試合IDを画面に渡し、試合詳細のビュー名を返します。
     *
     * @param gamePk URLのパスに含まれるMLBの試合識別ID
     * @param model 画面に渡すデータを保持するモデル
     * @return 試合詳細のテンプレートを指定するビュー名
     */
    @GetMapping("/mlb/game/{gamePk}")
    public String detail(@PathVariable("gamePk") long gamePk, Model model) {
        // URLのパスから受け取った試合IDをモデルに設定し、詳細画面に渡します。
        model.addAttribute("gamePk", gamePk);

        // Thymeleafでtemplates/mlb-detail.htmlを表示するためのビュー名を返します。
        return "mlb-detail";
    }
}
