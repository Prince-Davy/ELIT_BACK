#

**🚩 Documentation :** Connect yourself to the [SogetiLab's wiki](https://wiki.innovation-factory.io/en/home) where all the useful documentation is centralized.

## Springdoc-openapi with swagger-ui

- The Swagger UI page should then be available at `${BACKEND_URL}/api/swagger-ui.html`
- Documentation will be available at the following url for json format :               `${BACKEND_URL}/api/v3/api-docs`
- Documentation will be available at the following url for yaml format : `              ${BACKEND_URL}/api/v3/api-docs.yaml`

## Launch the application in development mode in the same way as in production mode

- Create an .env file from the .env.tpl file and Uncomment rhe following variables :
  # MYSQL_URL=db
  # SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/outilprisedecision

- Lancer la commande docker-compose suivante :
  docker-compose -f dev.docker-compose.yml up --build --force-recreate --remove-orphans
- You will have 3 docker containers, the back, the DB and an Adminer container for database management.

## Users local account

 Id | First name | Last name | Login                 | Password     
:---|:-----------|:----------|:----------------------|:-------------
 1  | John       | Doe       | user@user.com         | Azerty1234!? 
 2  | Nancy      | Michel    | admin@elite.fr        | Azerty1234!? 
 3  | Charles    | Baker     | contributer@elit.com  | Azerty1234!? 
 4  | Rochelle   | Wright    | observer@elit.com     | Azerty1234!? 


## Init project
`mvn clean install`

## Start project 
`bash spring-boot:run`