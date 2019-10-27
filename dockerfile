FROM openjdk:8
#FROM openjdk:8-jre-alpine
ADD ./target/progetto_2018-0.0.1-SNAPSHOT.jar entrypoint.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "entrypoint.jar"]