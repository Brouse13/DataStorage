package es.brouse.datastorage.storage.objects;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

public class Mysql {
    @Getter private static Mysql instance = new Mysql();
    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource dataSource;

    //Private constructor to force using class instance pattern
    private Mysql() {}

    static {
        if (dataSource == null) {
            config.setJdbcUrl("jdbc:mysql://localhost:3306/minecraft");
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setUsername("brouse");
            config.setPassword("test");
            config.setMaximumPoolSize(6);
            config.setConnectionTimeout(5000);
            dataSource = new HikariDataSource(config);
        }
    }

    /**
     * Get the established connection
     * ThreadPool connections - 6
     * ThreadPool timeout     - 5000ms
     * @return the sql connection
     * @throws SQLException if any error was established
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
