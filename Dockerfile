FROM maven:3.8-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY ./src ./src

# in the process, already tested in qbuild phase, in publish phase this is just preventing integration tests
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-jammy

ENV TZ 'Europe/Paris'
RUN echo $TZ > /etc/timezone

RUN apt-get update \
  && apt-get install -y --only-upgrade libssl3 openssl \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*

# --- Import the prediction service certificate into the JVM truststore ---
COPY cert/integ-elit-pmm-pred.crt /opt/certs/
RUN keytool -importcert -noprompt -trustcacerts \
  -alias sogetilabcert \
  -file /opt/certs/integ-elit-pmm-pred.crt \
  -keystore $JAVA_HOME/lib/security/cacerts \
  -storepass 

WORKDIR /app
RUN mkdir -p /app/template /app/logs /app/avatars /app/projects /app/db
COPY log4j.xml ./log4j.xml
COPY --from=builder /app/target/*-SNAPSHOT.jar .

CMD ["java", "-Xmx750m", "-jar", "outildedecision-0.0.1-SNAPSHOT.jar"]
