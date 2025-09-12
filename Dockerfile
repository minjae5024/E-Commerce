FROM gradle:8.5.0-jdk21-alpine AS build

WORKDIR /home/gradle/src

COPY build.gradle settings.gradle gradlew gradlew.bat /home/gradle/src/
COPY gradle /home/gradle/src/gradle
COPY src /home/gradle/src/src

RUN chmod +x ./gradlew
RUN ./gradlew build -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]