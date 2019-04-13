package org.xstudio.plugins.idea.database;

import com.intellij.openapi.ui.Messages;
import org.mybatis.generator.api.IntrospectedColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaobiao
 * @version 2019/3/20
 */
public class DatabaseUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUtil.class);
    /**
     * 数据库操作
     */
    private static final String SQL = "SELECT * FROM ";
    private String DRIVER = "";
    private String URL = "";
    private String USERNAME = "";
    private String PASSWORD = "";


    public DatabaseUtil(String url, String username, String password, String driverClass) {
        this.USERNAME = username;
        this.URL = url;
        this.PASSWORD = password;
        this.DRIVER = driverClass;
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            Messages.showMessageDialog("数据库驱动加载异常 " + e.getMessage(), "错误", Messages.getErrorIcon());
        }
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            Messages.showMessageDialog("数据库连接失败 " + e.getMessage(), "错误", Messages.getErrorIcon());
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     *
     * @param conn
     */
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
            }
        }
    }

    /**
     * 获取数据库下的所有表名
     */
    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        Connection conn = getConnection();
        ResultSet rs = null;
        try {
            //获取数据库的元数据
            DatabaseMetaData db = conn.getMetaData();
            //从元数据中获取到所有的表名
            rs = db.getTables(null, null, null, new String[]{"TABLE"});
            while (rs.next()) {
                tableNames.add(rs.getString(3));
            }
        } catch (SQLException e) {
            Messages.showMessageDialog("数据库表获取异常 " + e.getMessage(), "错误", Messages.getErrorIcon());
        } finally {
            try {
                rs.close();
                closeConnection(conn);
            } catch (SQLException e) {
                LOGGER.error("close ResultSet failure", e);
            }
        }
        return tableNames;
    }

    public List<IntrospectedColumn> getColumns(String database, String tableName) {
        List<IntrospectedColumn> columns = new ArrayList<>();
        //与数据库的连接
        Connection conn = getConnection();
        ResultSet rs;
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getColumns(null, database, tableName, "%");
            while (rs.next()) {
                IntrospectedColumn column = new IntrospectedColumn();
                column.setJdbcType(rs.getInt("DATA_TYPE")); //$NON-NLS-1$
                column.setLength(rs.getInt("COLUMN_SIZE")); //$NON-NLS-1$
                column.setActualColumnName(rs.getString("COLUMN_NAME")); //$NON-NLS-1$
                column.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable); //$NON-NLS-1$
                column.setScale(rs.getInt("DECIMAL_DIGITS")); //$NON-NLS-1$
                column.setRemarks(rs.getString("REMARKS")); //$NON-NLS-1$
                column.setDefaultValue(rs.getString("COLUMN_DEF")); //$NON-NLS-1$
                columns.add(column);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columns;
    }

    /**
     * 获取表中所有字段名称
     *
     * @param tableName 表名
     * @return
     */
    public List<String> getColumnNames(String tableName) {
        List<String> columnNames = new ArrayList<>();
        //与数据库的连接
        Connection conn = getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnNames.add(rsmd.getColumnName(i + 1));
            }
        } catch (SQLException e) {
            LOGGER.error("getColumnNames failure", e);
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnNames close pstem and connection failure", e);
                }
            }
        }
        return columnNames;
    }

    /**
     * 获取表中所有字段类型
     *
     * @param tableName
     * @return
     */
    public List<String> getColumnTypes(String tableName) {
        List<String> columnTypes = new ArrayList<>();
        //与数据库的连接
        Connection conn = getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnTypes.add(rsmd.getColumnTypeName(i + 1));
            }
        } catch (SQLException e) {
            LOGGER.error("getColumnTypes failure", e);
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnTypes close pstem and connection failure", e);
                }
            }
        }
        return columnTypes;
    }

    /**
     * 获取表中字段的所有注释
     *
     * @param tableName
     * @return
     */
    public List<String> getColumnComments(String tableName) {
        List<String> columnTypes = new ArrayList<>();
        //与数据库的连接
        Connection conn = getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        //列名注释集合
        List<String> columnComments = new ArrayList<>();
        ResultSet rs = null;
        try {
            pStemt = conn.prepareStatement(tableSql);
            rs = pStemt.executeQuery("show full columns from " + tableName);
            while (rs.next()) {
                columnComments.add(rs.getString("Comment"));
            }
        } catch (SQLException e) {
            LOGGER.error("getColumnComments close ResultSet and connection failure", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnComments close ResultSet and connection failure", e);
                }
            }
        }
        return columnComments;
    }
}
