package itstep.learning.services.db;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mysql.cj.jdbc.MysqlDataSource;
import itstep.learning.services.config.ConfigService;
import com.google.gson.JsonPrimitive;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class MySqlDbService implements DbService {
    private Connection connection;
    private final ConfigService configService;
    private final Logger logger;

    @Inject
    public MySqlDbService(ConfigService configService, Logger logger) {
        this.configService = configService;
        this.logger = logger;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            try {
                MysqlDataSource mds = new MysqlDataSource();

                String host = getStringConfig("db:MySql:host", "localhost");
                int port = getIntConfig("db:MySql:port", 3306);
                String database = getStringConfig("db:MySql:database", "java221");
                String user = getStringConfig("db:MySql:user", "user221");
                String password = getStringConfig("db:MySql:password", "pass221");

                String url = String.format("jdbc:mysql://%s:%d/%s", host, port, database);
                mds.setURL(url);
                connection = mds.getConnection(user, password);
                connection.setAutoCommit(false);

                logger.info("MySqlDbService::getConnection: Success! Connecting to database: " + url);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "MySqlDbService::getConnection: Error! Invalid connection to database!", ex);
                throw ex;
            }
        }
        return connection;
    }

    private String getStringConfig(String path, String defaultValue) {
        JsonPrimitive value = configService.getValue(path);
        return value != null ? value.getAsString() : defaultValue;
    }

    private int getIntConfig(String path, int defaultValue) {
        JsonPrimitive value = configService.getValue(path);
        return value != null ? value.getAsInt() : defaultValue;
    }
}
