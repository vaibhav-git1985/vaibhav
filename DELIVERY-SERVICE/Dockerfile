FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src ./src
RUN chmod +x mvnw && ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre
COPY --from=build /app/target/delivery-service.jar /delivery-service.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/delivery-service.jar"]
