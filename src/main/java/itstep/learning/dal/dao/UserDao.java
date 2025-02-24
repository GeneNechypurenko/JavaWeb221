package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.User;
import itstep.learning.models.UserSignupFormModel;
import itstep.learning.services.db.DbService;
import itstep.learning.services.kdf.KdfService;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Singleton
public class UserDao {

    private final Connection connection;
    private final DbService dbService;
    private final Logger logger;
    private final KdfService kdfService;

    @Inject
    public UserDao(DbService dbService, DbService dbService1, Logger logger, KdfService kdfService) throws SQLException {
        this.dbService = dbService1;
        this.connection = dbService.getConnection();
        this.logger = logger;
        this.kdfService = kdfService;
    }

    public boolean installTables() {
        Future<Boolean> taskUsers = CompletableFuture.supplyAsync((this::installUsers));
//                .thenApply(b -> { return 1; })
//                .thenApply(i -> true);
        Future<Boolean> taskUsersAccess = CompletableFuture.supplyAsync((this::installUsersAccess));
        Future<Boolean> taskUserRoles = CompletableFuture.supplyAsync((this::installUserRoles));
        Future<Boolean> taskDefaultRoles = CompletableFuture.supplyAsync((this::insertDefaultRoles));

        try {
            boolean resUsers = taskUsers.get();
            boolean resUserAccess = taskUsersAccess.get();
            boolean resUserRoles = taskUserRoles.get();
            boolean resDefaultRoles = taskDefaultRoles.get();
            try {
                dbService.getConnection().commit();
            } catch (SQLException ignored) {
            }
            return resUsers && resUserAccess && resUserRoles && resDefaultRoles;
        } catch (ExecutionException | InterruptedException ignore) {
            return false;
        }
    }

    private boolean installUsers() {
        String sql = "CREATE TABLE IF NOT EXISTS users("
                + "user_id CHAR(36) PRIMARY KEY DEFAULT(UUID()),"
                + "name VARCHAR(128) NOT NULL,"
                + "email VARCHAR(265) NULL,"
                + "phone VARCHAR(32) NULL,"
                + "deleted_at DATETIME NULL"
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
                + "ua_delete_dt DATETIME NULL,"
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

    private boolean installUserRoles() {
        String sql = "CREATE TABLE IF NOT EXISTS user_roles ("
                + "id VARCHAR(16) PRIMARY KEY,"
                + "description VARCHAR(255) NOT NULL,"
                + "canCreate BOOLEAN NOT NULL DEFAULT FALSE,"
                + "canRead BOOLEAN NOT NULL DEFAULT TRUE,"
                + "canUpdate BOOLEAN NOT NULL DEFAULT FALSE,"
                + "canDelete BOOLEAN NOT NULL DEFAULT FALSE"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            logger.info("UserDao::installUserRoles: OK");
            return true;
        } catch (SQLException e) {
            logger.warning("UserDao::installUserRoles: " + e.getMessage());
        }
        return false;
    }

