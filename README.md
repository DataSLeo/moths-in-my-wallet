# Moths In My Wallet 🪰

Moths in My Wallet is a website designed for personal financial organization.

> The idea for the name came from the phrase "moths in my wallet", which implies that you have no money and therefore need help with your personal financial organization.


## Tech Stack 

- Open JDK 21
- Spring Boot 3.5.3
- Thymeleaf
- Spring Security
- Spring MVC
- Dev tools
- MySQL
- H2 Database
- Maven


## Database

Script to create the database - SQL
[`database/schema_mothsinmywallet.sql`](database/schema_mothsinmywallet.sql)

Entity-Relationship Model - PDF
[`docs/erm_mothsinmywallet.pdf`](docs/erm_mothsinmywallet.pdf)

Entity-Relationship Model - MWB
[`docs/erm_mothsinmywallet.mwb`](docs/erm_mothsinmywallet.mwb)


## How to setup


### Clone the repository

```bash
git clone https://github.com/DataSLeo/moths-in-my-wallet.git
cd moths-in-my-wallet/
```

### Configure database 

Create the database `mothsinmywallet` in your MySQL Server.

```sql
CREATE DATABASE mothsinmywallet CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```


### Configure secrets.properties's file

In the project's root directory, create a `secrets.properties` file and add the following content:

```env
db.connection.url=jdbc:mysql://localhost:3306/mothsinmywallet?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
db.connection.user=your_user
db.connection.password=your_password
```

Note:

- Make sure the port (3306) matches your MySQL Server configuration.


### Run

```bash
./mvnw spring-boot:run
```

And open in your browser the link http://localhost:8080/.