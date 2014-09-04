<html>
<head></head>
<body>
	<ul>
		<li>obj.execute1() = ${obj1.execute1()}</li>
		<li>obj.execute2() = ${obj1.execute2()}</li>
		<li>obj.execute3() = ${obj1.execute3()}</li>
		<li>obj.execute4() = ${obj1.execute4()}</li>
		<li>obj.execute5() = ${obj1.execute5(1111)}</li>
		<li>obj.execute6() = ${obj1.execute6("arg1", "arg2", "arg3")}</li>
		<#assign execute7Result = obj1.execute7() >
		<li>obj.execute7().key = ${execute7Result.key}</li>
		<li>obj.execute7().value = ${execute7Result.value}</li>
		<#assign execute8Result = obj1.execute8() >
		<li>obj.execute8().key = ${execute8Result.key}</li>
		<li>obj.execute8().value.property = ${execute8Result.value.property}</li>
		<li>obj.execute8().value.execute81() = ${execute8Result.value.execute81()}</li>
		<li>obj.execute8().value.execute82() = ${execute8Result.value.execute82()}</li>
		<li>obj.execute8().value.execute83() = ${execute8Result.value.execute83()}</li>
		<li>obj.execute8().value.execute84() = ${execute8Result.value.execute84()}</li>
		<li>obj.execute8().value.execute85() = ${execute8Result.value.execute85()}</li>
		<li>obj.execute8().value.execute86() = ${execute8Result.value.execute86("arg81", "arg82", "arg83")}</li>
	</ul>
</body>
</html>
