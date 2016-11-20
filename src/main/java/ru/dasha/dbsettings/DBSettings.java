package ru.dasha.dbsettings;


import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBSettings {
    @Resource(name = "jdbc/dblibrary")
    private static DataSource ds;

    public static void dbInit() {
        try {
            Context ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/dblibrary");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }

        Connection con = ds.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        return con;
    }

    public static void closeAll(Connection con, PreparedStatement ps, ResultSet res) {
        try {
            if (con != null) {
                con.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (res != null) {
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
