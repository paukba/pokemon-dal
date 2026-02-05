-- Step 2: Insert test data
USE pokemon_collection;

-- Owners
INSERT INTO owners (name, email) VALUES
('Ash Ketchum', 'ash@example.com'),
('Misty Waterflower', 'misty@example.com'),
('Brock Harrison', 'brock@example.com'),
('Gary Oak', 'gary@example.com'),
('Serena Vale', 'serena@example.com');

-- Cards (EXACTLY 50 rows)
INSERT INTO cards (name, card_set, rarity, edition, market_value_usd, `condition`) VALUES
('Pikachu', 'Base Set', 'Common', '1st', 12.50, 'Near Mint'),
('Charizard', 'Base Set', 'Rare Holo', '1st', 250.00, 'Lightly Played'),
('Blastoise', 'Base Set', 'Rare Holo', '1st', 180.00, 'Near Mint'),
('Venusaur', 'Base Set', 'Rare Holo', '1st', 120.00, 'Near Mint'),
('Mewtwo', 'Fossil', 'Rare Holo', 'Unlimited', 95.00, 'Near Mint'),
('Gyarados', 'Base Set', 'Rare', 'Unlimited', 30.00, 'Near Mint'),
('Alakazam', 'Base Set', 'Rare', 'Unlimited', 40.00, 'Moderately Played'),
('Machamp', 'Base Set', 'Rare', 'Unlimited', 35.00, 'Near Mint'),
('Zapdos', 'Base Set', 'Rare Holo', '1st', 110.00, 'Near Mint'),
('Articuno', 'Base Set', 'Rare Holo', '1st', 105.00, 'Near Mint'),
('Ninetales', 'Base Set', 'Rare', 'Unlimited', 28.00, 'Near Mint'),
('Snorlax', 'Jungle', 'Rare', '1st', 45.00, 'Near Mint'),
('Scyther', 'Jungle', 'Uncommon', 'Unlimited', 6.00, 'Near Mint'),
('Gengar', 'Fossil', 'Rare Holo', 'Unlimited', 88.00, 'Near Mint'),
('Dragonite', 'Fossil', 'Rare Holo', '1st', 150.00, 'Lightly Played'),
('Eevee', 'Jungle', 'Common', 'Unlimited', 4.00, 'Near Mint'),
('Vaporeon', 'Jungle', 'Rare', '1st', 65.00, 'Near Mint'),
('Jolteon', 'Jungle', 'Rare', '1st', 60.00, 'Near Mint'),
('Flareon', 'Jungle', 'Rare', '1st', 58.00, 'Near Mint'),
('Pidgeot', 'Base Set', 'Rare', 'Unlimited', 15.00, 'Near Mint'),
('Rhydon', 'Base Set', 'Rare', 'Unlimited', 20.00, 'Near Mint'),
('Nidoking', 'Jungle', 'Rare', 'Unlimited', 18.00, 'Near Mint'),
('Nidoqueen', 'Jungle', 'Rare', 'Unlimited', 17.00, 'Near Mint'),
('Clefairy', 'Base Set', 'Rare', 'Unlimited', 22.00, 'Near Mint'),
('Charmeleon', 'Base Set', 'Uncommon', 'Unlimited', 8.00, 'Near Mint'),
('Squirtle', 'Base Set', 'Common', 'Unlimited', 3.50, 'Near Mint'),
('Bulbasaur', 'Base Set', 'Common', 'Unlimited', 3.00, 'Near Mint'),
('Psyduck', 'Base Set', 'Common', 'Unlimited', 2.50, 'Near Mint'),
('Growlithe', 'Jungle', 'Common', 'Unlimited', 2.75, 'Near Mint'),
('Arcanine', 'Jungle', 'Rare', 'Unlimited', 40.00, 'Near Mint'),
('Onix', 'Fossil', 'Uncommon', 'Unlimited', 5.00, 'Near Mint'),
('Clefable', 'Base Set', 'Rare', 'Unlimited', 25.00, 'Near Mint'),
('Abra', 'Base Set', 'Common', 'Unlimited', 1.75, 'Near Mint'),
('Porygon', 'Base Set', 'Uncommon', 'Unlimited', 7.00, 'Near Mint'),
('Lapras', 'Base Set', 'Rare Holo', '1st', 95.00, 'Near Mint'),
('Kabutops', 'Fossil', 'Rare', 'Unlimited', 12.00, 'Near Mint'),
('Omastar', 'Fossil', 'Rare', 'Unlimited', 11.00, 'Near Mint'),
('Hitmonchan', 'Base Set', 'Rare', 'Unlimited', 14.00, 'Near Mint'),
('Hitmonlee', 'Base Set', 'Rare', 'Unlimited', 13.00, 'Near Mint'),
('Mr. Mime', 'Base Set', 'Rare', 'Unlimited', 9.00, 'Near Mint'),
('Scyther (Holo)', 'Jungle', 'Rare Holo', '1st', 55.00, 'Near Mint'),
('Aerodactyl', 'Fossil', 'Rare Holo', '1st', 70.00, 'Near Mint'),
('Dratini', 'Base Set', 'Common', 'Unlimited', 2.25, 'Near Mint'),
('Dragonair', 'Base Set', 'Uncommon', 'Unlimited', 9.50, 'Near Mint'),
('Mew', 'Base Set', 'Rare Holo', '1st', 200.00, 'Lightly Played'),
('Dark Charizard', 'Team Rocket', 'Rare Holo', '1st', 220.00, 'Lightly Played'),
('Dark Blastoise', 'Team Rocket', 'Rare Holo', '1st', 190.00, 'Near Mint'),
('Umbreon', 'Neo Discovery', 'Rare Holo', '1st', 85.00, 'Near Mint');

-- Collections (sample data)
INSERT INTO collections (owner_id, card_id, quantity, acquired_date) VALUES
(1, 2, 1, '2026-02-01'),
(2, 1, 2, '2026-02-02'),
(3, 5, 1, '2026-02-03');

-- Trades (sample data)
INSERT INTO trades (from_owner, to_owner, card_id, quantity, trade_date, notes) VALUES
(1, 2, 2, 1, '2026-02-04', 'Charizard trade'),
(3, 4, 5, 1, '2026-02-04', 'Mewtwo trade');
