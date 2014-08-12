<html>
<head></head>
<body>

<h4>各データ型</h4>
<ul>
	<li>propString = ${propString}</li>
	<li>propInt = ${propInt}</li>
	<li>propBoolean = ${propBoolean?string}</li>
	<li>propLong = ${propLong}</li>
	<li>propDouble = ${propDouble}</li>
</ul>

<h4>Map（通常参照）</h4>
<ul>
	<li>map.propSimple = ${map.propSimple}</li>
<#list map.propList as value>
	<li>map.propList[${value_index}] = ${value}</li>
</#list>
	<li>map.propMap.childSimple = ${map.propMap.childSimple}</li>
<#list map.propMap.childList as value>
	<li>map.propMap.childList[${value_index}] = ${value}</li>
</#list>
</ul>

<h4>Map（Hash参照）</h4>
<ul>
	<li>map["propSimple"] = ${map["propSimple"]}</li>
<#list map["propList"] as value>
	<li>map["propList"][${value_index}] = ${value}</li>
</#list>
	<li>map["propMap"]["childSimple"] = ${map["propMap"]["childSimple"]}</li>
<#list map["propMap"]["childList"] as value>
	<li>map["propMap"]["childList"][${value_index}] = ${value}</li>
</#list>
</ul>

<h4>Map（get参照）</h4>
<ul>
	<li>map.get("propSimple") = ${map.get("propSimple")}</li>
<#list map.get("propList") as value>
	<li>map.get("propList")[${value_index}] = ${value}</li>
</#list>
	<li>map.get("propMap").get("childSimple") = ${map.get("propMap").get("childSimple")}</li>
<#list map.get("propMap").get("childList") as value>
	<li>map.get("propMap").get("childList")[${value_index}] = ${value}</li>
</#list>
</ul>
</body>
</html>