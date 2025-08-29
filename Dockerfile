FROM maven:3.6.2-jdk-11 AS BUILDER
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


FROM openjdk:11-jre-slim

ENV TZ 'Europe/Paris'
RUN echo $TZ > /etc/timezone

# Install filebeat
RUN apt update
RUN apt install apt-transport-https supervisor wget gnupg gnupg2 gnupg1 -y
# RUN wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | apt-key add -
# RUN echo "deb https://artifacts.elastic.co/packages/7.x/apt stable main" | tee -a /etc/apt/sources.list.d/elastic-7.x.list
# RUN apt update
# RUN apt install filebeat -y

# Clean up
RUN apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*

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
