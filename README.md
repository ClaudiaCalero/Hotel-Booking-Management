## Postman 
### USER HTTP REQUEST

REGISTER
POST: localhost:9090/api/auth/register
![image](https://github.com/user-attachments/assets/eb07578f-84aa-43ae-8271-4987456a11fc)

![image](https://github.com/user-attachments/assets/aaff5054-303f-428c-a68c-e2e100b2ece0)

POST without role: localhost:9090/api/auth/register
![image](https://github.com/user-attachments/assets/846895d1-f02f-42c9-bfef-5347fbe76b54)

![image](https://github.com/user-attachments/assets/dbbb8505-35f3-447a-a760-e802666d5221)

LOGIN
POST: localhost:9090/api/auth/login
![image](https://github.com/user-attachments/assets/85c51d67-d451-49d3-a328-301ff82e87cf)

USERS
GET all users ONLY using ADMIN token: localhost:9090/api/users/all
![image](https://github.com/user-attachments/assets/56336e62-23a3-414c-a595-0b23dcc5b092)

Update user using TOKEN, PUT: localhost:9090/api/users/update
![image](https://github.com/user-attachments/assets/802f0e37-de5c-4264-b296-6a79574fe25f)

![image](https://github.com/user-attachments/assets/cb667a36-5dff-40fe-81c0-f11c393e86cb)

Delete user, DELETE: localhost:9090/api/users/delete
![image](https://github.com/user-attachments/assets/1ab7abf5-16c1-4156-8ecb-147b4006379f)

![image](https://github.com/user-attachments/assets/ad354919-0103-4b43-ab07-d85335c793d0)

GetMyAccount using TOKEN of the user
GET: localhost:9090/api/users/account
![image](https://github.com/user-attachments/assets/f4b144b9-07eb-41c8-b952-e4c651941821)

MyBookings I don´t have any sort of info in there yet
GET: localhost:9090/api/users/bookings
![image](https://github.com/user-attachments/assets/3bb0c83a-504b-43f2-89e3-0ba51211b523)

### ROOM HTTP REQUEST
AddRoom remember to use token
POST: localhost:9090/api/rooms/add 
<img width="966" height="696" alt="image" src="https://github.com/user-attachments/assets/b0453c28-c1ad-4652-893a-ee3ebe452210" />

<img width="1457" height="125" alt="image" src="https://github.com/user-attachments/assets/0654c45b-96d5-47df-a055-058c9d9f43b2" />

UpdateRoom
PUT localhost:9090/api/rooms/update
<img width="987" height="753" alt="image" src="https://github.com/user-attachments/assets/af36ad3a-eb09-44db-803d-9f564e0cd947" />

All Rooms
GET: localhost:9090/api/rooms/all
<img width="1052" height="802" alt="image" src="https://github.com/user-attachments/assets/a8b2954f-0076-4877-95ff-dec79e8e6197" />

Room By id
GET: localhost:9090/api/rooms/{id}
<img width="925" height="710" alt="image" src="https://github.com/user-attachments/assets/22f586d1-dbc9-468f-9b17-d2620accd393" />

Delete room by id
DELETE: localhost:9090/api/rooms7delete/{id}
<img width="967" height="650" alt="image" src="https://github.com/user-attachments/assets/bf97b0fb-8e04-4a09-aace-8b4ba7164abb" />

Available rooms
GET: localhost:9090/api/rooms/available
<img width="1100" height="852" alt="image" src="https://github.com/user-attachments/assets/b2d92f64-e236-4480-93ec-caaf93af48fd" />

typerooms
GET: localhost:9090/api/rooms/types
<img width="678" height="688" alt="image" src="https://github.com/user-attachments/assets/469b79cf-bb50-42ff-af30-d8cd4bb9629a" />

search Room remember to put an input (SINGLE, DOUBLE, TRIPLE, SUITE)
GET: localhost:9090/api/rooms/search
<img width="952" height="832" alt="image" src="https://github.com/user-attachments/assets/bd97492c-41ef-4eb7-a85a-7ca7c67676c4" />




