Aeromock
===
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/jp.co.cyberagent.aeromock/aeromock-server_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/jp.co.cyberagent.aeromock/aeromock-server_2.11)
[![Circle CI](https://circleci.com/gh/CyberAgent/aeromock.png?style=shield&circle-token=3d2a76e5fdfb5c6c6da90f1eb7038ebd8df0e85a)](https://circleci.com/gh/CyberAgent/aeromock)
[![License: MIT](http://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)
[![Coverage Status](https://img.shields.io/coveralls/CyberAgent/aeromock.svg)](https://coveralls.io/r/CyberAgent/aeromock?branch=master)
![logo](https://github.com/CyberAgent/aeromock/raw/master/aeromock-view/img/aeromock.png)

What is Aeromock?
===
Aeromock is an application server that generates HTML from template files and data files. It enables speedy frontend development without the need for server side modules. Aeromock is referred to as a **lightweight mock web application server**.

Features
===
* No need to restart the application server once it has been started.
* HTML pages are generated from template files and their corresponding data files (JSON or YAML).
* Supports templates that invoke methods and make use of custom tags and functions.
* Various patterns can be rendered per template file.
* URL rewriting can be controlled via a routing script.
* Capable of functioning as a mock JSON API server.
* Capable of functioning as a static content server (js, css, images).

Getting started
===
Please refer to the [wiki](https://github.com/CyberAgent/aeromock/wiki).

Requirements
===
* Java >= 1.7
* Git
* Vagrant >= 1.5 (If using Vagrant Share)


Supported template engines (Currently)
===

* Freemarker
* handlebars.java
* Jade4j
* Velocity
* Groovy Template Engine
* Thymeleaf

Contributors
===
* Akinori Yamada - [@stormcat24](https://twitter.com/stormcat24) [github](https://github.com/stormcat24)

License
===
See [LICENSE](LICENSE).

Copyright © CyberAgent, Inc. All Rights Reserved.
