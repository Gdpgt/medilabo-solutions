# Stage 1 : Build
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY ./patient-service/pom.xml ./patient-service/
COPY ./note-praticien-service/pom.xml ./note-praticien-service/
COPY ./evaluation-risque-service/pom.xml ./evaluation-risque-service/
COPY ./frontend-service/pom.xml ./frontend-service/
COPY ./api-gateway/pom.xml ./api-gateway/
# Downloads dependencies and caches them (BuildKit) to avoid redownloading them on every change.
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -pl frontend-service
COPY ./frontend-service/src ./frontend-service/src
RUN --mount=type=cache,target=/root/.m2 mvn package -pl frontend-service -DskipTests -o

# Stage 2 : Runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/frontend-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]