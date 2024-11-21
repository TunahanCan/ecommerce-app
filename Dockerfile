
FROM eclipse-temurin:21-jdk as build

RUN apt-get update && apt-get install -y maven
WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/target/ecommerce-app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 1616

ENTRYPOINT ["java", "-jar", "app.jar"]