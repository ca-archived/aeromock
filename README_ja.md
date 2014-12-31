Aeromock
===

[![Gitter chat](https://badges.gitter.im/CyberAgent/aeromock.png)](https://gitter.im/CyberAgent/aeromock)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/jp.co.cyberagent.aeromock/aeromock-server_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/jp.co.cyberagent.aeromock/aeromock-server_2.11)
[![Circle CI](https://circleci.com/gh/CyberAgent/aeromock.png?style=shield&circle-token=3d2a76e5fdfb5c6c6da90f1eb7038ebd8df0e85a)](https://circleci.com/gh/CyberAgent/aeromock)
[![License: MIT](http://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)
[![Coverage Status](https://img.shields.io/coveralls/CyberAgent/aeromock.svg)](https://coveralls.io/r/CyberAgent/aeromock?branch=master)
![logo](https://github.com/CyberAgent/aeromock/raw/master/aeromock-view/img/aeromock.png)

Aeromockとは？
===
AeromockはテンプレートファイルとデータファイルだけでHTMLを描画することができるアプリケーションサーバです。サーバサイドのモジュールやミドルウェア無しでフロントエンド開発を可能にします。
軽量に動作するため、**Lightweight mock web application server** を謳っています。

機能
===
* 一度起動したら再起動する必要はありません。
* テンプレートファイルと、それに紐づくデータファイル（JSONまたはYAML）を用意するだけでHTMLを返します。
* メソッド呼び出しや、カスタムタグ・ファンクションを利用しているテンプレートもサポートします。
* 1つのテンプレートに対して、複数パターンの描画をすることができます。
* スクリプトによってURL Rewriteを制御できます。
* モックJSON APIサーバとしても機能します。
* 静的ファイルサーバとしても機能します（js, css, 画像等)。
* Protocol Buffersやmessagepackのバイナリデータをレスポンスできます。

利用するには
===
@See [Wiki](https://github.com/CyberAgent/aeromock/wiki)

前提条件
===
* Java >= 1.7
* Git
* Vagrant >= 1.5 (Vagrant Shareを利用する場合)

サポートしているテンプレートエンジン
===

* Freemarker
* handlebars.java
* Jade4j
* Velocity
* Groovy Template Engine
* Thymeleaf

コミッタ
===
* Akinori Yamada - [@stormcat24](https://twitter.com/stormcat24) [github](https://github.com/stormcat24)

ライセンス
===
See [LICENSE](LICENSE).

Copyright © CyberAgent, Inc. All Rights Reserved.
