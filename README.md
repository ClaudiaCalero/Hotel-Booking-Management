# 🏨 Hotel Booking Management

A hotel booking management backend developed with **Java 21** and **Spring Boot**. The application provides REST APIs for authentication, user management, room management, booking management and payment processing.

The project also includes **JWT-based authentication**, **role-based authorization** and a comprehensive automated test suite using **JUnit 5, Mockito and Spring Boot Test**.

---

## 🌐 Live Demo

The application is deployed and available online:

[**Visit the deployed application**](https://hotel-booking-management-thegrandho-phi.vercel.app/)

> 💳 **Test payment:** The application uses Stripe in test mode. No real payments are processed. To test the payment flow, use the Stripe test card `4242 4242 4242 4242`, with any future expiry date, any 3-digit CVC and any valid ZIP/postal code.

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

* Stripe payment integration
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

The application uses environment variables for sensitive configuration values.

Create the required environment variables before starting the application.

### 🗄️ Database

The application uses a MySQL database named:

```text
hotel_bookings
```

The default configuration expects:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hotel_bookings
spring.datasource.username=root
spring.datasource.password=
```

Update the database credentials according to your local MySQL configuration.

### 🔑 JWT

```text
JWT_SECRET=your_secure_jwt_secret
```

The JWT secret should be kept private and should **never be committed to the repository**.

### 👑 Administrator

```text
HOTEL_ADMIN_EMAIL=admin@example.com
```

The configured email address is automatically assigned administrator permissions according to the application's user management logic.

### 📧 Email

```text
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_email_password
```

### 📱 Twilio

```text
TWILIO_ACCOUNT_SID=your_account_sid
TWILIO_AUTH_TOKEN=your_auth_token
TWILIO_SMS_NUMBER=your_sms_number
TWILIO_WHATSAPP_NUMBER=your_whatsapp_number
```

### 💳 Stripe

```text
STRIPE_PUBLIC_KEY=your_stripe_public_key
STRIPE_SECRET_KEY=your_stripe_secret_key
```

> ⚠️ **Do not commit real credentials, API keys or secrets to GitHub.**

### 💳 Stripe Test Payments

The deployed application uses **Stripe test mode**, so no real payments are processed.

To test the payment flow, use Stripe's test card:

```text
Card number: 4242 4242 4242 4242
Expiry date: Any future date
CVC: Any 3-digit number
ZIP / Postal code: Any valid value
```

This test card can be used to simulate a successful payment without making a real charge.

---

## 🚀 Running the Application

### Prerequisites

Make sure you have installed:

* Java 21
* Maven
* MySQL 8 or compatible version

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

Configure the database credentials in `application.properties` or through your environment.

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
96 tests
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
Tests run: 96
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

## 🔮 Future Improvements

Possible future improvements include:

* API documentation with OpenAPI/Swagger
* Integration and end-to-end testing with a dedicated test database
* Docker support
* CI/CD pipeline with GitHub Actions
* Improved validation responses and API error formats
* Additional booking and payment features
* Production deployment configuration

---

## 👩‍💻 Author

**Clàudia Calero**

[GitHub](https://github.com/ClaudiaCalero)

[LinkedIn](https://www.linkedin.com/in/claudia-calero/)

---

## 📄 License

This project is for educational and development purposes.


