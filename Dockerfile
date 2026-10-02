# 1: Compiling app using Maven
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2: Execute app using java
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/HotelBooking/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
