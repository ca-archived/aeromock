<html>
<head></head>
<body>

<h4>Map（通常参照）</h4>
<ul>
	<li>proxy.propSimple = ${proxy.propSimple}</li>
<#list proxy.propList as value>
	<li>proxy.propList[${value_index}] = ${value}</li>
</#list>
	<li>proxy.propMap.childSimple = ${proxy.propMap.childSimple}</li>
<#list proxy.propMap.childList as value>
	<li>proxy.propMap.childList[${value_index}] = ${value}</li>
</#list>
	<li>proxy.execute() = ${proxy.execute()}</li>
	<li>proxy.executeHash().id = ${proxy.executeHash().id}</li>
	<li>proxy.executeHash().value = ${proxy.executeHash().value}</li>
	<li>proxy.executeHash().map.key1 = ${proxy.executeHash().map.key1}</li>
	<li>proxy.executeHash().map.key2 = ${proxy.executeHash().map.key2}</li>
	<li>proxy.executeProxy().key1 = ${proxy.executeProxy().key1}</li>
	<li>proxy.executeProxy().executeChild() = ${proxy.executeProxy().executeChild()}</li>
	<li>proxy.executeProxy().executeChildHash().key1 = ${proxy.executeProxy().executeChildHash().key1}</li>
	<li>proxy.executeProxy().executeChildHash().key2 = ${proxy.executeProxy().executeChildHash().key2}</li>
	<li>proxy.executeProxy().executeChildProxy().key1 = ${proxy.executeProxy().executeChildProxy().key1}</li>
	<li>proxy.executeProxy().executeChildProxy().executeChildChild() = ${proxy.executeProxy().executeChildProxy().executeChildChild()}</li>
</ul>

<h4>Map（Hash参照）</h4>
<ul>
	<li>proxy["propSimple"] = ${proxy["propSimple"]}</li>
<#list proxy["propList"] as value>
	<li>proxy["propList"][${value_index}] = ${value}</li>
</#list>
	<li>proxy["propMap"]["childSimple"] = ${proxy["propMap"]["childSimple"]}</li>
<#list proxy["propMap"]["childList"] as value>
	<li>proxy["propMap"]["childList"][${value_index}] = ${value}</li>
</#list>
	<li>proxy.execute() = ${proxy.execute()}</li>
	<li>proxy.executeHash()["id"] = ${proxy.executeHash()["id"]}</li>
	<li>proxy.executeHash()["value"] = ${proxy.executeHash()["value"]}</li>
	<li>proxy.executeHash()["map"]["key1"] = ${proxy.executeHash()["map"]["key1"]}</li>
	<li>proxy.executeHash()["map"]["key2"] = ${proxy.executeHash()["map"]["key2"]}</li>
	<li>proxy.executeProxy()["key1"] = ${proxy.executeProxy()["key1"]}</li>
	<li>proxy.executeProxy().executeChild() = ${proxy.executeProxy().executeChild()}</li>
	<li>proxy.executeProxy().executeChildHash()["key1"] = ${proxy.executeProxy().executeChildHash()["key1"]}</li>
	<li>proxy.executeProxy().executeChildHash()["key2"] = ${proxy.executeProxy().executeChildHash()["key2"]}</li>
	<li>proxy.executeProxy().executeChildProxy()["key1"] = ${proxy.executeProxy().executeChildProxy()["key1"]}</li>
	<li>proxy.executeProxy().executeChildProxy().executeChildChild() = ${proxy.executeProxy().executeChildProxy().executeChildChild()}</li>
</ul>

<h4>Map（get参照）</h4>
<ul>
	<li>proxy.get("propSimple") = ${proxy.get("propSimple")}</li>
<#list proxy.get("propList") as value>
	<li>proxy.get("propList")[${value_index}] = ${value}</li>
</#list>
	<li>proxy.get("propMap").get("childSimple") = ${proxy.get("propMap").get("childSimple")}</li>
<#list proxy.get("propMap").get("childList") as value>
	<li>proxy.get("propMap").get("childList")[${value_index}] = ${value}</li>
</#list>
	<li>proxy.execute() = ${proxy.execute()}</li>
	<li>proxy.executeHash().get("id") = ${proxy.executeHash().get("id")}</li>
	<li>proxy.executeHash().get("value") = ${proxy.executeHash().get("value")}</li>
	<li>proxy.executeHash().get("map").get("key1") = ${proxy.executeHash().get("map").get("key1")}</li>
	<li>proxy.executeHash().get("map").get("key2") = ${proxy.executeHash().get("map").get("key2")}</li>
	
	<li>proxy.executeProxy().get("key1") = ${proxy.executeProxy().get("key1")}</li>
	<li>proxy.executeProxy().executeChild() = ${proxy.executeProxy().executeChild()}</li>
	<li>proxy.executeProxy().executeChildHash().get("key1") = ${proxy.executeProxy().executeChildHash().get("key1")}</li>
	<li>proxy.executeProxy().executeChildHash().get("key2") = ${proxy.executeProxy().executeChildHash().get("key2")}</li>
	<li>proxy.executeProxy().executeChildProxy().get("key1") = ${proxy.executeProxy().executeChildProxy().get("key1")}</li>
	<li>proxy.executeProxy().executeChildProxy().executeChildChild() = ${proxy.executeProxy().executeChildProxy().executeChildChild()}</li>
</ul>
</body>
</html>