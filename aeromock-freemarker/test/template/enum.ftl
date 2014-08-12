<html>
<head></head>
<body>
<#assign testType = enums["ameba.aeromock.test.enums.TestType"] />
<#assign testType2 = enums["ameba.aeromock.test.enums.TestType2"] />
	<ul>
		<li>enums["ameba.aeromock.test.enums.TestType"].NEKO1.key = ${testType.NEKO1.key}</li>
		<li>enums["ameba.aeromock.test.enums.TestType"].NEKO1.value = ${testType.NEKO1.value}</li>
		<li>enums["ameba.aeromock.test.enums.TestType"].NEKO2.key = ${testType.NEKO2.key}</li>
		<li>enums["ameba.aeromock.test.enums.TestType"].NEKO2.value = ${testType.NEKO2.value}</li>
		<li>enums["ameba.aeromock.test.enums.TestType2"].NUKO1.key = ${testType2.NUKO1.key}</li>
		<li>enums["ameba.aeromock.test.enums.TestType2"].NUKO1.value = ${testType2.NUKO1.value}</li>
		<li>enums["ameba.aeromock.test.enums.TestType2"].NUKO2.key = ${testType2.NUKO2.key}</li>
		<li>enums["ameba.aeromock.test.enums.TestType2"].NUKO2.value = ${testType2.NUKO2.value}</li>
	</ul>
</body>
</html>