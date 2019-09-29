package org.xstudio.plugin.idea.util;


import org.xstudio.plugin.idea.model.DbType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtils {

    public static String testConnection(String driverClass, String url, String username, String password, boolean isMySql8) throws ClassNotFoundException, SQLException {
        String databaseType = "mysql";
        Connection conn = null;
        if (driverClass.contains("oracle")) {
            databaseType = "oracle";
            Class.forName(DbType.Oracle.getDriverClass());
        } else if (driverClass.contains("mysql")) {
            if (!isMySql8) {
                databaseType = "mysql";
                Class.forName(DbType.MySQL.getDriverClass());
            } else {
                databaseType = "mysql8";
                Class.forName(DbType.MySQL_8.getDriverClass());
                url += "?serverTimezone=UTC";
            }
        } else if (driverClass.contains("postgresql")) {
            databaseType = "postgresql";
            Class.forName(DbType.PostgreSQL.getDriverClass());
        } else if (driverClass.contains("sqlserver")) {
            databaseType = "sqlserver";
            Class.forName(DbType.SqlServer.getDriverClass());
        } else if (driverClass.contains("sqlite")) {
            databaseType = "sqlite";
            Class.forName(DbType.Sqlite.getDriverClass());
        } else if (driverClass.contains("mariadb")) {
            databaseType = "mariadb";
            Class.forName(DbType.MariaDB.getDriverClass());
        }

        try {
            conn = DriverManager.getConnection(url, username, password);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return databaseType;
    }

}
