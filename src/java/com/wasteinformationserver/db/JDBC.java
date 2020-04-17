package com.wasteinformationserver.db;

import com.wasteinformationserver.basicutils.Log;

import java.io.IOException;
import java.sql.*;

/**
 * basic connection class to a Database
 *
 * @author Lukas Heiligenbrunner
 */
public class JDBC {
    private static Connection conn;

    private static JDBC JDBC;
    private static boolean loggedin = false;

    private static String usernamec;
    private static String passwordc;
    private static String dbnamec;
    private static String ipc;
    private static int portc;

    private JDBC(String username, String password, String dbname, String ip, int port) throws IOException {
        logintodb(username, password, dbname, ip, port);
    }

    /**
     * instance of JDBC driver
     */
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * initialize database values
     * suggested on startup
     *
     * @param username db username
     * @param password db password
     * @param dbname   Database name
     * @param ip       Server ip or hostname
     * @param port     Server port
     * @throws IOException
     */
    public static void init(String username, String password, String dbname, String ip, int port) throws IOException {
        usernamec = username;
        passwordc = password;
        dbnamec = dbname;
        ipc = ip;
        portc = port;
        JDBC = new JDBC(username, password, dbname, ip, port);
    }

    /**
     * get instance of db object
     * logindata has to be set before!
     *
     * @return JDBC object of this
     * @throws IOException
     */
    public static JDBC getInstance() {
        if (!loggedin) {
            try {
                JDBC = new JDBC(usernamec, passwordc, dbnamec, ipc, portc);
            } catch (IOException e) {
                Log.Log.error("no connetion to db - retrying in 5min");
            }
        }
        return JDBC;
    }

