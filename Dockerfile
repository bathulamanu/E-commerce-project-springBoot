FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.war app.war

EXPOSE 8082

ENTRYPOINT ["java","-jar","app.war"]
