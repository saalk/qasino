--
-- Table structure for table playingCards
--
DROP TABLE IF EXISTS playingCards;

CREATE TABLE playingCards (
  cardId varchar(3) NOT NULL,
  rank varchar(255) DEFAULT NULL,
  suit varchar(255) DEFAULT NULL,
  value int(11) DEFAULT NULL
);

--
-- Dumping data for table CARD
--

INSERT INTO cads (cardId, rand, suit, value) VALUES
('10C', 'TEN', 'CLUBS', 10),
('10D', 'TEN', 'DIAMONDS', 10),
('10H', 'TEN', 'HEARTS', 10),
('10S', 'TEN', 'SPADES', 10),
('2C', 'TWO', 'CLUBS', 2),
('2D', 'TWO', 'DIAMONDS', 2),
('2H', 'TWO', 'HEARTS', 2),
('2S', 'TWO', 'SPADES', 2),
('3C', 'THREE', 'CLUBS', 3),
('3D', 'THREE', 'DIAMONDS', 3),
('3H', 'THREE', 'HEARTS', 3),
('3S', 'THREE', 'SPADES', 3),
('4C', 'FOUR', 'CLUBS', 4),
('4D', 'FOUR', 'DIAMONDS', 4),
('4H', 'FOUR', 'HEARTS', 4),
('4S', 'FOUR', 'SPADES', 4),
('5C', 'FIVE', 'CLUBS', 5),
('5D', 'FIVE', 'DIAMONDS', 5),
('5H', 'FIVE', 'HEARTS', 5),
('5S', 'FIVE', 'SPADES', 5),
('6C', 'SIX', 'CLUBS', 6),
('6D', 'SIX', 'DIAMONDS', 6),
('6H', 'SIX', 'HEARTS', 6),
('6S', 'SIX', 'SPADES', 6),
('7C', 'SEVEN', 'CLUBS', 7),
('7D', 'SEVEN', 'DIAMONDS', 7),
('7H', 'SEVEN', 'HEARTS', 7),
('7S', 'SEVEN', 'SPADES', 7),
('8C', 'EIGHT', 'CLUBS', 8),
('8D', 'EIGHT', 'DIAMONDS', 8),
('8H', 'EIGHT', 'HEARTS', 8),
('8S', 'EIGHT', 'SPADES', 8),
('9C', 'NINE', 'CLUBS', 9),
('9D', 'NINE', 'DIAMONDS', 9),
('9H', 'NINE', 'HEARTS', 9),
('9S', 'NINE', 'SPADES', 9),
('AC', 'ACE', 'CLUBS', 1),
('AD', 'ACE', 'DIAMONDS', 1),
('AH', 'ACE', 'HEARTS', 1),
('AS', 'ACE', 'SPADES', 1),
('JC', 'JACK', 'CLUBS', 11),
('JD', 'JACK', 'DIAMONDS', 11),
('JH', 'JACK', 'HEARTS', 11),
('JS', 'JACK', 'SPADES', 11),
('KC', 'KING', 'CLUBS', 13),
('KD', 'KING', 'DIAMONDS', 13),
('KH', 'KING', 'HEARTS', 13),
('KS', 'KING', 'SPADES', 13),
('QC', 'QUEEN', 'CLUBS', 12),
('QD', 'QUEEN', 'DIAMONDS', 12),
('QH', 'QUEEN', 'HEARTS', 12),
('QS', 'QUEEN', 'SPADES', 12),
('RJ', 'JOKER', 'JOKERS', 0);
