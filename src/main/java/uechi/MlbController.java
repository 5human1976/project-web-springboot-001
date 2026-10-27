package uechi;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 【ソースファイル名】
 * MlbController.java
 *
 * 【概要】
 * /mlbへのGETリクエストに対してMLB試合情報の画面を表示するクラスです。
 * 対象日の試合情報をServiceから取得し、検索結果をThymeleafの画面に渡します。
 *
 * 【作成日】2026-10-21
 * 【最終更新日】2026-10-27
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
}
