FROM openjdk:17-jdk-alpine AS build
WORKDIR /workspace/app

COPY pom.xml .
COPY src src

RUN mvn install


FROM openjdk:17-jdk-alpine AS build
#VOLUME /tmp
ARG JAR_FILE=/workspace/app/target/*.jar
COPY --from=build /bin/cat /bin/cat
COPY --from=build ${JAR_FILE} web-server.jar
EXPOSE 8443
ENTRYPOINT ["java", "-jar", "/web-server.jar"]