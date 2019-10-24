FROM openjdk:8
#FROM openjdk:8-jre-alpine
ADD /target/lab3-0.0.1-SNAPSHOT.jar lab3.jar
#COPY /classes/artifacts/lab3_jar/lab3.jar /usr/src/lab3/
#WORKDIR /usr/src/lab3
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "lab3.jar"]
#CMD ["java", "-jar", "lab3.jar"]