package uechi;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 【ソースファイル名】
 * ServletInitializer.java
 *
 * 【概要】
 * 外部のサーブレットコンテナで起動する際に、アプリケーションの構成元を設定するクラスです。
 * WARファイルとして配備したSpring Bootアプリケーションの初期化に使用します。
 *
 * 【作成日】2026-09-23
 * 【最終更新日】2026-09-23
 *
 * @author masa
 * @version 1.0
 */
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		// 起動クラスを構成元として登録し、設定済みのアプリケーションビルダーを返します。
		return application.sources(ProjectWebSpringboot001Application.class);
	}

}
