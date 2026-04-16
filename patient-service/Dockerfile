# Stage 1 : Build
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY ./patient-service/pom.xml ./patient-service/
COPY ./patient-service/src ./patient-service/src
RUN mvn clean package -pl patient-service -DskipTests

# Stage 2 : Runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/patient-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]