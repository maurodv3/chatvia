FROM openjdk:17

RUN ls
COPY build/libs/app.jar app.jar

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=server","/app.jar"]