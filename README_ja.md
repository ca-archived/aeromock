Aeromock
===

![logo](https://github.com/CyberAgent/aeromock/raw/master/aeromock-view/img/aeromock.png)

What's Aeromock?
===
一言で言うと、Aeromockとは**Lightweight mock web application server**です。サーバサイドのモジュールや、付随するミドルウェア依存しない高速なフロントエンド開発を可能にすることが最大のミッションです。

Features
===
* Aeromockの起動は数秒で、基本的に一度Aeromockを起動したら再度起動する必要はありません。
* 必要なメモリはおおよそ200から300MBであり、少ないシステムリソースで稼働します。
* テンプレートファイルと、それに紐づくデータファイルを用意するだけでHTMLを返します。
* テンプレートに対応させるデータファイルはJSONとYAML形式をサポートしていますが、YAML形式を強く推奨しています。JSON形式に比べて、データ構造や意図を共有しやすいからです。
* Dynamic Enhanced ProxyによってJavaのインスタンスを生成することができるため、テンプレート上でのJavaメソッド実行も問題ありません。
* カスタムルーティングスクリプトを記述することで、URL Rewriteを制御できます。
* 横断的に適用できる共通データファイルをサポートしています。適用ルールはスクリプトによって自由に制御できます。
* 1つのテンプレートファイルに1つのデータファイルではなく、複数のデータファイルを対応させることができます。これにより、様々なパターンのデータファイルを試すことが出来ます。
* モックJSON APIサーバとしても機能します。
* 静的ファイルサーバとしても機能します（js, css, 画像等)。
* aeromock-unitというコマンドラインツールを使えば描画のユニットテストができ、JUnit形式のXMLファイルにテストレポートを出力することが出来ます。そのため、Jenkins上でテンプレートとモックデータファイルをCIすることが可能です。

Getting started
===
@See [Wiki](https://github.com/CyberAgent/aeromock/wiki)

Requirements
===
* Java >= 1.7
* Git
* Vagrant >= 1.5 (Vagrant Shareを利用する場合)

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
