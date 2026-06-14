FROM eclipse-temurin:21-jre

COPY target/missions-back-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]