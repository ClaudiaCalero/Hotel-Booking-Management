# 1) Frontend (Create React App)
FROM node:20-alpine AS frontend
WORKDIR /frontend
COPY hotel-frontend/package*.json ./
RUN npm ci
COPY hotel-frontend/ ./
ENV GENERATE_SOURCEMAP=false
RUN npm run build

# 2) Backend
FROM maven:3.9-eclipse-temurin-21 AS backend
WORKDIR /app
COPY HotelBooking/ HotelBooking/
# Reemplaza el build subido a mano por uno recién compilado
RUN rm -rf HotelBooking/src/main/resources/static
COPY --from=frontend /frontend/build HotelBooking/src/main/resources/static
RUN mvn -f HotelBooking/pom.xml clean package -DskipTests

# 3) Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend /app/HotelBooking/target/*.jar app.jar
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
CMD ["java", "-jar", "app.jar"]