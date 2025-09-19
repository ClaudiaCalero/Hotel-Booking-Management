## 🖼️ MockUp
[Click to view the Figma project](https://www.figma.com/design/RiiARqgNRd5CpYm5VHAzvB/OnyxCrownHotel?node-id=0-1&p=f&t=GYhQ6tYGtqEKdx9E-0)

[![OnyxCrownHotel Preview](https://github.com/user-attachments/assets/0e1facdc-86c0-4f44-a4cc-ab175dd31bb4)](https://www.figma.com/design/RiiARqgNRd5CpYm5VHAzvB/OnyxCrownHotel?node-id=0-1&p=f&t=GYhQ6tYGtqEKdx9E-0)

## 🧪 API Testing (Postman)

---
> ⚠️ **Authorization Required**  
> For **all requests**, you need an **authorization token (Bearer Token)**.  
> This token is obtained when an **admin** registers (`/api/auth/register`) and then logs in (`/api/auth/login`).  
> Once retrieved, you must add it in Postman under **Authorization → Bearer Token**.
---

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

**Get All Users**  
`GET /api/users/all`

<details>
<summary>View Image</summary>

![Get All Users](https://github.com/user-attachments/assets/56336e62-23a3-414c-a595-0b23dcc5b092)

</details>

**Update User**  
`PUT /api/users/update`

<details>
<summary>View Images</summary>

![Update User](https://github.com/user-attachments/assets/802f0e37-de5c-4264-b296-6a79574fe25f)  
![Update Example](https://github.com/user-attachments/assets/cb667a36-5dff-40fe-81c0-f11c393e86cb)

</details>

**Delete User**  
`DELETE /api/users/delete`

<details>
<summary>View Images</summary>

![Delete User](https://github.com/user-attachments/assets/1ab7abf5-16c1-4156-8ecb-147b4006379f)  
![Delete Example](https://github.com/user-attachments/assets/ad354919-0103-4b43-ab07-d85335c793d0)

</details>

**Get My Account**  
`GET /api/users/account`

<details>
<summary>View Image</summary>

![Get Account](https://github.com/user-attachments/assets/f4b144b9-07eb-41c8-b952-e4c651941821)

</details>

**My Bookings**  
`GET /api/users/bookings` (no data yet)

<details>
<summary>View Image</summary>

![My Bookings](https://github.com/user-attachments/assets/3bb0c83a-504b-43f2-89e3-0ba51211b523)

</details>

---

### 4️⃣ Booking HTTP Requests

**Create Booking**  
`POST /api/bookings/create`

<details>
<summary>View Image</summary>

![Create Booking](https://github.com/user-attachments/assets/6c5b7dbf-58bf-4275-829f-1a4324c957ac)

</details>

**Get All Bookings**  
`GET /api/bookings/all`

<details>
<summary>View Image</summary>

![Get All Bookings](https://github.com/user-attachments/assets/d9694439-16ef-413d-b844-2f667b0c8df7)

</details>

**Find Booking by Reference Number**  
`GET /api/bookings/{reference}`

<details>
<summary>View Image</summary>

![Find Booking](https://github.com/user-attachments/assets/c3e4e70f-c3c3-4e49-b4ee-a9e02477ca4f)

</details>

> 💡 You need to do **Get All Bookings** first in order to find the reference number.

<details>
<summary>Example Response</summary>

<img width="319" height="25" alt="image" src="https://github.com/user-attachments/assets/74865085-b099-4fd5-9881-9cc3cbe8b4e8" />

</details>

**Update Booking Status**  
`PUT /api/bookings/update`

<details>
<summary>View Images</summary>

![Update Booking](https://github.com/user-attachments/assets/fa37ac1c-8b9e-49f7-b68c-1c0bcb3a728b)  

</details>

<details>
<summary> Payment Email Example</summary>

![Update Example](https://github.com/user-attachments/assets/80ae03b8-00e6-4703-ace0-363f506d8ff5)

</details>




