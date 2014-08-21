<html>
<head></head>
<body>

<h4>use Aeromock builtin variables</h4>
<ul>
	<li>REQUEST_URI = ${requestUri}</li>
	<li>QUERY_STRING = ${queryString}</li>
	<li>HOST = ${host}</li>
	<li>REMOTE_HOST = ${remoteHost}</li>
</ul>

<h4>simple nest</h4>
<ul>
	<li>nest.level1 = ${nest.level1}</li>
	<li>nest.property1 = ${nest.property1}</li>
	<li>nest.child.level2 = ${nest.child.level2}</li>
	<li>nest.child.property2 = ${nest.child.property2}</li>
</ul>

<h4>simple list</h4>
<ul>
<#list simpleList as value>
    <li>${value}</li>
</#list>
</ul>

<h4>nest list</h4>
<ul>
<#list nestList as value>
    <li>${value}</li>
</#list>
</ul>

<h4>nest with method</h4>
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

<h4>common data</h4>
<ul>
	<li>commonProp = ${commonProp}</li>
	<li>commonHash.prop1 = ${commonHash.prop1}</li>
</ul>

<h4>use tag</h4>
<ul>
	<li><@span style="font-style: italic;">span test</@span></li>
</ul>

<h4>use function</h4>
<div>
	<img src=${staticUrl("/img/sample.jpg")} />
</div>

<h4>Freemarker builtin '.now'</h4>
<ul>
	<li>${.now?string}</li>
</ul>

<h4>Spring macro</h4>
<ul>
	<li>@spring.message = <@spring.message "site.title" /></li>
	<li>@spring.message = <@spring.url "/aaa" /></li>
</ul>

</body>
</html>
