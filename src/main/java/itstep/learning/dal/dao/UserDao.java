package itstep.learning.dal.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class UserDao {

    private final Connection connection;
    private final Logger logger;

    public UserDao(Connection connection, Logger logger) {
        this.connection = connection;
        this.logger = logger;
    }

    public boolean installTables() {
        return installUsers() && installUsersAccess();
    }

    private boolean installUsers() {
        String sql = "CREATE TABLE IF NOT EXISTS users("
                + "user_id CHAR(36) PRIMARY KEY DEFAULT(UUID()),"
                + "name VARCHAR(128) NOT NULL,"
                + "email VARCHAR(265) NULL,"
                + "phone VARCHAR(32) NULL"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            logger.info("UserDao::installUsers: OK");
            return true;
        } catch (SQLException e) {
            logger.warning("UserDao::installUsers: " + e.getMessage());
        }
        return false;
    }

    private boolean installUsersAccess() {
        String sql = "CREATE TABLE IF NOT EXISTS users_access("
                + "user_access_id CHAR(36) PRIMARY KEY DEFAULT(UUID()),"
                + "user_id VARCHAR(36) NOT NULL,"
                + "role_id VARCHAR(16) NOT NULL,"
                + "login VARCHAR(128) NOT NULL,"
                + "salt CHAR(16) NOT NULL,"
                + "dk CHAR(20) NOT NULL,"
                + "UNIQUE(login)"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            logger.info("UserDao::installUsersAccess: OK");
            return true;
        } catch (SQLException e) {
            logger.warning("UserDao::installUsersAccess: " + e.getMessage());
        }
        return false;
    }

}
