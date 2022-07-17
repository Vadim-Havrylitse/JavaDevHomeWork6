package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import util.PropertiesLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class DataSource {

    private final static String JDBC_URL = "url";
    private final static String USERNAME = "user";
    private final static String PASSWORD = "password";
    private final static String DRIVER = "driverClassName";
    private final static String POOL_SIZE = "maximum-pool-size";
    private final static String LIFE_TIME = "max-lifetime";
    private final static String CONNECTION_TIMEOUT = "connection-timeout";

    private final static String FILE_NAME_CONFIG = "db_config.properties";
    private final static HikariConfig config = new HikariConfig();
    private static HikariDataSource dataSource;
    private static boolean initFlag = false;

    private DataSource(){}

    public static Connection getConnection() throws SQLException {
        if (!initFlag){
            doInit();
            initFlag = true;
        }
        return dataSource.getConnection();
    }

    private static void doInit(){
        try {
            Properties properties;
            properties = PropertiesLoader.getAllPropertiesFromPropertyFile(FILE_NAME_CONFIG);
            String url = properties.getProperty(JDBC_URL);
            String username = properties.getProperty(USERNAME);
            String password = properties.getProperty(PASSWORD);

            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName(properties.getProperty(DRIVER));
            config.setMaximumPoolSize(Integer.parseInt(properties.getProperty(POOL_SIZE)));
            config.setMaxLifetime(Long.parseLong(properties.getProperty(LIFE_TIME)));
            config.setConnectionTimeout(Long.parseLong(properties.getProperty(CONNECTION_TIMEOUT)));

            dataSource = new HikariDataSource(config);

            Flyway flyway = Flyway.configure()
                    .dataSource(url,
                            username,
                            password)
                    .load();
            flyway.migrate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}