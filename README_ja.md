Aeromock
===

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
