FROM openjdk:8
#FROM openjdk:8-jre-alpine
ADD ./target/artifacts/progetto_2018_jar/progetto_2018.jar entrypoint.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "entrypoint.jar"]