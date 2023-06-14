FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /workspace/app

COPY pom.xml .
COPY src src

RUN mvn install


FROM maven:3.8.3-openjdk-17
#VOLUME /tmp
ARG JAR_FILE=/workspace/app/target/*.jar
COPY --from=build ${JAR_FILE} web-server.jar
EXPOSE 8443
ENTRYPOINT ["java", "-jar", "/web-server.jar"]