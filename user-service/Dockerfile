FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /build

COPY mvnw .mvn/ ./
RUN mvnw --version

COPY pom.xml ./
COPY user-service/pom.xml ./user-service/
COPY user-api/pom.xml ./user-api/
COPY user-client-java/pom.xml ./user-client-java/
COPY backend-common/pom.xml ./backend-common/

RUN mvnw -B -pl user-service -am dependency:go-offline -DexcludeGroupIds=app.schwarzdavid.packlist

COPY user-service/src/ ./user-service/src/
COPY user-api/src/ ./user-api/src/
COPY backend-common/src/ ./backend-common/src/

RUN mvnw -B -pl user-service -am package -Dmaven.test.skip=true

FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

ENV HTTP_PORT=8881

COPY --from=builder /build/user-service/target/lib lib/
COPY --from=builder /build/user-service/target/lib-internal lib/
COPY --from=builder /build/user-service/target/user-service-1.0-SNAPSHOT.jar app.jar

USER service

EXPOSE 8881
ENTRYPOINT ["java", "-jar", "app.jar"]