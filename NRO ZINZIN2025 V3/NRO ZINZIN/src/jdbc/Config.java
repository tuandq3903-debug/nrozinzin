package jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Config {

    public static String DB_DRIVER = "com.mysql.jdbc.Driver";
    public static String DB_HOST = "localhost";
    public static String DB_PORT = "3306";
    public static String DB_NAME = "nro";
    public static String DB_USER = "root";
    public static String DB_PASSWORD = "";
    public static int DB_MIN_CONN = 5;
    public static int DB_MAX_CONN = 20;
    public static long DB_MAX_LIFE_TIME = 1800000;
    public static byte SERVER = 1;
    public static String NAME = "NRO ZINZIN";
    public static int PORT = 23501;
    public static String IP = "teamobiz.io.vn";
    public static String LINK_IP_PORT = "teamobiz.io.vn:23501";
    public static byte SECOND_WAIT_LOGIN = 5;
    public static int MAX_PER_IP = 100000;
    public static int MAX_PLAYER = 100000;
    public static double RATE_EXP_SERVER = 1.0;
    public static double RATE_DROP_PERCENT = 100.0;
    public static boolean LOCAL = false;
    public static boolean TEST = false;
    public static boolean DAO_AUTO_UPDATER = true;
    public static String[] LINK_SERVERS = new String[10];

    public static void Server() {
        StringBuilder linkServer = new StringBuilder();
        linkServer.append(NAME).append(":").append(IP).append(":").append(PORT).append(":0,");
        for (int i = 1; i <= 10; i++) {
            LINK_SERVERS[i - 1] = "Server" + i + ":0";
            linkServer.append(LINK_SERVERS[i - 1]).append(",");
        }
        LINK_IP_PORT = linkServer.substring(0, linkServer.length() - 1);
    }
    private static HikariConfig createConfig(String poolName, String databaseName) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(DB_DRIVER);
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8", DB_HOST, DB_PORT, databaseName));
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMinimumIdle(DB_MIN_CONN);
        config.setMaximumPoolSize(DB_MAX_CONN);
        config.setMaxLifetime(DB_MAX_LIFE_TIME);
        config.setPoolName(poolName);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        return config;
    }
    public static HikariDataSource createDataSource(String poolName, String databaseName) {
        HikariConfig config = createConfig(poolName, databaseName);
        return new HikariDataSource(config);
    }
}
