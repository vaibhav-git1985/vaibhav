FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src ./src
RUN chmod +x mvnw && ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre
COPY --from=build /app/target/PRODUCT-SERVICE.jar /PRODUCT-SERVICE.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/PRODUCT-SERVICE.jar"]
