package ameba.aeromock.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link ameba.aeromock.template.TemplateService}とテンプレート識別子を紐付けるためのアノテーションです。
 * @author stormcat24
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TemplateIdentifier {

	/**
	 * テンプレート識別子を返します。
	 * @return テンプレート識別子
	 */
	String name();

	/**
	 * 設定ファイルのマッピングクラスを返します。
	 * @return マッピングクラス
	 */
	Class<?> configType();

    /**
     * template.yaml以外のテンプレート設定ファイルを利用する場合、{@code true}を返します。
     * @return {@code true}: 非template.yaml / {@code false}: template.yaml
     */
    boolean specialConfig() default false;

}
