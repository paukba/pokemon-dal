Pokémon Card Collection Manager
Deployment Document

## 1. Project Summary

    This project is a Pokémon Card Collection Manager application built using:
        - Java
        - MySQL
        - Maven
        - JDBC
        - Spark Java
        - Bootstrap and JavaScript for the client UI

    The application contains:

        - Data Access Layer (DAO)
        - Business/Service Layer
        - REST API layer
        - Web client served from the application

    Users are able to:

        - View owners, cards, collections, and trades
        - Compute owner collection values
        - Create and accept trades


2. Prerequisites

    Before running these must be installed:

        - Java 21+
        - Maven 3.8+ 
        - MySQL Server
        - A code editor or IDE, such as VS Code
        - A browser


3. Downloading the Project

    1. Go to the GitHub repository.
    2. Click **Code**.
    3. Select **Download ZIP**.
    4. Extract the ZIP file.
    5. Open a terminal in the extracted project folder.

    The folder should contain:

        - pom.xml
        - src/
        - .sql files


4. Database Setup

    1. Run these files in MySQL:

            1. create_schemas.sql
            2. insert_test_data.sql
       OR in the terminal:

            1. mysql -u root -p < create_schemas.sql
            2. mysql -u root -p < insert_test_data.sql


5. Database Configuration

    Option 1: Local Development
        1. Create: src/main/resources/db.properties
        2. Add in that file:

            db.url=jdbc:mysql://localhost:3306/pokemon_collection?useSSL=false&serverTimezone=UTC
            db.user=root
            db.password=[your_password]

    Option 2: Hosting
        Set:
            DB_URL=jdbc:mysql://localhost:3306/pokemon_collection?
            DB_USER=root
            DB_PASSWORD=[your_password]


6. Building the Project

    From the project root, run the command in the terminal (powershell):
        mvn clean compile


7. Running the Backend

    Run:
        mvn exec:java -Dexec.mainClass=com.example.pokemon.service.rest.PokemonServiceApp
    
    The server is run at: http://localhost:4567

8. Running the Frontend

    Open: http://localhost:4567/index.html