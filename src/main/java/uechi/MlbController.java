package uechi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 【ソースファイル名】
 * MlbController.java
 *
 * 【概要】
 * /mlbへのGETリクエストに対してMLB試合情報の画面を表示するクラスです。
 * Spring MVCとThymeleafによるHTML画面の表示確認に使用します。
 *
 * 【作成日】2026-10-21
 * 【最終更新日】2026-10-21
 *
 * @author masa
 * @version 1.0
 */
@Controller
public class MlbController {

    /**
     * MLB試合情報の画面を表示するためのビュー名を返します。
     *
     * @return MLB試合情報のテンプレートを指定するビュー名
     */
    @GetMapping("/mlb")
    public String mlb() {
        // Thymeleafでtemplates/mlb.htmlを表示するためのビュー名を返します。
        return "mlb";
    }
}
