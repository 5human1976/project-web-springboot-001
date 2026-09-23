package uechi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【ソースファイル名】
 * HelloController.java
 *
 * 【概要】
 * ルートパスへのGETリクエストに対して "Hello, World!" を返すクラスです。
 * WebアプリケーションのHTTP応答の動作確認に使用します。
 *
 * 【作成日】2026-09-23
 * 【最終更新日】2026-09-23
 *
 * @author masa
 * @version 1.0
 */
@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        // HTTPレスポンスの本文として "Hello, World!" を返します。
        return "Hello, World!";
    }
}
