## 🧪 API Testing (Postman)

### 1️⃣ User HTTP Requests

**Register a New User**
`POST: localhost:9090/api/auth/register`

<details>
<summary>View Images</summary>

![Register](https://github.com/user-attachments/assets/eb07578f-84aa-43ae-8271-4987456a11fc)
![Register Example](https://github.com/user-attachments/assets/aaff5054-303f-428c-a68c-e2e100b2ece0)

</details>

**Register Without Role**
`POST: localhost:9090/api/auth/register`

<details>
<summary>View Images</summary>

![No Role](https://github.com/user-attachments/assets/846895d1-f02f-42c9-bfef-5347fbe76b54)
![No Role Example](https://github.com/user-attachments/assets/dbbb8505-35f3-447a-a760-e802666d5221)

</details>

**Login**
`POST: localhost:9090/api/auth/login`

<details>
<summary>View Image</summary>

![Login](https://github.com/user-attachments/assets/85c51d67-d451-49d3-a328-301ff82e87cf)

</details>

---

### 2️⃣ User Management (Admin Only)

* **Get All Users**: `GET /api/users/all`

<details>
<summary>View Image</summary>

![Get All Users](https://github.com/user-attachments/assets/56336e62-23a3-414c-a595-0b23dcc5b092)

</details>

* **Update User**: `PUT /api/users/update`

<details>
<summary>View Images</summary>

![Update User](https://github.com/user-attachments/assets/802f0e37-de5c-4264-b296-6a79574fe25f)
![Update Example](https://github.com/user-attachments/assets/cb667a36-5dff-40fe-81c0-f11c393e86cb)

</details>

* **Delete User**: `DELETE /api/users/delete`

<details>
<summary>View Images</summary>

![Delete User](https://github.com/user-attachments/assets/1ab7abf5-16c1-4156-8ecb-147b4006379f)
![Delete Example](https://github.com/user-attachments/assets/ad354919-0103-4b43-ab07-d85335c793d0)

</details>

* **Get My Account**: `GET /api/users/account`

<details>
<summary>View Image</summary>

![Get Account](https://github.com/user-attachments/assets/f4b144b9-07eb-41c8-b952-e4c651941821)

</details>

* **My Bookings**: `GET /api/users/bookings` (no data yet)

<details>
<summary>View Image</summary>

![My Bookings](https://github.com/user-attachments/assets/3bb0c83a-504b-43f2-89e3-0ba51211b523)

</details>

---

### 3️⃣ Room HTTP Requests

* **Add Room** (`POST /api/rooms/add`) – Requires token

<details>
<summary>View Images</summary>

![Add Room](https://github.com/user-attachments/assets/b0453c28-c1ad-4652-893a-ee3ebe452210)
![Add Room Example](https://github.com/user-attachments/assets/0654c45b-96d5-47df-a055-058c9d9f43b2)

</details>

* **Update Room** (`PUT /api/rooms/update`)

<details>
<summary>View Image</summary>

![Update Room](https://github.com/user-attachments/assets/af36ad3a-eb09-44db-803d-9f564e0cd947)

</details>

* **Get All Rooms** (`GET /api/rooms/all`)

<details>
<summary>View Image</summary>

![All Rooms](https://github.com/user-attachments/assets/a8b2954f-0076-4877-95ff-dec79e8e6197)

</details>

* **Get Room by ID** (`GET /api/rooms/{id}`)

<details>
<summary>View Image</summary>

![Room By ID](https://github.com/user-attachments/assets/22f586d1-dbc9-468f-9b17-d2620accd393)

</details>

* **Delete Room by ID** (`DELETE /api/rooms7delete/{id}`)

<details>
<summary>View Image</summary>

![Delete Room](https://github.com/user-attachments/assets/bf97b0fb-8e04-4a09-aace-8b4ba7164abb)

</details>

* **Get Available Rooms** (`GET /api/rooms/available`)

<details>
<summary>View Image</summary>

![Available Rooms](https://github.com/user-attachments/assets/b2d92f64-e236-4480-93ec-caaf93af48fd)

</details>

* **Get Room Types** (`GET /api/rooms/types`)

<details>
<summary>View Image</summary>

![Room Types](https://github.com/user-attachments/assets/469b79cf-bb50-42ff-af30-d8cd4bb9629a)

</details>

* **Search Rooms** (`GET /api/rooms/search`) – Input: `SINGLE`, `DOUBLE`, `TRIPLE`, `SUITE`

<details>
<summary>View Image</summary>

![Search Room](https://github.com/user-attachments/assets/bd97492c-41ef-4eb7-a85a-7ca7c67676c4)

</details>




