FROM eclipse-temurin:17-jre-alpine as runtime

WORKDIR /app

ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]


