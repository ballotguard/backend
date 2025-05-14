FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy only pom.xml and download dependencies first (better caching)
COPY pom.xml .
RUN mvn -B dependency:resolve dependency:resolve-plugins

# Then copy the rest of the project
COPY src ./src

# Build the app, skip tests
RUN mvn clean package -DskipTests

# === Stage 2: Run the application ===
FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY --from=build /app/target/ballotguard-0.0.1-SNAPSHOT.jar demo.jar

EXPOSE 8089
ENTRYPOINT ["java", "-jar", "demo.jar"]