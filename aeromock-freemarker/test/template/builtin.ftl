<html>
<head></head>
<body>
<#assign url = httpServletRequest.getRequestURI() />
<ul>
  <li>url.split("/")?last = ${url.split("/")?last}</li>
  <li>url.substring(1, 2) = ${url.substring(1, 2)}</li>
</ul>
</body>
</html>