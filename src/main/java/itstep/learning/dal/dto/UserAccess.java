package itstep.learning.dal.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class UserAccess {
    private UUID userAccessId;
    private UUID userId;
    private String login;
    private String salt;
    private String dk;
    private String roleId;
    private Date deletedAt;

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getUserAccessId() {
        return userAccessId;
    }

    public void setUserAccessId(UUID userAccessId) {
        this.userAccessId = userAccessId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getDk() {
        return dk;
    }

    public void setDk(String dk) {
        this.dk = dk;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public static UserAccess fromResultSet(ResultSet rs) throws SQLException {
        UserAccess ua = new UserAccess();
        ua.setUserAccessId( UUID.fromString( rs.getString( "user_access_id" ) ) );
        ua.setUserId( UUID.fromString( rs.getString( "user_id" ) ) );
        ua.setLogin( rs.getString( "login" ) );
        ua.setSalt( rs.getString( "salt" ) );
        ua.setDk( rs.getString( "dk" ) );
        ua.setRoleId(rs.getString( "role_id" ) );
        java.sql.Timestamp timestamp = rs.getTimestamp( "ua_delete_dt" ) ;
        ua.setDeletedAt(
                timestamp == null ? null : new Date( timestamp.getTime() ) ); ;
        return ua;
    }
}
