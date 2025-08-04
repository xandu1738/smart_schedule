FROM node:20 AS frontend-builder

WORKDIR /app/front
COPY front/package*.json ./
RUN npm install
COPY front/ ./
RUN npm run build

FROM maven:3.9-eclipse-temurin-17 AS backend-builder

WORKDIR /app

COPY ./Moonlight-1.0.jar ./lib/

RUN mvn install:install-file \
    -Dfile=lib/Moonlight-1.0.jar \
    -DgroupId=com.jmsoft \
    -DartifactId=Moonlight \
    -Dversion=1.0 \
    -Dpackaging=jar

COPY pom.xml ./
COPY src ./src
COPY target ./target
COPY --from=frontend-builder /app/front/dist ./src/main/resources/static

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=backend-builder /app/target/smart-scheduler.jar .

ENV SPRING_PROFILE=dev \
    DB_URL=jdbc:postgresql://localhost:5432/smart_system \
    DB_PASSWORD=1234 \
    DB_USERNAME=postgres \
    SERVER_PORT=7005

CMD ["sh", "-c", "java -Dspring.profiles.active=$SPRING_PROFILE -Dspring.datasource.url=$DB_URL -Dspring.datasource.password=$DB_PASSWORD -Dspring.datasource.username=$DB_USERNAME -Dserver.port=$SERVER_PORT -Xms256m -Xmx256m -jar smart-scheduler.jar"]
