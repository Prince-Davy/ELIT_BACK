FROM maven:3.8-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY log4j.xml .
RUN mkdir -p /app/db
RUN mkdir -p /app/documentation
RUN mkdir /logs
RUN mkdir ./images
RUN touch /logs/java.logs
# RUN mvn dependency:go-offline -B
COPY ./src ./src


# in the process, already tested in qbuild phase, in publish phase this is just preventing integration tests
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-jammy

ENV TZ 'Europe/Paris'
RUN echo $TZ > /etc/timezone


# --- Add: import the certificate into a dedicated truststore and configure the JVM ---
RUN mkdir -p /opt/certs

COPY cert/integ-elit-pmm-pred.crt /opt/certs/

RUN keytool -importcert -noprompt -trustcacerts \
  -alias sogetilabcert \
  -file /opt/certs/integ-elit-pmm-pred.crt \
  -keystore $JAVA_HOME/lib/security/cacerts \
  -storepass 



WORKDIR /app
RUN mkdir -p /app/template
RUN mkdir -p /app/logs
RUN mkdir -p /app/avatars
RUN mkdir -p /app/projects
COPY --from=BUILDER /app/db ./db
COPY --from=BUILDER /app/log4j.xml ./log4j.xml
COPY --from=BUILDER /app/target/*-SNAPSHOT.jar .

# ADD ./filebeat/filebeat.yml /usr/share/filebeat/config/filebeat.yml

CMD ["java", "-Xmx750m", "-jar", "outildedecision-0.0.1-SNAPSHOT.jar"]
