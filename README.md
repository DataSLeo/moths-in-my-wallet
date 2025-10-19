# Moths In My Wallet 🪰

Moths in My Wallet is a website designed for personal financial organization.

> The idea for the name came from the phrase "moths in my wallet", which implies that you have no money and therefore need help with your personal financial organization.


## Tech Stack 

- Open JDK 21+
- Spring Boot 3.5.3
- MySQL
- Thymeleaf
- Maven


## Database

Script to create the database
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
CREATE DATABASE mothsinmywallet;
```


### Configure .env's file

In the project's root directory, create a `.env` file and add the following content:

```env
DB_URL=jdbc:mysql://localhost::3306/mothsinmywallet 
DB_USER=your_user_of_mysql
DB_PASSSWORD=your_password_of_mysql
```

Note:

- Make sure the port (3306) matches your MySQL Server configuration.
- Check that the `.env` file is listed in your `.gitignore` (so your credentials aren’t uploaded to GitHub).


### Run

```bash
./mvnw spring-boot:run
```

And open in your browser the link http://localhost:8080/.