    private boolean insertDefaultRoles() {
        String sql = "INSERT INTO user_roles (id, description, canCreate, canRead, canUpdate, canDelete) VALUES "
                + "('admin', 'Administrator', TRUE, TRUE, TRUE, TRUE),"
                + "('guest', 'Guest', FALSE, TRUE, FALSE, FALSE),"
                + "('moder', 'Moderator', FALSE, TRUE, TRUE, FALSE) "
                + "ON DUPLICATE KEY UPDATE id = id;";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            logger.info("UserDao::insertDefaultRoles: OK");
            return true;
        } catch (SQLException e) {
            logger.warning("UserDao::insertDefaultRoles: " + e.getMessage());
        }
        return false;
    }

    public User addUser(UserSignupFormModel userModel) {

        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setName(userModel.getName());
        user.setEmail(userModel.getEmail());
        user.setPhone(userModel.getPhone());
        user.setAge(userModel.getAge());
        user.setActive(userModel.isActive());
        user.setBalance(userModel.getBalance());
        user.setBirthDate(userModel.getBirthDate());
        user.setCreatedAt(System.currentTimeMillis());

        String sql = "INSERT INTO users (user_id, name, email, phone, age, is_active, balance, birth_date, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement prep = this.connection.prepareStatement(sql)) {
            prep.setString(1, user.getUserId().toString());
            prep.setString(2, user.getName());
            prep.setString(3, user.getEmail());
            prep.setString(4, user.getPhone());
            prep.setInt(5, user.getAge());
            prep.setBoolean(6, user.isActive());
            prep.setDouble(7, user.getBalance());
            prep.setString(8, user.getBirthDate());
            prep.setLong(9, user.getCreatedAt());
            // this.connection.setAutoCommit(false);
            prep.executeUpdate();
        } catch (SQLException e) {
            logger.warning("UserDao::addUser: " + e.getMessage());
            try {
                this.connection.rollback();
            } catch (SQLException ignore) {
            }
            return null;
        }

        sql = "INSERT INTO users_access (user_access_id, user_id, role_id, login, salt, dk) "
                + "VALUES (UUID(), ?, 'user', ?, ?, ?)";

        try (PreparedStatement prep = this.connection.prepareStatement(sql)) {
            prep.setString(1, user.getUserId().toString());
            prep.setString(2, user.getEmail());

            String salt = UUID.randomUUID().toString().substring(0, 16);
            prep.setString(3, salt);
            prep.setString(4, kdfService.dk(userModel.getPassword(), salt));

            prep.executeUpdate();
            this.connection.commit();
        } catch (SQLException e) {
            logger.warning("UserDao::addUser: " + e.getMessage());
            try {
                this.connection.rollback();
            } catch (SQLException ignore) {
            }
            return null;
        }

        return user;
    }

    public User authorize(String login, String password) {
        String sql =
                "SELECT * FROM users_access ua " +
                        "JOIN users u ON ua.user_id =  u.user_id " +
                        "WHERE ua.login = ?";

        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, login);
            ResultSet rs = prep.executeQuery();

            if (rs.next()) {
                String dk = kdfService.dk(password, rs.getString("salt"));

                if (Objects.equals(dk, rs.getString("dk"))) {
                    return User.fromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.warning("UserDao::authorize: " + e.getMessage());
        }
        return null;
    }

    public User getUserById(String id) {

        UUID uuid;

        try {
            uuid = UUID.fromString(id);

        } catch (IllegalArgumentException e) {
            logger.warning("UserDao::getUserById::String: " + e.getMessage());
            return null;
        }
        return getUserById(uuid);
    }

    public User getUserById(UUID uuid) {

        String sql = String.format(
                "SELECT u.* FROM users u WHERE u.user_id = '%s'",
                uuid.toString()
        );

        try (Statement stmt = dbService.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return User.fromResultSet(rs);
            }

        } catch (Exception e) {
            logger.warning("UserDao::getUserById::UUID: " + e.getMessage());
        }
        return null;
    }

    public CompletableFuture<Boolean> updateUserAsync(User user) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> data = new HashMap<>();

            if (user.getName() != null) data.put("name", user.getName());
            if (user.getEmail() != null) data.put("email", user.getEmail());
            if (user.getPhone() != null) data.put("phone", user.getPhone());
            if (user.getAge() != null) data.put("age", user.getAge());
            if (user.getBalance() != null) data.put("balance", user.getBalance());
            if (user.getBirthDate() != null) data.put("birth_date", user.getBirthDate());
            data.put("is_active", user.isActive());

            if (data.isEmpty()) return true;

            StringBuilder sql = new StringBuilder("UPDATE users SET ");
            boolean first = true;

            for (String key : data.keySet()) {
                if (!first) sql.append(", ");
                sql.append(key).append(" = ?");
                first = false;
            }
            sql.append(" WHERE user_id = ?");

            final String query = sql.toString();
            final String sqlAccess = "UPDATE users_access SET login = ? WHERE user_id = ?";

            try {
                try (PreparedStatement prep = connection.prepareStatement(query)) {
                    int index = 1;
                    for (Object value : data.values()) {
                        prep.setObject(index++, value);
                    }
                    prep.setString(index, user.getUserId().toString());

                    if (prep.executeUpdate() <= 0) {
                        connection.rollback();
                        return false;
                    }
                }

                if (user.getEmail() != null) {
                    try (PreparedStatement prep = connection.prepareStatement(sqlAccess)) {
                        prep.setString(1, user.getEmail());
                        prep.setString(2, user.getUserId().toString());

                        if (prep.executeUpdate() <= 0) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {
                }
                logger.warning("UserDao::updateUser: " + e.getMessage());
                return false;
            }
        });
    }

    public CompletableFuture<Void> deleteUserAsync(User user) {

        String sql1 = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP," +
                " name = ''," +
                " email = NULL," +
                " phone = NULL," +
                " age = 0," +
                " birth_date = CURRENT_TIMESTAMP," +
                " is_active = 0" +
                " WHERE user_id = ?";
        String sql2 = "UPDATE users_access SET ua_delete_dt = CURRENT_TIMESTAMP, login = '' WHERE user_id = ?";

        CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
            try (Connection connection = dbService.getConnection()) {

                try (PreparedStatement stmt1 = connection.prepareStatement(sql1)) {
                    stmt1.setString(1, user.getUserId().toString());
                    stmt1.executeUpdate();
                }

                try (PreparedStatement stmt2 = connection.prepareStatement(sql2)) {
                    stmt2.setString(1, user.getUserId().toString());
                    stmt2.executeUpdate();
                }

                connection.commit();
            } catch (SQLException e) {
                logger.warning("Error in deleting user: " + e.getMessage());
                try {
                    connection.rollback();
                } catch (SQLException ignored) {
                }
            }
        });

        return task;
    }
}
