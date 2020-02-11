package com.wasteinformationserver.db;

import java.sql.Connection;
import java.sql.SQLException;

abstract class Database {

    protected String user;
    protected String password;

    protected String host;
    protected int port;

    protected String dbName;

    public Database(String user, String password, String host, int port, String dbName) {
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
        this.dbName = dbName;
    }

    public abstract Connection getConnection() throws SQLException;
}
