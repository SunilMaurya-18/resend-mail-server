# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# Cache dependencies separately from source for faster rebuilds
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-jammy

RUN useradd --create-home --shell /usr/sbin/nologin appuser

WORKDIR /app

COPY --from=build /build/target/resend-mail-server.jar app.jar

RUN chown -R appuser:appuser /app
USER appuser

# Render assigns the actual port at runtime via the PORT env var;
# this is just documentation for local `docker run -p`.
EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
