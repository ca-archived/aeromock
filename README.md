Aeromock
===

![logo](https://github.com/CyberAgent/aeromock/raw/master/aeromock-view/img/aeromock.png)

What's Aeromock?
===
In a word, Aeromock is **Lightweight mock web application server**. Enable frontend development without modules of server side, any middleware, and quickly is the biggest mission.

Features
===
* Takes few seconds to boot Aeromock. Basically, if you started to Aeromock once, no need to reboot it.
* Necessary amount of memory is about between 200 to 300 MB, so runs fewer system resources.
* Only template file and data file related to template, respond HTML.
* Data file apply template, supports JSON and YAML format, but we strongly recommend using YAML format. Because, can share data structure and intention easily compared with JSON format.
* Create imitated Java instance by dymamic enhanced proxy, no problem to invoke java method on template.
* By writing routing script, control to URL Rewrite.
* Support common data file transversally. Control to applying rule by applying data script freely.
* Not one data file per one template file but Some data file per one template file, so you can try to do various pattern.
* Work as mock JSON API server.
* Work as static contents server (js, css, various images).
* Rendering unit test by command line tools (aeromock-unit), and output test report XML of JUnit report format. So, enable CI (Continuous Integration) of template and mock data files on Jenkins.

Getting started
===
@See [Wiki](https://github.com/CyberAgent/aeromock/wiki)

Requirements
===
* Java >= 1.7
* Git
* Vagrant >= 1.5 (If use Vagrant Share)


Support template engine (Currently)
===

* Freemarker
* handlebars.java
* Jade4j
* Velocity
* Groovy Template Engine
* Thymeleaf

Contributing
===
* Akinori Yamada - [@stormcat24](https://twitter.com/stormcat24) [github](https://github.com/stormcat24)

License
===
See [LICENSE](LICENSE).

Copyright © CyberAgent, Inc. All Rights Reserved.
