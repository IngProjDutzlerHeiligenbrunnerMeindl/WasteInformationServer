package com.wasteinformationserver.db;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.basicutils.Storage;

import java.io.IOException;
import java.sql.*;

/**
 * basic connection class to a Database
 *
 * @author Lukas Heiligenbrunner
 * @author Emil Meindl
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

    private JDBC(String username, String password, String dbname, String ip, int port) {
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
            logintodb(usernamec, passwordc, dbnamec, ipc, portc);
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
    private static boolean logintodb(String username, String password, String dbname, String ip, int port) {
        try {
            DriverManager.setLoginTimeout(1);
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + ip + ":" + port + "/" + dbname + "?useSSL=false&serverTimezone=CET",
                    username,
                    password);
            loggedin = true;
            Log.Log.message("Connected to database");
        } catch (SQLException e) {
            // reconnect every 10 sec
            Log.Log.warning("Database-Connection not possible");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    Log.Log.debug("Reading config");
                    Storage st = Storage.Companion.getInstance();
                    st.init();
                    usernamec = st.getDbName();
                    passwordc = st.getDbPassword();
                    dbnamec = st.getDbName();
                    ipc = st.getDbhost();
                    portc = st.getDbPort();
                    Log.Log.info("Retry connection");
                    loggedin = logintodb(usernamec, passwordc, dbnamec, ipc, portc);
                }
            }).start();
        }
        return loggedin;
    }

    public void disconnect(){
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
}
