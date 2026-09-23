package uechi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 【ソースファイル名】
 * ProjectWebSpringboot001Application.java
 *
 * 【概要】
 * Spring BootのWebアプリケーションを起動するクラスです。
 * mainメソッドから実行する際の起動処理の入口として使用します。
 *
 * 【作成日】2026-09-23
 * 【最終更新日】2026-09-23
 *
 * @author masa
 * @version 1.0
 */
@SpringBootApplication
public class ProjectWebSpringboot001Application {

	public static void main(String[] args) {
		// 起動クラスとコマンドライン引数を渡し、Spring Bootアプリケーションを起動します。
		SpringApplication.run(ProjectWebSpringboot001Application.class, args);
	}

}
