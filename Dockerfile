FROM openjdk:19-jdk-alpine3.16
ARG JAR_FILE=build/libs/backend-*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
