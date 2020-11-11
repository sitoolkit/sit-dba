[日本語](README_ja.md)

# DB Administrator

SI-Toolkit for DB Administrator is a tool of DB migration, JPA (Java Persistence API) reverse engineering and DB design document generation.

Its main functionalities are

- Executes SQL DDL / DML script to create DB objects with schema versioning using [Flyway](https://flywaydb.org/).
- Generates JPA entity java source file from database schema using [Hibernate Tools](https://hibernate.org/tools/).
- Generates database design document contains table list and ER diagram from actual DB metadata using [Schemaspy](http://schemaspy.org/).


## Software Requirements

DB Administrator requires the following software.

- JDK 11
- Docker or DBMS
- Maven

If you can't use docker for some reason, see xxx.

## Get Started

To know how DB Administrator works, you can run a sample project.

### Sample Project

You can run the sample project with the following commands.

```sh
git clone https://github.com/sitoolkit/sit-dba.git
cd sit-dba

# Windows
mvnw

# macOS
./mvnw
```

As the result of this maven build, the following deliverables are generated.

- DB Schema
- JPA Entity
- DB Design Document

To see them, execute the following commands.

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


## How It Works?

The build process of the above Maven command contains these 4 phases.

1. DB Migration
2. Reverse Engineering
3. Test and Packaging
4. DB Design Document Generation


#### DB Migration

In DB Migration phase, SQL scripts are executed by Flyway. The script files are in sit-dba-migration/src/main/resources/db/migration directory which is defined as resource location of Flyway.

The parameters to connect DB are defined in root pom.xml and passed to Flyway in Maven build process.

```xml
  <properties>
    <db.name>postgres</db.name>
    <db.host>localhost</db.host>
    <db.port>5432</db.port>
    <db.jdbc.url>jdbc:postgresql://${db.host}:${db.port}/${db.name}?currentSchema=${db.schema}</db.jdbc.url>
    <db.username>postgres</db.username>
    <db.password>postgres</db.password>
    <db.schema>${project.parent.artifactId}</db.schema>
  </properties>
```

These parameters are used in all phases.

#### Revese Engineering

In the Reverse Reverse Engineering phase, java files are generated by Hibernate Tools. Those files define JPA Entity class based on DB schema information (tables and relations). Output directory is sit-dba-entity/target/generated-sources/hibernate-tools which is additional java source directory of sit-dba-entity project.

#### Test and Packaging

In the Test and Packaging phase, JUnit tests in sit-dba-sample-spring are executed by Maven.


#### DB Design Document Generation

In the DB Design Document Generation, document files (html, css, javascript) are generated by Schemaspy. We implement it as a docker-compose service.


## Project Structure

DB Administrator has these 3 Maven projects.

- sit-dba-migration
- sit-dba-entity
- sit-dba-sample-spring


### sit-dba-migration

This project is for maintaining DDL / DML script to create DB schema for the application. It is packaged to an executable jar which supports [Flyway CLI](https://flywaydb.org/documentation/commandline/). 

```
java -jar sit-db-migration-xxx-SNAPSHOT.jar --config=flyway.properties
```

The specification of flyway.properties is defined in [here](https://flywaydb.org/documentation/configfiles).

### sit-dba-entity

Thie project is to be packaged to general library jar which contains JPA Entities and is used by application java projects.

### sit-dba-sample-spring

This project is the sample of implementation of Spring family, uses Spring Boot and Spring Data JPA.

## How To Use as Your Project

You can use sit-dba as your project with Maven archetype.
Basic usage of sit-dba is the following steps.

1. Generate project.
2. Modify DB connection parameters.
3. Modify DDL / DML script.
4. Modify target tables to generate to JPA Entity.

### Generate Project

You can generate a new project using the following maven command.
(Replace `xxx` and `yyy` to your groupId and artifactId.)

```
mvn archetype:generate -B \
    -DarchetypeGroupId=io.sitoolkit.dba \
    -DarchetypeArtifactId=sit-dba-archetype \
    -DarchetypeVersion=1.1.0-SNAPSHOT \
    -DgroupId=xxx \
    -DartifactId=yyy
```


### Modify DB Connection Parameters

After a project is generated, change DB connection parameters in pom.xml in the root project to your development environment.


### Modify DDL / DML Script

Modify DDL / DML script to create your DB schema in yyy-migration/src/main/resources/db/migration directory.
The name of script files follows <a href="https://flywaydb.org/documentation/migrations#sql-based-migrations" target="sql-migration">Flyway rules</a>.


You can execute those script files with the following command. 

```sh
# Widows
mvnw -f yyy-migration -P migrate

# macOS
./mvnw -f yyy-migration -P migrate
```


### Modify Target Tables to Generate to JPA Entity

Modify yyy-entity/hibernate.reveng.xml file to add target tables to genereate to JPA entity.


```xml
<hibernate-reverse-engineering>

  <!-- This is sample, so remove after modifing DB schema. -->
  <table-filter match-name="person" package="io.sitoolkit.dba.domain.persion"></table-filter>

  <!--  Add your target tables and those entity packages. -->
  <table-filter match-name="YOUR TABLE" package="YOUR PACKAGE OF ENTITY CLASS"></table-filter>

</hibernate-reverse-engineering>
```

After modifying, you can generate entities with command.

```sh
# Windows
mvnw -f yyy-entity -P reveng

# macOS
./mvnw -f yyy-entity -P reveng
```