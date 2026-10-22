package uechi;

import java.time.LocalDate;

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
 * 対象日を受け取り、Spring MVCとThymeleafで同じ画面に表示します。
 *
 * 【作成日】2026-10-21
 * 【最終更新日】2026-10-22
 *
 * @author masa
 * @version 1.0
 */
@Controller
public class MlbController {

    /**
     * 指定された対象日を画面に渡し、MLB試合情報のビュー名を返します。
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

        // Thymeleafでtemplates/mlb.htmlを表示するためのビュー名を返します。
        return "mlb";
    }
}
