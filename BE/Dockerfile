FROM eclipse-temurin:17-jdk-alpine
WORKDIR /opt/app
RUN addgroup --system javauser && adduser -S -s /usr/sbin/nologin -G javauser javauser
ARG JAR_FILE
COPY target/${JAR_FILE} app.jar
RUN chown -R javauser:javauser .
USER javauser
ENTRYPOINT ["java", "-jar", "app.jar"]