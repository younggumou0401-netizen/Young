FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
# Run the tests as part of the deployment build.
RUN mvn -B -ntp verify

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --uid 10001 --create-home app
COPY --from=build /build/target/staff-ratings-0.0.1-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65 -XX:InitialRAMPercentage=20"
USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
