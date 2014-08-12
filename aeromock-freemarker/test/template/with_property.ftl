<html>
<head></head>
<body>

<#assign hoge = instance.getHoge() >
<#assign neko = instance.neko >
<#assign execute = instance.getChild().execute() >
<#assign tehepero = instance.getChild().tehepero() >
<#assign id = instance.getChild().id >
<#assign name = instance.getChild().name >
<#assign alert = raidwarBean.iRaidwarUser.alert >
<ul>
  <li>${hoge}</li>
  <li>${neko}</li>
  <li>${execute}</li>
  <li>${tehepero}</li>
  <li>${id}</li>
  <li>${name}</li>
  <li>${alert}</li>
</ul>
<#include "/common/footer.ftl">
</body>
</html>