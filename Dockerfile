# 1: Compiling app using Maven
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


# 2: Execute app using java
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/HotelBooking/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]



