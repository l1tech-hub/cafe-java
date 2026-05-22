FROM gradle:8.7-jdk21 AS build

WORKDIR /build

COPY . .

RUN ./gradlew clean bootJar --no-daemon


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

