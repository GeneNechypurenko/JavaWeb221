package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.AccessToken;
import itstep.learning.dal.dto.UserAccess;
import itstep.learning.services.config.ConfigService;
import itstep.learning.services.db.DbService;

import java.sql.*;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

@Singleton
public class AccessTokenDao {

    private final DbService dbService;
    private final Logger logger;
    private final ConfigService configService;

    private int tokenLifetime;

    @Inject
    public AccessTokenDao(DbService dbService, Logger logger, ConfigService configService) {
        this.dbService = dbService;
        this.logger = logger;
        this.configService = configService;
        this.tokenLifetime = 0;
    }

    public boolean installTables() {
        String sql = "CREATE TABLE IF NOT EXISTS access_tokens("
                + "access_token_id CHAR(36) PRIMARY KEY DEFAULT(UUID()), "
                + "user_access_id VARCHAR(36) NOT NULL, "
                + "issued_at DATETIME NOT NULL, "
                + "expires_at DATETIME NULL"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";

        try (Statement statement = dbService.getConnection().createStatement()) {
            statement.executeUpdate(sql);
            logger.info("AccessTokenDao::installTables: OK");
            return true;
        } catch (SQLException e) {
            logger.warning("AccessTokenDao::installTables: " + e.getMessage());
        }
        return false;
    }

    public AccessToken createAccessToken(UserAccess userAccess) {
        if (userAccess == null) {
            return null;
        }

        if (tokenLifetime == 0) {
            tokenLifetime = 1000 * configService.getValue("token:lifetime").getAsInt();
        }

        AccessToken token = new AccessToken();
        token.setAccessTokenId(UUID.randomUUID());
        token.setUserAccessId(userAccess.getUserId());
        Date date = new Date();
        token.setIssuedAt(date);
        token.setExpiresAt(new Date(date.getTime() + tokenLifetime));

        String sql = "INSERT INTO access_tokens (access_token_id, user_access_id, issued_at, expires_at) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            stmt.setString(1, token.getAccessTokenId().toString());
            stmt.setString(2, userAccess.getUserAccessId().toString());
            stmt.setTimestamp(3, new Timestamp(token.getIssuedAt().getTime()));
            stmt.setTimestamp(4, new Timestamp(token.getExpiresAt().getTime()));
            stmt.executeUpdate();
            dbService.getConnection().commit();
        } catch (SQLException e) {
            logger.warning("AccessTokenDao::createAccessToken: " + e.getMessage());
            return null;
        }

        return token;
    }

    public UserAccess getUserAccess(String credentials) {
        UUID accessTokenId;
        try {
            accessTokenId = UUID.fromString(credentials);
        } catch (Exception ignored) {
            return null;
        }

        String sql = "SELECT * FROM access_tokens a"
                + " JOIN users_access ua ON a.user_access_id = ua.user_access_id"
                + " WHERE a.access_token_id = ? AND a.expires_at > CURRENT_TIMESTAMP";

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            stmt.setString(1, accessTokenId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                extendAccessTokenValidity(accessTokenId); // Продлеваем срок действия токена
                return UserAccess.fromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.warning("AccessTokenDao::getUserAccess: " + e.getMessage());
        }
        return null;
    }


    public boolean cancelAccessToken(AccessToken token) {
        return true;
    }

    public boolean extendAccessTokenValidity(UUID accessTokenId) {
        if (tokenLifetime == 0) {
            tokenLifetime = 1000 * configService.getValue("token:lifetime").getAsInt();
        }

        String sql = "UPDATE access_tokens SET expires_at = ? WHERE access_token_id = ?";

        try (PreparedStatement stmt = dbService.getConnection().prepareStatement(sql)) {
            Date newExpirationDate = new Date(System.currentTimeMillis() + tokenLifetime);
            stmt.setTimestamp(1, new Timestamp(newExpirationDate.getTime()));
            stmt.setString(2, accessTokenId.toString());
            int updatedRows = stmt.executeUpdate();
            dbService.getConnection().commit();
            return updatedRows > 0;
        } catch (SQLException e) {
            logger.warning("AccessTokenDao::extendAccessTokenValidity: " + e.getMessage());
            return false;
        }
    }
}
