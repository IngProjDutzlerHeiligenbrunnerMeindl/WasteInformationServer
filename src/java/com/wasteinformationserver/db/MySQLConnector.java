package com.wasteinformationserver.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnector extends Database {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MySQLConnector(String user, String password, String host, int port, String dbName) {
        super(user, password, host, port, dbName);
    }

    public Connection getConnection() throws SQLException {
        DriverManager.setLoginTimeout(1); // TODO: 30.11.19 set higher maybe
        return DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false",
                user,
                password);
    }

}
