package com.wasteinformationserver.db;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        JDBC = new JDBC(username, password, dbname, ip, port);
    }

    private JDBC(String username, String password, String dbname, String ip, int port) throws IOException {
        logintodb(username, password, dbname, ip, port);
    }

    /**
     * get instance of db object
     * logindata has to be set before!
     *
     * @return JDBC object of this
     * @throws IOException
     */
    public static JDBC getInstance() throws IOException {
        if (loggedin) {
            return JDBC;
        } else {
            logintodb(usernamec, passwordc, dbnamec, ipc, portc);
            return JDBC;
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
}
