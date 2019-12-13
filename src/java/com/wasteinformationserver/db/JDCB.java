package com.wasteinformationserver.db;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDCB {
    static Connection conn;

    static JDCB jdcb;
    static boolean loggedin = false;

    static String usernamec;
    static String passwordc;
    static String dbnamec;
    static String ipc;
    static int portc;

    public static void init(String username, String password, String dbname, String ip, int port) throws IOException {
        usernamec = username;
        passwordc = password;
        dbnamec = dbname;
        jdcb = new JDCB(username, password, dbname, ip, port);
    }

    private JDCB(String username, String password, String dbname, String ip, int port) throws IOException {
        logintodb(username, password, dbname, ip, port);
    }

    public static JDCB getInstance() throws IOException {
        if (loggedin) {
            return jdcb;
        } else {
            logintodb(usernamec, passwordc, dbnamec, ipc, portc);
            return jdcb;
        }

    }

    public static void logintodb(String username, String password, String dbname, String ip, int port) throws IOException {
        Database db = new MySQLConnector(
                username,
                password,
                ip,
                port,
                dbname);

        try {
            conn = db.getConnection();
            loggedin = true;
        } catch (SQLException e) {
            throw new IOException("No connection to database");
        }

    }

    public ResultSet executeQuery(String sql) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int executeUpdate(String sql) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);

        return stmt.executeUpdate();
    }
}
