FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src ./src
RUN chmod +x mvnw && ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre
COPY --from=build /app/target/user-service.jar /user-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/user-service.jar"]
