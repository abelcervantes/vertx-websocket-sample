# build
FROM maven:3.6.0-jdk-8-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

# run
FROM openjdk:8-jre-slim
COPY --from=build /home/app/target/server-1.0-SNAPSHOT-fat.jar /usr/local/lib/ws.jar
EXPOSE 5050
ENTRYPOINT ["java","-jar","/usr/local/lib/ws.jar"]