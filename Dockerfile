FROM maven:3.8.7-amazoncorretto-11

WORKDIR /app-backend
COPY . .
RUN mvn clean package -Dmaven.test.skip

CMD ["java", "-jar", "target/outildedecision-0.0.1-SNAPSHOT.jar"]