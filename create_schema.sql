-- Step 1: Create database schema
-- Pokémon Card Collection Manager
-- MySQL

CREATE DATABASE IF NOT EXISTS pokemon_collection;
USE pokemon_collection;

-- Owners table
CREATE TABLE owners (
    owner_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE
);

-- Cards table
CREATE TABLE cards (
    card_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    card_set VARCHAR(100),
    rarity VARCHAR(50),
    edition VARCHAR(50),
    market_value_usd DECIMAL(10,2),
    `condition` VARCHAR(50)
);

-- Collections table (many-to-many between owners and cards)
CREATE TABLE collections (
    collection_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    card_id INT NOT NULL,
    quantity INT DEFAULT 1,
    acquired_date DATE,
    FOREIGN KEY (owner_id) REFERENCES owners(owner_id),
    FOREIGN KEY (card_id) REFERENCES cards(card_id)
);

-- Trades table
CREATE TABLE trades (
    trade_id INT AUTO_INCREMENT PRIMARY KEY,
    from_owner INT NOT NULL,
    to_owner INT NOT NULL,
    card_id INT NOT NULL,
    quantity INT NOT NULL,
    trade_date DATE,
    notes VARCHAR(255),
    FOREIGN KEY (from_owner) REFERENCES owners(owner_id),
    FOREIGN KEY (to_owner) REFERENCES owners(owner_id),
    FOREIGN KEY (card_id) REFERENCES cards(card_id)
);
