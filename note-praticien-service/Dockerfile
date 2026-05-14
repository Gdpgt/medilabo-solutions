# Stage 1 : Build
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY ./patient-service/pom.xml ./patient-service/
COPY ./note-praticien-service/pom.xml ./note-praticien-service/
COPY ./evaluation-risque-service/pom.xml ./evaluation-risque-service/
# Downloads dependencies and caches them (BuildKit) to avoid redownloading them on every change.
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -pl note-praticien-service
COPY ./note-praticien-service/src ./note-praticien-service/src
RUN --mount=type=cache,target=/root/.m2 mvn package -pl note-praticien-service -DskipTests -o

# Stage 2 : Runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/note-praticien-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]