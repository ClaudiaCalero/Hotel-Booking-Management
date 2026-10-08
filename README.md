<img width="800" height="450" alt="VideoProject4-ezgif com-video-to-gif-converter" src="https://github.com/user-attachments/assets/6daec642-c6a4-4e18-9325-a4ebeacb1986" />
# 🏨 Hotel Booking Management

A full-stack hotel booking management application built with **Java 21**, **Spring Boot** and **React**. The backend provides REST APIs for authentication, user management, room management, booking management and payment processing, and the React frontend is served by the same Spring Boot application.

The project also includes **JWT-based authentication**, **role-based authorization** and a comprehensive automated test suite using **JUnit 5, Mockito and Spring Boot Test**.

---

## 🌐 Live Demo

**Production:** https://hotel-booking-management-yjwq.onrender.com

> ⏳ The app is hosted on a free Render instance. After a period of inactivity it goes to sleep, so the first load can take up to a minute.

### Demo credentials

| Role  | Email           | Password          |
| ----- | --------------- | ----------------- |
| Admin | admin@hotel.com | `AdminHotel2026*` |

You can also register your own customer account from the site.

### Payments (demo mode)

Payments are **simulated** in this public demo: no card data is validated and no real charge is made, so you can enter any values in the payment form. The backend also supports real Stripe payments with server-side verification (see `PAYMENTS_VERIFY_WITH_STRIPE` in the [Configuration](#️-configuration) section).

---

## 🎨 Creative Concept

When I think about the movie ***The Grand Budapest Hotel***, directed by Wes Anderson, I find it curious to imagine what its website would look like.

Originally, this project was created as part of my portfolio. However, while working on the mockup, choosing the visual style, selecting the photos, colors, and overall aesthetic, I couldn't help but think of that movie.

Could it be because I watched it shortly before working on the design? Who knows.

The point is, I find it fun to imagine that, if it had one, **this could be the official website of the Grand Budapest Hotel**.

---

## ✨ Features

### 🔐 Authentication & Security

* User registration and login
* JWT-based authentication
* Password encryption with BCrypt
* Role-based authorization
* `ADMIN` and `CUSTOMER` roles
* Password recovery and password reset
* Stateless Spring Security configuration
* Custom authentication and authorization handling

### 👤 User Management

* Retrieve all users
* Update user information
* Delete users
* Retrieve the authenticated user's account
* Retrieve the user's bookings
* Administrator-only user management operations

### 🛏️ Room Management

* Add rooms
* Update rooms
* Delete rooms
* Retrieve all rooms
* Retrieve a room by ID
* Search rooms
* Retrieve available rooms by date
* Retrieve available room types
* Support for room images
* Room capacity and price management

### 📅 Booking Management

* Create bookings
* Search bookings by reference number
* Update bookings
* Retrieve all bookings
* Booking authorization based on user roles
* Availability validation

### 💳 Payments

* Stripe PaymentIntent integration, with optional server-side verification of the payment
* The public demo runs in simulated payment mode
* Payment status management
* Secure payment configuration through environment variables

### 📱 Notifications

* Twilio integration for SMS/WhatsApp notifications
* External service credentials managed through environment variables

---

## 🛠️ Technologies

### Backend

* Java 21
* Spring Boot 3.4.4
* Spring Security 6
* Spring Data JPA
* Hibernate
* Maven

### Frontend

* React 19
* React Router
* Axios
* Stripe.js / React Stripe.js
* Create React App

### Database

* MySQL

### Security

* JWT
* JJWT
* BCrypt
* Spring Security

### Payments & Notifications

* Stripe
* Twilio

### Testing

* JUnit 5
* Mockito
* Spring Boot Test
* MockMvc
* Spring Security Test

### Infrastructure

* Docker (multi-stage build)
* Render (hosting)
* Aiven (managed MySQL)

### Additional Libraries

* ModelMapper
* Lombok

---

## 🔌 API Endpoints

### Authentication

| Method | Endpoint                    | Description               | Access |
| ------ | --------------------------- | ------------------------- | ------ |
| `POST` | `/api/auth/register`        | Register a new user       | Public |
| `POST` | `/api/auth/login`           | Authenticate a user       | Public |
| `POST` | `/api/auth/forgot-password` | Request password recovery | Public |
| `POST` | `/api/auth/reset-password`  | Reset password            | Public |

### Users

| Method   | Endpoint              | Description                      | Access        |
| -------- | --------------------- | -------------------------------- | ------------- |
| `GET`    | `/api/users/all`      | Retrieve all users               | ADMIN         |
| `PUT`    | `/api/users/update`   | Update user information          | Authenticated |
| `DELETE` | `/api/users/delete`   | Delete a user                    | Authenticated |
| `GET`    | `/api/users/account`  | Retrieve current user's account  | Authenticated |
| `GET`    | `/api/users/bookings` | Retrieve current user's bookings | Authenticated |

### Rooms

| Method   | Endpoint                 | Description              | Access |
| -------- | ------------------------ | ------------------------ | ------ |
| `POST`   | `/api/rooms/add`         | Add a new room           | ADMIN  |
| `PUT`    | `/api/rooms/update`      | Update a room            | ADMIN  |
| `GET`    | `/api/rooms/all`         | Retrieve all rooms       | Public |
| `GET`    | `/api/rooms/{id}`        | Retrieve a room by ID    | Public |
| `DELETE` | `/api/rooms/delete/{id}` | Delete a room            | ADMIN  |
| `GET`    | `/api/rooms/available`   | Retrieve available rooms | Public |
| `GET`    | `/api/rooms/types`       | Retrieve room types      | Public |
| `GET`    | `/api/rooms/search`      | Search for rooms         | Public |

### Bookings

| Method | Endpoint                    | Description                 | Access           |
| ------ | --------------------------- | --------------------------- | ---------------- |
| `GET`  | `/api/bookings/all`         | Retrieve all bookings       | ADMIN            |
| `POST` | `/api/bookings`             | Create a booking            | Public           |
| `GET`  | `/api/bookings/{reference}` | Find a booking by reference | Public           |
| `PUT`  | `/api/bookings/update`      | Update a booking            | ADMIN / CUSTOMER |

Protected endpoints require a valid JWT token in the `Authorization` header:

```text
Authorization: Bearer <token>
```

---

## ⚙️ Configuration

The application reads all sensitive values from environment variables. **Do not commit real credentials, API keys or secrets to GitHub.**

| Variable                     | Description                                                 | Required |
| ---------------------------- | ----------------------------------------------------------- | -------- |
| `SPRING_DATASOURCE_URL`      | JDBC URL, e.g. `jdbc:mysql://localhost:3306/hotel_bookings` | Yes      |
| `SPRING_DATASOURCE_USERNAME` | Database user                                               | Yes      |
| `SPRING_DATASOURCE_PASSWORD` | Database password                                           | Yes      |
| `SECRETEJWTSTRING`           | JWT signing secret (at least 32 characters)                 | Yes      |
| `HOTEL_ADMIN_EMAIL`          | Email that is automatically granted the `ADMIN` role        | Yes      |
| `HOTEL_ADMIN_PASSWORD`       | Password of the default `admin@hotel.com` user              | No\*     |
| `APP_BASE_URL`               | Public URL of the app, used in emails and payment links     | No\*\*   |
| `SPRING_MAIL_HOST`           | SMTP host                                                   | Yes      |
| `SPRING_MAIL_PORT`           | SMTP port (default `587`)                                   | No       |
| `SPRING_MAIL_USERNAME`       | SMTP user                                                   | Yes      |
| `SPRING_MAIL_PASSWORD`       | SMTP password                                               | Yes      |
| `TWILIO_ACCOUNT_SID`         | Twilio account SID                                          | Yes      |
| `TWILIO_AUTH_TOKEN`          | Twilio auth token                                           | Yes      |
| `TWILIO_SMS_NUMBER`          | Twilio SMS number                                           | No       |
| `TWILIO_WHATSAPP_NUMBER`     | Twilio WhatsApp number                                      | No       |
| `STRIPE_API_PUBLIC_KEY`      | Stripe public key (`pk_test_...` for test mode)             | Yes      |
| `STRIPE_API_SECRET_KEY`      | Stripe secret key (`sk_test_...` for test mode)             | Yes      |
| `PAYMENTS_VERIFY_WITH_STRIPE`| `true` verifies each payment with Stripe on the server; `false` (default) is demo mode | No |

\* If not set, the default admin user is not created.
\*\* Defaults to `http://localhost:3000`.

The server port is read from `PORT` (default `8080`).

### 🗄️ Database

The application uses a MySQL database. For a local setup:

```sql
CREATE DATABASE hotel_bookings;
```

```text
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/hotel_bookings
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password
```

Tables are created automatically on startup (`spring.jpa.hibernate.ddl-auto=update`).

When the database requires an encrypted connection (as with Aiven), add `?sslMode=REQUIRED` to the JDBC URL.

### 👑 Administrator

The email set in `HOTEL_ADMIN_EMAIL` is automatically assigned administrator permissions when registering. Additionally, if `HOTEL_ADMIN_PASSWORD` is set, a default `admin@hotel.com` user is created on first startup.

---

## 🐳 Docker & Deployment

The repository includes a multi-stage `Dockerfile` that builds the React frontend, bundles it into the Spring Boot application and produces a single image that serves both the API and the web app:

1. **Frontend:** `npm ci` + `npm run build` (Create React App, Node 20)
2. **Backend:** the build output is copied into `src/main/resources/static` and packaged with Maven (Java 21)
3. **Runtime:** a slim JRE image runs the resulting JAR

Run it locally (create a `.env` file with the variables listed above):

```bash
docker build -t hotel-booking .
docker run -p 8080:8080 --env-file .env hotel-booking
```

The production demo runs on **Render** (free Web Service, Docker runtime) with a free **Aiven MySQL** database. The environment variables are configured in the Render dashboard, and every push to `master` triggers a new deploy.

---

## 🚀 Running the Application

### Prerequisites

Make sure you have installed:

* Java 21
* Maven
* MySQL 8 or compatible version
* Docker (optional, to run the full stack as in production)

### 1. Clone the repository

```bash
git clone https://github.com/ClaudiaCalero/Hotel-Booking-Management.git
cd Hotel-Booking-Management/HotelBooking
```

### 2. Create the database

Create a MySQL database named:

```sql
CREATE DATABASE hotel_bookings;
```

### 3. Configure environment variables

Set the required environment variables described in the [Configuration](#️-configuration) section.

### 4. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

The application runs by default on:

```text
http://localhost:8080
```

---

## 🧪 Testing

The project includes a comprehensive automated test suite covering the main application layers.

### Test Coverage

Tests include:

* Service layer
* Controller layer
* JWT utilities
* JWT authentication filter
* User details service
* Authentication user model
* Spring Security configuration
* CORS configuration
* Application context
* Authentication and authorization scenarios
* Room availability and room management
* Booking operations
* User operations

### Test Result

The complete test suite currently passes with:

```text
118 tests
0 failures
0 errors
0 skipped
```

### Run all tests

From the `HotelBooking` directory:

```bash
mvn test
```

Expected result:

```text
Tests run: 118
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

---

## 🔒 Security

The application uses **Spring Security with JWT authentication**.

The authentication flow is:

```text
User
  │
  ▼
Login / Register
  │
  ▼
JWT Token
  │
  ▼
Authorization Header
  │
  ▼
AuthFilter
  │
  ▼
JWT Validation
  │
  ▼
SecurityContext
  │
  ▼
Protected API Endpoint
```

Protected operations are restricted according to the user's authorities.

For example:

* `ADMIN` users can manage rooms and access administrative booking and user operations.
* `CUSTOMER` users can perform customer-level operations such as creating and updating bookings.

---

## 🛏️ Room Availability

The application provides an endpoint for searching available rooms:

```text
GET /api/rooms/available
```

The endpoint accepts:

* Check-in date
* Check-out date
* Optional room type

Example:

```text
/api/rooms/available?checkInDate=2026-10-01&checkOutDate=2026-10-05&roomType=STANDARD_ROOM
```

The room type can be omitted when availability should be searched across all room types.

---

## ⚠️ Error Handling

The application includes custom exception handling for common API errors, including:

* Resource not found
* Authentication errors
* Authorization errors
* Validation errors
* Invalid requests
* JWT authentication errors

The security configuration also provides custom authentication and access-denied handling.

---

## 🏗️ Development

The project follows a layered Spring Boot architecture:

```text
Controller
    │
    ▼
Service
    │
    ▼
Repository
    │
    ▼
MySQL Database
```

DTOs are used to transfer data between the API and application layers, while **ModelMapper** is used where appropriate to map between DTOs and entities.

---

## 🖼️ Mockup

The initial visual concept for the project was developed in Figma and has since evolved significantly, with several design changes and refinements made throughout the process.

Initial concept:
[**Click to view the Figma project**](https://www.figma.com/design/RiiARqgNRd5CpYm5VHAzvB/OnyxCrownHotel?node-id=0-1&p=f&t=GYhQ6tYGtqEKdx9E-0)

[![OnyxCrownHotel Preview](https://github.com/user-attachments/assets/0e1facdc-86c0-4f44-a4cc-ab175dd31bb4)](https://www.figma.com/design/RiiARqgNRd5CpYm5VHAzvB/OnyxCrownHotel?node-id=0-1&p=f&t=GYhQ6tYGtqEKdx9E-0)

Current Project:
[**Click to view the Figma project**](https://www.figma.com/design/WU4gOPa11P11Mx1P9mLkmD/The-Grand-Hotel-Budapest?node-id=0-1&p=f)

[![The Grand Hotel Budapest Preview](https://github.com/user-attachments/assets/a4bd600e-4980-464e-8a1b-589bd1c5e947)](https://www.figma.com/design/WU4gOPa11P11Mx1P9mLkmD/The-Grand-Hotel-Budapest?node-id=0-1&p=f)

---
## 🎞️ 
Visuals
USER ROUTE falta por convertir video


GUEST ROUTE
<img width="800" height="450" alt="VideoProject21-ezgif com-video-to-gif-converter" src="https://github.com/user-attachments/assets/11bc6a95-3e76-42bb-b00b-84381913c119" />

ADMIN ROUTE
<img width="800" height="450" alt="VideoProject4-ezgif com-video-to-gif-converter" src="https://github.com/user-attachments/assets/7910a938-eaba-404b-86ae-3f88ec1b2421" />

USER ROUTE
<img width="800" height="450" alt="ezgif com-video-to-gif-converter (1)" src="https://github.com/user-attachments/assets/6511d33c-37a9-4fe6-beb7-865b721b10ae" />





## 🔮 Future Improvements

Possible future improvements include:

* API documentation with OpenAPI/Swagger
* Integration and end-to-end testing with a dedicated test database
* CI/CD pipeline with GitHub Actions
* Improved validation responses and API error formats
* Additional booking and payment features
* Email delivery through an HTTPS-based provider (SMTP is blocked on the free hosting tier)
* Persistent storage for uploaded room images
* Re-enable real Stripe payments in the frontend and set `PAYMENTS_VERIFY_WITH_STRIPE=true`

---

## 👩‍💻 Author

**Clàudia Calero**

[GitHub](https://github.com/ClaudiaCalero)

[LinkedIn](https://www.linkedin.com/in/claudia-calero/)


---

## 📄 License

This project is for educational and development purposes.

Bye!

