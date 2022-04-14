FROM openjdk:17
MAINTAINER mauro

COPY build/libs/app.jar app.jar

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=client","/app.jar"]