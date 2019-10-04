package com.wasteinformationserver.db;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public abstract class
Database {

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

    public static void logToConsole(ResultSet res) {
        try {

            ResultSetMetaData rsmd = res.getMetaData();

            while (res.next()) {
                String row = "";

                for (int i = 1; i <= rsmd.getColumnCount(); ++i) {
                    if (row.length() > 0) {
                        row += ", ";
                    }

                    if (res.getObject(i) != null) {
                        row +=
                                rsmd.getColumnName(i) +
                                        ": " +
                                        res.getObject(i).toString();
                    }
                }

                System.out.println(row);
            }

        } catch(SQLException e) {
        }
    }

    public static DefaultTableModel logToTable(ResultSet res) {
        try {
            ResultSetMetaData rsmd = res.getMetaData();
            Vector<String> columnNames = new Vector<>();
            int columnCount = rsmd.getColumnCount();

            for (int i = 1; i <= columnCount; ++i) {
                columnNames.add(rsmd.getColumnName(i));
            }

            Vector<Vector<Object>> data = new Vector<>();

            while(res.next()) {
                Vector<Object> row = new Vector();

                for (int i = 1; i <= columnCount; ++i) {
                    row.add(res.getObject(i));
                }

                data.add(row);
            }

            return new DefaultTableModel(data, columnNames);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
