FROM maven:3.10.0-eclipse-temurin-17

WORKDIR /app

COPY . .

RUN mvn -f HotelBooking/pom.xml clean package -DskipTests