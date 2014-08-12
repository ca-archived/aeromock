<html>
<head></head>
<body>

<h4>ビルトイン項目確認</h4>
<ul>
	<li>REQUEST_URI = ${requestUri}</li>
	<li>QUERY_STRING = ${queryString}</li>
	<li>HOST = ${host}</li>
	<li>REMOTE_HOST = ${remoteHost}</li>
</ul>

<h4>シンプルネスト</h4>
<ul>
	<li>nest.level1 = ${nest.level1}</li>
	<li>nest.property1 = ${nest.property1}</li>
	<li>nest.child.level2 = ${nest.child.level2}</li>
	<li>nest.child.property2 = ${nest.child.property2}</li>
</ul>

<h4>シンプルリスト</h4>
<ul>
<#list simpleList as value>
    <li>${value}</li>
</#list>
</ul>

<h4>ネストリスト</h4>
<ul>
<#list nestList as value>
    <li>${value}</li>
</#list>
</ul>

<h4>メソッド付きネスト</h4>
<ul>
	<li>nestWithMethod.level1 = ${nestWithMethod.level1}</li>	
	<li>nestWithMethod.property1 = ${nestWithMethod.property1}</li>
	<li>nestWithMethod.execute1() = ${nestWithMethod.execute1()}</li>
	<#assign execute2Result = nestWithMethod.execute2()/>
	<li>nestWithMethod.execute2().id = ${execute2Result.id}</li>
	<li>nestWithMethod.execute2().value = ${execute2Result.value}</li>
	<#assign execute3Result = nestWithMethod.execute3()/>
	<li>nestWithMethod.execute3().property1 = ${execute3Result.property1}</li>
	<#list execute3Result as element>
	<li>nestWithMethod.execute3()[${element_index}] = ${element}</li>
	</#list>
	<li>nestWithMethod.execute3().execute().childProperty = ${execute3Result.execute().childProperty}</li>
	<#assign execute4Result = nestWithMethod.execute4()/>
	<li>execute4Result.property1 = ${nestWithMethod.execute4().property1}</li>
	<#list execute4Result as element>
	<li>nestWithMethod.execute4()[${element_index}].level1 = ${element.level1}</li>
	<li>nestWithMethod.execute4()[${element_index}].execute() = ${element.execute()}</li>
	</#list>
</ul>

</body>
</html>