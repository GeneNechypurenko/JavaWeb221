package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.User;
import itstep.learning.models.UserSignupFormModel;
import itstep.learning.services.db.DbService;
import itstep.learning.services.kdf.KdfService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Logger;

@Singleton
public class UserDao {

    private final Connection connection;
    private final Logger logger;
    private final KdfService kdfService;

    @Inject
    public UserDao(DbService dbService, Logger logger, KdfService kdfService) throws SQLException {
        this.connection = dbService.getConnection();
        this.logger = logger;
        this.kdfService = kdfService;
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

    public User addUser(UserSignupFormModel userModel) {

        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setName(userModel.getName());
        user.setEmail(userModel.getEmails().get(0));
        user.setPhone(userModel.getPhones().get(0));

        String sql = "INSERT INTO users (user_id, name, email, phone) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement prep = this.connection.prepareStatement(sql)) {
            prep.setString(1, user.getUserId().toString());
            prep.setString(2, user.getName());
            prep.setString(3, user.getEmail());
            prep.setString(4, user.getPhone());
            this.connection.setAutoCommit(false);
            prep.executeUpdate();

        } catch (SQLException e) {
            logger.warning("UserDao::adduser" + e.getMessage());
            try {
                this.connection.rollback();
            } catch (SQLException ignore) {
            }
            return null;
        }

        sql = "INSERT INTO users_access (user_access_id, user_id, role_id, login, salt, dk) "
                + "VALUES (UUID(), ?, 'guest', ?, ?, ?)";

        try (PreparedStatement prep = this.connection.prepareStatement(sql)) {
            prep.setString(1, user.getUserId().toString());
            prep.setString(2, user.getEmail());

            String salt = UUID.randomUUID().toString().substring(0, 16);
            prep.setString(3, salt);
            prep.setString(4, kdfService.dk(userModel.getPassword(), salt));

            prep.executeUpdate();
            this.connection.commit();

        } catch (SQLException e) {
            logger.warning("UserDao::adduser" + e.getMessage());
            try {
                this.connection.rollback();
            } catch (SQLException ignore) {
            }
            return null;
        }

        return user;
    }
}
