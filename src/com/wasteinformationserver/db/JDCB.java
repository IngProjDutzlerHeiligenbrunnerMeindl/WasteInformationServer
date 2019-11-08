package com.wasteinformationserver.db;

import com.wasteinformationserver.basicutils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDCB {
    Connection conn;

    public JDCB(String username, String password, String dbname) {
        Database db = new MySQLConnector(
                username,
                password,
                "192.168.65.15",
                3306,
                dbname);

        try {
            conn = db.getConnection();
        } catch (SQLException e) {
            Log.error("no connection to Database!");
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

    public int executeUpdate(String sql) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
