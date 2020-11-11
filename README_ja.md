[English](README.md)

# DB Administrator

SI-Toolkit for DB Administratorは、DBマイグレーション、JPA(Java Persistence API)リバースエンジニアリング、およびDB設計ドキュメント生成のツールです。

主な機能は次のとおりです。

- SQL DDL/DMLスクリプトを実行し、[Flyway](https://flywaydb.org/)を使用してスキーマ・バージョニングを使用してDBオブジェクトを作成します。
- [Hibernate Tools](https://hibernate.org/tools/)を使用して、データベーススキーマからJPAエンティティjavaソースファイルを生成します。
- [Schemaspy](http://schemaspy.org/)を使用して、実際のDBメタデータからテーブルリストとER図を含むデータベース設計ドキュメントを生成します。

## ソフトウェア要件

DB Administratorには、次のソフトウェアが必要です。

- JDK11
- DockerまたはDBMS
- Maven

何らかの理由でDockerを使用できない場合は、xxxを参照してください。

## はじめに

DB Administratorの動作を確認するには、サンプルプロジェクトを実行します。

### サンプルプロジェクト

サンプルプロジェクトは、次のコマンドで実行できます。

```sh
git clone https://github.com/sitoolkit/sit-dba.git
cd sit-dba

# windows
mvnw

# macOS
./mvnw
```

このmavenビルドの結果、以下の成果物が生成されます。

- DBスキーマ
- JPAエンティティ
- DB設計ドキュメント

これらを表示するには、次のコマンドを実行します。

```sh
# Windows
## Open phpPgAdmin
start http://localhost
## Open directory which JPA Entity java files are generated in.
start sit-dba-entity¥target¥generated-sources¥hibernate-tools¥
## Open DB design document
start target¥schemaspy¥index.html


# macOS
## Open phpPgAdmin
open http://localhost
## Open directory which JPA Entity java files are generated in.
open sit-dba-entity/target/generated-sources/hibernate-tools
## Open DB design document
open target/schemaspy/index.html
```

## 仕組み

上記のMavenコマンドのビルドプロセスには、次の4つのフェーズが含まれています。

1. DB移行
2. リバース・エンジニアリング
3. テストとパッケージング
4. DB設計ドキュメントの生成

#### DB移行

DB移行のフェーズでは、SQLスクリプトがFlywayによって実行されます。スクリプト・ファイルは、Flywayのリソースの場所として定義されているsit-dba-migration/src/main/resources/db/migrationディレクトリにあります。

DBを接続するためのパラメータは、ルートpom.xmlで定義され、MavenビルドプロセスでFlywayに渡されます。

```xml
  <properties>
    <db.name>postgres</db.name>
    <db.host>localhost</db.host>
    <db.port>5432</db.port>
    <db.jdbc.url>jdbc:postgresql://${db.host}:${db.port}/${db.name}?currentSchema=${db.schema}</db.jdbc.url>
    <db.username>postgres</db.username>
    <db.password>postgres</db.password>
    <db.schema>sit_db</db.schema>
  </properties>
```


これらのパラメータは、すべてのフェーズで使用されます。

#### リバースエンジニアリング

リバース・エンジニアリングのフェーズでは、Hibernate Toolsによってjavaファイルが生成されます。これらのファイルは、DBスキーマ情報(表および関係)に基づいてJPAエンティティ・クラスを定義します。出力ディレクトリは、sit-dba-entity/target/generated-sources/hibernate-tools、sit-dba-entityプロジェクトの追加のjavaソースディレクトリです。

#### テストとパッケージング

テストとパッケージングのフェーズでは、sit-dba-sample-springのJUnitテストがMavenによって実行されます。

#### DB設計ドキュメントの生成

DB Design Document Generationでは、ドキュメントファイル(html,css,javascript)がSchemaspyによって生成される。これをdocker-composeサービスとして実装する。

## プロジェクト構造

DB Administratorには、次の3つのMavenプロジェクトがあります。

- sit-dba-migration
- sit-dba-entity
- sit-dba-sample-spring

### sit-dba-migration

このプロジェクトは、アプリケーションのDBスキーマを作成するためのDDL/DMLスクリプトを保守するためのものです。このプロジェクトは、[Flyway CLI](https://flywaydb.org/documentation/commandline/)をサポートする実行可能なjarにパッケージ化されています。

```
java -jar sit-db-migration-xxx-SNAPSHOT.jar --config=flyway.properties
```


flyway.propertiesの仕様は[ここ](https://flywaydb.org/documentation/configfiles)で定義されている。

### sit-dba-entity

このプロジェクトは、JPAエンティティを含む汎用ライブラリjarにパッケージ化され、アプリケーションjavaプロジェクトで使用されます。

### sit-dba-sample-spring

このプロジェクトは、Spring BootとSpring Data Spring BootとSpring Data JPAを使用しています。

## プロジェクトとして使用する方法

Mavenのarchetypeでは、プロジェクトとしてsit-dbaを使うことができます。
sit-dbaの基本的な使用方法は、次の手順です。

1. プロジェクトを生成します。
2. DB接続パラメータを変更します。
3. DDL/DMLスクリプトを変更します。
4. JPAエンティティに生成するターゲット表を変更します。

### プロジェクトを生成

次のmavenコマンドを使用して、新しいプロジェクトを生成できます。
(`xxx`、`yyy`をgroupIdとartifactIdに置き換えてください。)

```
mvn archetype:generate -B \
    -DarchetypeGroupId=io.sitoolkit.dba \
    -DarchetypeArtifactId=sit-dba-archetype \
    -DarchetypeVersion=1.0.0 \
    -DgroupId=xxx \
    -DartifactId=yyy
```


### DB接続パラメータの変更

プロジェクトが生成されたら、ルートプロジェクトのpom.xmlのDB接続パラメータを開発環境に変更します。

### DDL/DMLスクリプトの変更

yyy-migration/src/main/resources/db/migrationディレクトリにDBスキーマを作成するようにDDL/DMLスクリプトを変更します。
スクリプトファイルの名前は、<a href="https://flywaydb.org/documentation/migrations#sql-based-migrations" target="sql-migration">Flywayルール</a>に従います。

これらのスクリプトファイルは、次のコマンドで実行できます。

```sh
# Widows
mvnw -f yyy-migration -P migrate

# macOS
./mvnw -f yyy-migration -P migrate
```

### JPAエンティティに生成するターゲット表の変更

yyy-entity/hibernate.reveng.xmlファイルを変更して、JPAエンティティに生成するターゲット表を追加します。

```xml
<hibernate-reverse-engineering>

<!--これはサンプルであるため、DBスキーマを変更した後に削除します。-->
<table-filter match-name="person" package="io.sitoolkit.dba.domain.persion"></table-filter>

<!--ターゲットテーブルとそれらのエンティティパッケージを追加します。-->
<table-filter match-name="YOUR TABLE" package="YOUR PACKAGE OF ENTITY CLASS"></table-filter>

</hibernate-reverse-engineering>
```

修正後、コマンドを使用してエンティティを生成できます。

```sh
# Windows
mvnw -f yyy-entity -P reveng

# macOS
./mvnw -f yyy-entity -P reveng
```