    /**
     * initial login to db -- should be called only one time or for reconnect
     *
     * @param username username
     * @param password password
     * @param dbname   Database name
     * @param ip       Host or ip address
     * @param port     Server port
     * @throws IOException thrown if no connection to db is possible.
     */
    private void logintodb(String username, String password, String dbname, String ip, int port) throws IOException {
        try {
            DriverManager.setLoginTimeout(1);
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + ip + ":" + port + "/" + dbname + "?useSSL=false&serverTimezone=CET",
                    username,
                    password);
            checkDBStructure();
            loggedin = true;
        } catch (SQLException e) {
            throw new IOException("No connection to database");
            // todo reconnect every 5mins or something
        }
    }

    public void disconnect() {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * execute basic query --> requests only
     *
     * @param sql query sql statement
     * @return ResultSet representating the table
     */
    public ResultSet executeQuery(String sql) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * update db in some way
     *
     * @param sql sql insert/update/delete statement
     * @return status
     * @throws SQLException
     */
    public int executeUpdate(String sql) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);

        return stmt.executeUpdate();
    }

    /**
     * check if connection is still established
     *
     * @return connection state
     */
    public static boolean isConnected() {
        return loggedin;
    }

    /**
     * validate the correctness of the current sql db structure
     */
    public void checkDBStructure() {
        try {
            ResultSet set = conn.getMetaData().getTables(null, null, null, new String[]{"TABLE"});
            set.last();
            if (set.getRow() != 5) {
                // structure not valid
                executeUpdate("-- phpMyAdmin SQL Dump\n" +
                        "-- version 4.6.6deb4\n" +
                        "-- https://www.phpmyadmin.net/\n" +
                        "--\n" +
                        "-- Host: localhost:3306\n" +
                        "-- Erstellungszeit: 17. Apr 2020 um 09:07\n" +
                        "-- Server-Version: 10.1.44-MariaDB-0+deb9u1\n" +
                        "-- PHP-Version: 7.3.13-1+0~20191218.50+debian9~1.gbp23c2da\n" +
                        "\n" +
                        "SET SQL_MODE = \"NO_AUTO_VALUE_ON_ZERO\";\n" +
                        "SET time_zone = \"+00:00\";\n" +
                        "\n" +
                        "\n" +
                        "/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;\n" +
                        "/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;\n" +
                        "/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;\n" +
                        "/*!40101 SET NAMES utf8mb4 */;\n" +
                        "\n" +
                        "--\n" +
                        "-- Datenbank: `ingproject`\n" +
                        "--\n" +
                        "\n" +
                        "-- --------------------------------------------------------\n" +
                        "\n" +
                        "--\n" +
                        "-- Tabellenstruktur für Tabelle `cities`\n" +
                        "--\n" +
                        "\n" +
                        "CREATE TABLE `cities` (\n" +
                        "  `id` int(11) NOT NULL,\n" +
                        "  `userid` int(11) NOT NULL,\n" +
                        "  `name` varchar(256) NOT NULL,\n" +
                        "  `wastetype` varchar(64) NOT NULL,\n" +
                        "  `zone` int(11) NOT NULL\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8;\n" +
                        "\n" +
                        "-- --------------------------------------------------------\n" +
                        "\n" +
                        "--\n" +
                        "-- Tabellenstruktur für Tabelle `devices`\n" +
                        "--\n" +
                        "\n" +
                        "CREATE TABLE `devices` (\n" +
                        "  `DeviceID` int(11) NOT NULL,\n" +
                        "  `CityID` int(11) NOT NULL DEFAULT '-1',\n" +
                        "  `DeviceName` varchar(15) DEFAULT NULL,\n" +
                        "  `DeviceLocation` varchar(15) DEFAULT NULL\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;\n" +
                        "\n" +
                        "-- --------------------------------------------------------\n" +
                        "\n" +
                        "--\n" +
                        "-- Tabellenstruktur für Tabelle `device_city`\n" +
                        "--\n" +
                        "\n" +
                        "CREATE TABLE `device_city` (\n" +
                        "  `DeviceID` int(11) NOT NULL,\n" +
                        "  `CityID` int(11) NOT NULL\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;\n" +
                        "\n" +
                        "-- --------------------------------------------------------\n" +
                        "\n" +
                        "--\n" +
                        "-- Tabellenstruktur für Tabelle `pickupdates`\n" +
                        "--\n" +
                        "\n" +
                        "CREATE TABLE `pickupdates` (\n" +
                        "  `id` int(11) NOT NULL,\n" +
                        "  `citywastezoneid` int(11) NOT NULL,\n" +
                        "  `pickupdate` date NOT NULL\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8;\n" +
                        "\n" +
                        "-- --------------------------------------------------------\n" +
                        "\n" +
                        "--\n" +
                        "-- Tabellenstruktur für Tabelle `user`\n" +
                        "--\n" +
                        "\n" +
                        "CREATE TABLE `user` (\n" +
                        "  `id` int(11) NOT NULL,\n" +
                        "  `username` varchar(150) NOT NULL,\n" +
                        "  `firstName` varchar(32) NOT NULL,\n" +
                        "  `secondName` varchar(32) NOT NULL,\n" +
                        "  `password` varchar(32) NOT NULL,\n" +
                        "  `permission` int(11) NOT NULL DEFAULT '0',\n" +
                        "  `email` varchar(64) NOT NULL,\n" +
                        "  `logindate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8;\n" +
                        "\n" +
                        "--\n" +
                        "-- Indizes der exportierten Tabellen\n" +
                        "--\n" +
                        "\n" +
                        "--\n" +
                        "-- Indizes für die Tabelle `cities`\n" +
                        "--\n" +
                        "ALTER TABLE `cities`\n" +
                        "  ADD PRIMARY KEY (`id`);\n" +
                        "\n" +
                        "--\n" +
                        "-- Indizes für die Tabelle `devices`\n" +
                        "--\n" +
                        "ALTER TABLE `devices`\n" +
                        "  ADD PRIMARY KEY (`DeviceID`);\n" +
                        "\n" +
                        "--\n" +
                        "-- Indizes für die Tabelle `pickupdates`\n" +
                        "--\n" +
                        "ALTER TABLE `pickupdates`\n" +
                        "  ADD PRIMARY KEY (`id`),\n" +
                        "  ADD KEY `citywastezoneid` (`citywastezoneid`);\n" +
                        "\n" +
                        "--\n" +
                        "-- Indizes für die Tabelle `user`\n" +
                        "--\n" +
                        "ALTER TABLE `user`\n" +
                        "  ADD PRIMARY KEY (`id`);\n" +
                        "\n" +
                        "--\n" +
                        "-- AUTO_INCREMENT für exportierte Tabellen\n" +
                        "--\n" +
                        "\n" +
                        "--\n" +
                        "-- AUTO_INCREMENT für Tabelle `cities`\n" +
                        "--\n" +
                        "ALTER TABLE `cities`\n" +
                        "  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=143;\n" +
                        "--\n" +
                        "-- AUTO_INCREMENT für Tabelle `pickupdates`\n" +
                        "--\n" +
                        "ALTER TABLE `pickupdates`\n" +
                        "  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=67;\n" +
                        "--\n" +
                        "-- AUTO_INCREMENT für Tabelle `user`\n" +
                        "--\n" +
                        "ALTER TABLE `user`\n" +
                        "  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;\n" +
                        "--\n" +
                        "-- Constraints der exportierten Tabellen\n" +
                        "--\n" +
                        "\n" +
                        "--\n" +
                        "-- Constraints der Tabelle `pickupdates`\n" +
                        "--\n" +
                        "ALTER TABLE `pickupdates`\n" +
                        "  ADD CONSTRAINT `pickupdates_ibfk_1` FOREIGN KEY (`citywastezoneid`) REFERENCES `cities` (`id`);\n" +
                        "\n" +
                        "/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;\n" +
                        "/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;\n" +
                        "/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;\n");
            } else {
                Log.Log.message("found valid database structure!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
