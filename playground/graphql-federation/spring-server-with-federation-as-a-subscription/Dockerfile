FROM openjdk:17-alpine
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar
RUN mv build/libs/*.jar build/libs/app.jar
ENTRYPOINT ["java", "-jar", "build/libs/app.jar"]
