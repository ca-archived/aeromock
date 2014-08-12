<html>
<head></head>
<body>
	<h4>__list（中身は数値型）自身をループ</h4>
	<ul>
<#list list as element>
		<li>${element}</li>
</#list>
	</ul>

	<h4>java.util.Collectionのメソッド呼び出し</h4>
	<ul>
		<li>list.size() = ${list.size()}</li>		
	</ul>
	
	<h4>__methods追加メソッド呼び出し</h4>
	<ul>
		<li>list.execute() = ${list.execute()}</li>
	</ul>
	
	<h4>プロパティ呼び出し</h4>
	<ul>
		<li>list.getProperty1() = ${list.property1}</li>
		<li>list.property1 = ${list.property1}</li>
		<li>list["property1"] = ${list.property1}</li>
		<li>list.booleanType = ${list.booleanType?string}</li>
	</ul>
	
	<h4>__list（中身はオブジェクト）自身をループ</h4>
	<ul>
		<li>objectList.neko = ${objectList.neko}</li>
<#list objectList as element>
		<li>property1 = ${element.property1}, property2 = ${element.property2}</li>
		<li>getNeko() = ${element.getNeko()}</li>
</#list>
	</ul>
	
	<#assign prizes = gettingPrizes.exclude(dailyBonusPrizes)>
<#list prizes as prize>
      ${prize.point}, ${prize.stampCount}, ${prize.neko.id}<br/>
</#list>
    
    <h4>proxyが返す__list</h4>
    <ul>
    	<li>proxy.pager().getCurrentPage() = ${proxy.pager().getCurrentPage()}</li>
    	<#assign simpleList = proxy.pager().getSimpleDataList()>
    	<#list simpleList as data>
    	<li>proxy.pager().getSimpleDataList()[${data_index}] = ${data}</li>
    	</#list>
    	<#assign objectList = proxy.pager().getObjectList()>
    	<li>proxy.pager().getObjectList().name = ${objectList.name}</li>
    	<#list objectList as data>
    	<li>proxy.pager().getObjectList()[${data_index}].id = ${data.id}</li>
    	<li>proxy.pager().getObjectList()[${data_index}].value = ${data.value}</li>
    	</#list>
    </ul>
</body>
</html>