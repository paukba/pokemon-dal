Pokemon Card Manager/Organizer

Code written by ChatGPT, slightly adjusted by me

In order to run the code in a terminal, I had to use the command: <br/>
mvn exec:java "-Dexec.mainClass=com.example.pokemon.Main" <br/>

In order to run the code to set up the web client, I had to use the command: <br/>
mvn clean compile exec:java <br/>
The website is http://localhost:4567/index.html when it is set up.

It might work otherwise, but I am not sure.

Architecture:

Web Client (index.html + app.js)
        ↓ HTTP (JSON)
Spark REST API (PokemonServiceApp)
        ↓
Service Layer (Business Logic + Validation + Transactions)
        ↓
DAO Layer (JDBC)
        ↓
MySQL Database (pokemon_collection)
