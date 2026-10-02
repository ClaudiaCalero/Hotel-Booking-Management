FROM maven:3.10.0-eclipse-temurin-21

WORKDIR /app

COPY . .

RUN mvn -f HotelBooking/pom.xml clean package -DskipTests

CMD ["java", "-jar", "HotelBooking/target/HotelBooking-0.0.1-SNAPSHOT.jar"]