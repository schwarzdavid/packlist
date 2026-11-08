FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /build

COPY mvnw ./
COPY .mvn/ ./.mvn/
RUN ./mvnw --version

COPY pom.xml ./
COPY backend-common/pom.xml ./backend-common/pom.xml
COPY backend-common-test/pom.xml ./backend-common-test/pom.xml
COPY user-service/pom.xml ./user-service/
COPY user-api/pom.xml ./user-api/
COPY user-client-java/pom.xml ./user-client-java/
COPY list-service/pom.xml ./list-service/
COPY list-api/pom.xml ./list-api/

RUN ./mvnw -B -pl list-service -am dependency:go-offline -DexcludeGroupIds=app.schwarzdavid.packlist

COPY list-service/src/ ./list-service/src/
COPY list-api/src/ ./list-api/src/
COPY backend-common/src/ ./backend-common/src/

RUN ./mvnw -B -pl list-service -am package -Dmaven.test.skip=true

FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

ENV HTTP_PORT=8871

RUN addgroup -g 10001 -S service && adduser -u 10001 -S -G service -H -D service
USER service

COPY --from=builder /build/list-service/target/lib lib/
COPY --from=builder /build/list-service/target/lib-internal lib/
COPY --from=builder /build/list-service/target/list-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8871
ENTRYPOINT ["java", "-jar", "app.jar"]