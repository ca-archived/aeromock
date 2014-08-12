<html>
<head></head>
<body>
<ul>
	<li>.template_name = ${.template_name}</li>
	<li>.globals.hoge = ${.globals.hoge}</li>
	<#macro m>
	<#local localValue = "local!" />
	<li>.locals.localValue = ${.locals.localValue}</li>
	</#macro>
	<@m/>
	<li>.data_model.hoge = ${.data_model.hoge}</li>
	<li>.lang = ${.lang}</li>
	<li>.locale = ${.locale}</li>
	<li>.version = ${.version}</li>
	<li>.vars.hoge = ${.vars.hoge}</li>
	<#assign neko = "nuko" />
	<li>.vars.neko = ${.vars.neko}</li>
	<#attempt>
		<#assign value = Integer.parseInt("hoge") />
	<#recover>
	</#attempt>
	<li>.output_encoding = ${.output_encoding}</li>
	<li>.url_escaping_charset = ${.url_escaping_charset}</li>
	<li>.now = ${.now}</li>
</ul>
</body>
</html>