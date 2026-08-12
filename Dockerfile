# ---- Build ----
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY mvnw ./
COPY .mvn ./.mvn
COPY pom.xml ./

RUN chmod +x mvnw \
    && ./mvnw -B -q dependency:go-offline -DskipTests

COPY src ./src

RUN ./mvnw -B -q clean package -DskipTests \
    && APP_JAR="$(ls target/*.jar | grep -v '\.original$' | head -n 1)" \
    && cp "$APP_JAR" /app/application.jar

# ---- Runtime ----
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

COPY --from=build --chown=spring:spring /app/application.jar ./application.jar

USER spring:spring

EXPOSE 8160

ENTRYPOINT ["java", "-jar", "application.jar"]
