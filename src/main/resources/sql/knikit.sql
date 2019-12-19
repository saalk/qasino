-- phpMyAdmin SQL Dump
-- version 4.4.7
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 10, 2016 at 09:49 PM
-- Server version: 5.5.47-MariaDB
-- PHP Version: 5.5.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `knikit`
--
DROP DATABASE knikit;
CREATE DATABASE knikit DEFAULT CHARSET=utf8;
-- --------------------------------------------------------

--
-- Table structure for table `CARD`
--

CREATE TABLE IF NOT EXISTS `CARD` (
  `CARD_ID` varchar(3) NOT NULL,
  `RANK` varchar(255) DEFAULT NULL,
  `SUIT` varchar(255) DEFAULT NULL,
  `VALUE` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `CARD`
--

INSERT INTO `CARD` (`CARD_ID`, `RANK`, `SUIT`, `VALUE`) VALUES
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

-- --------------------------------------------------------

--
-- Table structure for table `CASINO`
--

CREATE TABLE IF NOT EXISTS `CASINO` (
  `CASINO_ID` int(11) NOT NULL,
  `CREATED` varchar(25) DEFAULT NULL,
  `PLAYING_ORDER` int(11) DEFAULT NULL,
  `GAME_ID` int(11) DEFAULT NULL,
  `HAND_ID` int(11) DEFAULT NULL,
  `PLAYER_ID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `CASINO`
--

INSERT INTO `CASINO` (`CASINO_ID`, `CREATED`, `PLAYING_ORDER`, `GAME_ID`, `HAND_ID`, `PLAYER_ID`) VALUES
(66, '123', 1, 6, 77, 8);

-- --------------------------------------------------------

--
-- Table structure for table `DECK`
--

CREATE TABLE IF NOT EXISTS `DECK` (
  `DECK_ID` int(11) NOT NULL,
  `CARD_ORDER` int(11) DEFAULT NULL,
  `CARD_ID` varchar(3) DEFAULT NULL,
  `PLAYER_ID` int(11) DEFAULT NULL,
  `GAME_ID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `DECK`
--

INSERT INTO `DECK` (`DECK_ID`, `CARD_ORDER`, `CARD_ID`, `PLAYER_ID`, `GAME_ID`) VALUES
(11, 53, 'RJ', NULL, 6);

-- --------------------------------------------------------

--
-- Table structure for table `GAME`
--

CREATE TABLE IF NOT EXISTS `GAME` (
  `GAME_ID` int(11) NOT NULL,
  `ANTE` int(11) DEFAULT NULL,
  `CARD_GAME_TYPE` varchar(255) NOT NULL,
  `CREATED` varchar(25) DEFAULT NULL,
  `CURRENT_ROUND` int(11) DEFAULT NULL,
  `CURRENT_TURN` int(11) DEFAULT NULL,
  `MAX_ROUNDS` int(11) DEFAULT NULL,
  `MAX_TURNS` int(11) DEFAULT NULL,
  `MIN_ROUNDS` int(11) DEFAULT NULL,
  `MIN_TURNS` int(11) DEFAULT NULL,
  `STATE` varchar(25) NOT NULL,
  `TURNS_TO_WIN` int(11) DEFAULT NULL,
  `PLAYER_ID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `GAME`
--

INSERT INTO `GAME` (`GAME_ID`, `ANTE`, `CARD_GAME_TYPE`, `CREATED`, `CURRENT_ROUND`, `CURRENT_TURN`, `MAX_ROUNDS`, `MAX_TURNS`, `MIN_ROUNDS`, `MIN_TURNS`, `STATE`, `TURNS_TO_WIN`, `PLAYER_ID`) VALUES
(6, 0, 'HIGHLOW', '161110-21:48-20213', 0, 0, 0, 0, 0, 0, 'IS_SETUP', 0, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `HAND`
--

CREATE TABLE IF NOT EXISTS `HAND` (
  `HAND_ID` int(11) NOT NULL,
  `CARD_ORDER` int(11) DEFAULT NULL,
  `CARD_ID` varchar(3) DEFAULT NULL,
  `CASINO_ID` int(11) DEFAULT NULL,
  `PLAYER_ID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `HAND`
--

INSERT INTO `HAND` (`HAND_ID`, `CARD_ORDER`, `CARD_ID`, `CASINO_ID`, `PLAYER_ID`) VALUES
(77, 1, 'AS', 66, 8);

-- --------------------------------------------------------

--
-- Table structure for table `hibernate_sequences`
--

CREATE TABLE IF NOT EXISTS `hibernate_sequences` (
  `sequence_name` varchar(255) NOT NULL,
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `hibernate_sequences`
--

INSERT INTO `hibernate_sequences` (`sequence_name`, `next_val`) VALUES
('default', 12);

-- --------------------------------------------------------

--
-- Table structure for table `PLAYER`
--

CREATE TABLE IF NOT EXISTS `PLAYER` (
  `PLAYER_ID` int(11) NOT NULL,
  `AI_LEVEL` varchar(255) NOT NULL,
  `ALIAS` varchar(255) DEFAULT NULL,
  `AVATAR` varchar(255) NOT NULL,
  `CREATED` varchar(25) DEFAULT NULL,
  `CUBITS` int(11) DEFAULT NULL,
  `HUMAN` bit(1) DEFAULT NULL,
  `SECURED_LOAN` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `PLAYER`
--

INSERT INTO `PLAYER` (`PLAYER_ID`, `AI_LEVEL`, `ALIAS`, `AVATAR`, `CREATED`, `CUBITS`, `HUMAN`, `SECURED_LOAN`) VALUES
(8, 'HUMAN', 'Script Doe', 'ELF', '161110-21:45-37689', 566, b'1', 466),
(9, 'MEDIUM', 'alien', 'ELF', '161110-21:45-55442', 916, b'0', 816),
(10, 'LOW', 'alien', 'ELF', '161110-21:45-55441', 906, b'0', 956);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `CARD`
--
ALTER TABLE `CARD`
  ADD PRIMARY KEY (`CARD_ID`);

--
-- Indexes for table `CASINO`
--
ALTER TABLE `CASINO`
  ADD PRIMARY KEY (`CASINO_ID`),
  ADD UNIQUE KEY `UC_GAME_PLAYER_HAND` (`GAME_ID`,`PLAYER_ID`,`HAND_ID`),
  ADD KEY `GAME_ID_INDEX` (`GAME_ID`),
  ADD KEY `PLAYER_ID_INDEX` (`PLAYER_ID`),
  ADD KEY `HAND_ID_INDEX` (`HAND_ID`);

--
-- Indexes for table `DECK`
--
ALTER TABLE `DECK`
  ADD PRIMARY KEY (`DECK_ID`),
  ADD KEY `GAME_ID_INDEX` (`GAME_ID`),
  ADD KEY `CARD_ID` (`CARD_ID`);

--
-- Indexes for table `GAME`
--
ALTER TABLE `GAME`
  ADD PRIMARY KEY (`GAME_ID`);

--
-- Indexes for table `HAND`
--
ALTER TABLE `HAND`
  ADD PRIMARY KEY (`HAND_ID`),
  ADD UNIQUE KEY `UC_PLAYER_CASINO` (`PLAYER_ID`,`CASINO_ID`),
  ADD KEY `PLAYER_ID_INDEX` (`PLAYER_ID`),
  ADD KEY `CASINO_ID_INDEX` (`CASINO_ID`);

--
-- Indexes for table `hibernate_sequences`
--
ALTER TABLE `hibernate_sequences`
  ADD PRIMARY KEY (`sequence_name`);

--
-- Indexes for table `PLAYER`
--
ALTER TABLE `PLAYER`
  ADD PRIMARY KEY (`PLAYER_ID`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `CASINO`
--
ALTER TABLE `CASINO`
  ADD CONSTRAINT `PLAYER_ID` FOREIGN KEY (`PLAYER_ID`) REFERENCES `PLAYER` (`PLAYER_ID`),
  ADD CONSTRAINT `GAME_ID` FOREIGN KEY (`GAME_ID`) REFERENCES `GAME` (`GAME_ID`),
  ADD CONSTRAINT `HAND_ID` FOREIGN KEY (`HAND_ID`) REFERENCES `HAND` (`HAND_ID`);

--
-- Constraints for table `DECK`
--
ALTER TABLE `DECK`
  ADD CONSTRAINT `CARD_ID` FOREIGN KEY (`CARD_ID`) REFERENCES `CARD` (`CARD_ID`);

--
-- Constraints for table `HAND`
--
ALTER TABLE `HAND`
  ADD CONSTRAINT `CASINO_ID` FOREIGN KEY (`CASINO_ID`) REFERENCES `CASINO` (`CASINO_ID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
