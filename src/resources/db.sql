SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;


CREATE TABLE cities
(
    id        int(11)      NOT NULL,
    userid    int(11)      NOT NULL,
    name      varchar(256) NOT NULL,
    wastetype varchar(64)  NOT NULL,
    zone      int(11)      NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE devices
(
    DeviceID       int(11) NOT NULL,
    CityID         int(11) NOT NULL DEFAULT '-1',
    DeviceName     varchar(15)      DEFAULT NULL,
    DeviceLocation varchar(15)      DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE device_city
(
    DeviceID int(11) NOT NULL,
    CityID   int(11) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE pickupdates
(
    id              int(11) NOT NULL,
    citywastezoneid int(11) NOT NULL,
    pickupdate      date    NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `user`
(
    id         int(11)      NOT NULL,
    username   varchar(150) NOT NULL,
    firstName  varchar(32)  NOT NULL,
    secondName varchar(32)  NOT NULL,
    password   varchar(32)  NOT NULL,
    permission int(11)      NOT NULL DEFAULT '0',
    email      varchar(64)  NOT NULL,
    logindate  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


ALTER TABLE cities
    ADD PRIMARY KEY (id);

ALTER TABLE devices
    ADD PRIMARY KEY (DeviceID);

ALTER TABLE pickupdates
    ADD PRIMARY KEY (id),
    ADD KEY citywastezoneid (citywastezoneid);

ALTER TABLE `user`
    ADD PRIMARY KEY (id);


ALTER TABLE cities
    MODIFY id int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 143;
ALTER TABLE pickupdates
    MODIFY id int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 67;
ALTER TABLE `user`
    MODIFY id int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 17;

ALTER TABLE pickupdates
    ADD CONSTRAINT pickupdates_ibfk_1 FOREIGN KEY (citywastezoneid) REFERENCES cities (id);

/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
