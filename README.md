# 🚩 Documentation :

[Documentation complète](https://capgemini.sharepoint.com/:w:/r/sites/ELIThELponITprojectmanagement/Shared%20Documents/General/1%20-%20Dev%20Team%20area/Environnement%20de%20d%C3%A9veloppement/Guide%20d%27installation%20ELIT.docx?d=w6adc79bd058f4752ba25bf73a31cf7d9&csf=1&web=1&e=XgrhjU) 

## Lunch the application locally without Docker (Recommand for developement)

1. Download JAVA and MAVEN
2. Duplicate .ven.tpl and rename in .env
3. Open a terminal, navigate to the project directory, and run the command :

```shell
  mvn clean install
```
Only one time for get 'target' file

2. Lunch application
```shell
mvn spring-boot:run
```   

3. Bonus : Before push on git excute this
```shell
checkstyle:check pmd:check verify site 
``` 

## Lunch the application locally with Docker (Recommand for ISO AzureDevOps)

1. Create the network : elit_shared_network

```shell
  docker network create elit_shared_network 
```

2. Open dev.docker-compose.yml

```shell
  docker-compose -f dev.docker-compose.yml up --build --force-recreate --remove-orphans 
```

## Local user accounts

| Id | First name | Last name | Login                | Password     |
|:---|:-----------|:----------|:---------------------|:-------------|
| 1  | John       | Doe       | user@user.com        | Azerty1234!? |
| 2  | Nancy      | Michel    | admin@elite.fr       | Azerty1234!? |
| 3  | Charles    | Baker     | contributer@elit.com | Azerty1234!? |
| 4  | Rochelle   | Wright    | observer@elit.com    | Azerty1234!? |

# Old Documentation

## Launch the application locally

### With IDE

Once the project is imported to your favorite IDE, refer to its documentation on how to launch a Java project.
Make sure : 
1. It runs on your local machine.
2. It uses Java 11 as runner (either JDK or JRE should be fine as long as the version is correct).
3. The main class should be `com.elit.outildedecision.OutildedecisionApplication`
4. ***VERY IMPORTANT*** : use `local` as Spring profile. Feel free to ask your favorite AI how to add required information to the run configuration.
5. Optional : save console output to a file on your local filesystem, in case there is a log that becomes inaccessible. The directory `logs/` in the project is an ideal candidate to store this file as it should be excluded from version control.


## Launch the application in development mode in the same way as in production mode

1. Create an `.env` file from the `.env.tpl` file

2. Change value of variable `DB_SERVER` to `localhost` if you launch the application from IDE or maven, and to `db` if you launch from Docker.

3. If you launch from IDE or Maven, you should comment the following part in file `dev.docker-compose.yml` because container 'back' will NOT be used :

  ```yaml
  back:
    container_name: 'elit_backend'
    build:
      context: "."
      dockerfile: Dockerfile
    env_file: .env
    environment:
      - SPRING_PROFILES_ACTIVE=local
    ports:
      - "8080:8080"
    volumes:
      - ./out/:/usr/src/app/out/
    depends_on:
      - db
    networks:
      - elit_network
   ```
      
4. Launch the following docker-compose command :
  `docker-compose -f dev.docker-compose.yml up --build --force-recreate --remove-orphans`

5. You'll have (2/3) Docker containers : database, phpMyAdmin and backend (depending on step 2).




## Database 

The project uses `H2` (in-memory DB) for local environment (actual setup, please correct when local environment will be fully back on Docker).
It uses a `MySQL` database managed by DevOps team in INTEG environment (please update this file when application will be deployed in production environment).

Since in memory database is non-persistent and we don't have rights to access INTEG database, we use Liquibase to manage schema evolution. 
Moreover, Hibernate official documentation states not to use `jpa.hibernate.ddl-auto=update` in live environments.

To learn more about Liquibase, please consult the [official documentation](https://docs.liquibase.com/home.html).

### How the project uses Liquibase

From application.properties config file applicable to current environment we refer to a liquibase master change log. For dev we have the following :

```properties
spring.liquibase.change-log=classpath:liquibase/dev/master.xml
```

Said changelog being located at `src/main/resources/liquibase/dev/master.xml`. By default, `src/main/resources` being in Spring classpath, the master change log is supposed to be automatically picked up and Liquibase run by Spring Boot.

Liquibase stores the changelogs already ran in tables `DATABASECHANGELOG` and `DATABASECHANGELOGLOCK`. Therefore, you only need to describe what changes you made during the current feature.

When changing entities (adding, removing or renaming properties), we need to tell Liquibase to update database schema with a changelog containing a changeset. A good prodecure to follow is :
- Create an XML file in `src/main/resources/liquibase/<env>/changelog` following the convention.
- Create a XML data change log item copied from existing files.
- Include a `<changeSet>` element. Fill in the `author` and `id` required attributes. The `author` attribute is not very important but the `id` is, very much so. A good rule of thumb is to use [unix timestamp](https://www.unixtimestamp.com/) to ensure your change set is effectively unique (there is a 'Copy' button you can use on the provided site). You can use seconds or milliseconds it is up to you.
- Include the change to the data model. Refer to the [Liquibase changetype documentation](https://docs.liquibase.com/change-types/home.html) to learn more about the syntax of what you want to change.
- Add the reference to the created data change log in the master change log, _appending at the end_.

Liquibase will be run at next application startup and the database will be updated correctly. Once the behavior is validated in dev, copy the script for integration environment and production environment if applicable.

## Springdoc-openapi with swagger-ui

- The Swagger UI page should then be available at `${BACKEND_URL}/api/swagger-ui.html
- Documentation will be available at the following url for json format : `${BACKEND_URL}/api/v3/api-docs`
- Documentation will be available at the following url for yaml format : `${BACKEND_URL}/api/v3/api-docs.yaml`

## Good practices document

We have a good practices document available [here](https://capgemini.sharepoint.com/:w:/r/sites/ELIThELponITprojectmanagement/_layouts/15/Doc.aspx?sourcedoc=%7B5775C1E1-62A0-41F1-98E3-B741BCC4A495%7D&file=BonnePratique.docx&action=default&mobileredirect=true). Feel free to update it as needed. 
