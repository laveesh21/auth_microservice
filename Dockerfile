# DEVELOPMENT DOCKERFILE
# Fast builds for rapid iteration

FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre-noble
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

# Simple health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD java -cp app.jar org.springframework.boot.loader.launch.JarLauncher || exit 1

# Basic run command
ENTRYPOINT ["java", "-jar", "app.jar"]
