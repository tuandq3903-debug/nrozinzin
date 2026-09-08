package jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.Logger;

public class DatabaseManager {
    private static final HikariDataSource ds;
    static {
        ds = Config.createDataSource("DatabaseManager", Config.DB_NAME);
    }

    
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void close() {
        if (ds != null) {
            ds.close();
        }
    }
    public static DatabaseResultSet executeQuery(final String query, final Object... objs) throws Exception {
        try ( Connection con = getConnection();  PreparedStatement ps = con.prepareStatement(query)) {

            for (int i = 0; i < objs.length; ++i) {
                ps.setObject(i + 1, objs[i]);
            }

            try ( var rs = ps.executeQuery()) {
                return new ResultSetImpl(rs);
            }
        } catch (SQLException ex) {
            Logger.log(Logger.RED, "Error executing query: " + query);
            throw ex;
        }
    }

    public static int executeUpdate(final String query, final Object... objs) throws Exception {
        String formattedQuery = query;
        if (query.startsWith("insert") && query.endsWith("()")) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (int i = 0; i < objs.length; ++i) {
                sb.append("?");
                if (i < objs.length - 1) {
                    sb.append(",");
                }
            }
            sb.append(")");
            formattedQuery = query.replace("()", sb.toString());
        }

        try ( Connection con = getConnection();  PreparedStatement ps = con.prepareStatement(formattedQuery)) {

            for (int i = 0; i < objs.length; ++i) {
                ps.setObject(i + 1, objs[i]);
            }

            return ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.log(Logger.RED, "Error executing update: " + query);
            throw ex;
        }
    }
}
