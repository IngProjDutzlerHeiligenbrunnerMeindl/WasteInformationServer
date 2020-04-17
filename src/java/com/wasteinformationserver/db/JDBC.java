package com.wasteinformationserver.db;

import com.wasteinformationserver.basicutils.Log;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

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
            ResultSet seti = executeQuery("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '" + dbnamec + "'");
            seti.last();
            Log.Log.debug("found " + seti.getInt(1) + " tables in db");
            if (seti.getInt(1) != 5) {
                // structure not valid
                Log.Log.info("recreating Database structure!");
                Scanner s = new Scanner(getClass().getResourceAsStream("/db.sql"));
                s.useDelimiter("(;(\r)?\n)|(--\n)");
                Statement st = null;
                try {
                    st = conn.createStatement();
                    while (s.hasNext()) {
                        String line = s.next();
                        if (line.startsWith("/*!") && line.endsWith("*/")) {
                            int i = line.indexOf(' ');
                            line = line.substring(i + 1, line.length() - " */".length());
                        }

                        if (line.trim().length() > 0) {
                            executeUpdate(line);
                        }
                    }
                } finally {
                    if (st != null) st.close();
                }
            } else {
                Log.Log.message("found valid database structure!");
            }
        } catch (SQLException e) {
            Log.Log.error("a unhandled SQLexception occured at db structure creation.");
        }
    }
}
