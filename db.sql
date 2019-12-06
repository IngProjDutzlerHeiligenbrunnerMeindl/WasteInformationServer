-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Erstellungszeit: 06. Dez 2019 um 16:11
-- Server-Version: 10.3.11-MariaDB
-- PHP-Version: 5.6.40

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `wasteinformation`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `cities`
--

CREATE TABLE `cities` (
  `id` int(11) NOT NULL,
  `userid` int(11) NOT NULL,
  `name` varchar(256) NOT NULL,
  `wastetype` varchar(64) NOT NULL,
  `zone` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `cities`
--

INSERT INTO `cities` (`id`, `userid`, `name`, `wastetype`, `zone`) VALUES
(2, 0, 'Steyr', 'Bio', 2),
(3, 0, 'Steyr', 'Bio', 3),
(4, 0, 'Steyr', 'Bio', 4),
(128, 0, 'Steyr', 'Biowaste', 1),
(130, 0, 'abc', 'Biowaste', 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `pickupdates`
--

CREATE TABLE `pickupdates` (
  `id` int(11) NOT NULL,
  `citywastezoneid` int(11) NOT NULL,
  `pickupdate` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `pickupdates`
--

INSERT INTO `pickupdates` (`id`, `citywastezoneid`, `pickupdate`) VALUES
(6, 4, '2019-12-06'),
(7, 3, '2019-12-06'),
(8, 2, '2019-12-06'),
(9, 130, '2019-12-26');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(150) NOT NULL,
  `firstName` varchar(32) NOT NULL,
  `secondName` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  `permission` int(11) NOT NULL DEFAULT 0,
  `email` varchar(64) NOT NULL,
  `logindate` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `user`
--

INSERT INTO `user` (`id`, `username`, `firstName`, `secondName`, `password`, `permission`, `email`, `logindate`) VALUES
(2, 'lheilige', 'lukas', 'heiligenbrunner', 'c6e2ddf577e0dfdc4366f788f1b27102', 0, 'lukas.heiligenbrunner@gmail.com', '2019-09-20 12:19:31'),
(11, 'meindl', 'emil', 'meindl', '81dc9bdb52d04dc20036dbd8313ed055', 0, 'mailmeindl', '2019-09-27 10:23:46'),
(15, 'horni', 'kk', 'kk', 'c4ca4238a0b923820dcc509a6f75849b', 1, 'kk', '2019-09-27 13:19:09');

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `cities`
--
ALTER TABLE `cities`
  ADD PRIMARY KEY (`id`);

--
-- Indizes für die Tabelle `pickupdates`
--
ALTER TABLE `pickupdates`
  ADD PRIMARY KEY (`id`),
  ADD KEY `citywastezoneid` (`citywastezoneid`);

--
-- Indizes für die Tabelle `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `cities`
--
ALTER TABLE `cities`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=131;

--
-- AUTO_INCREMENT für Tabelle `pickupdates`
--
ALTER TABLE `pickupdates`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT für Tabelle `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `pickupdates`
--
ALTER TABLE `pickupdates`
  ADD CONSTRAINT `pickupdates_ibfk_1` FOREIGN KEY (`citywastezoneid`) REFERENCES `cities` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
