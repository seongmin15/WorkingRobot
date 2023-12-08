FROM openjdk:20-jdk
ARG JAR_FILE=server/build/libs/*.jar
COPY ${JAR_FILE} working-robot.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "working-robot.jar"]