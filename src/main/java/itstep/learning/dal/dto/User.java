package itstep.learning.dal.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class User {
    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private Integer age;
    private boolean isActive;
    private Double balance;
    private String birthDate;
    private long createdAt;
    private Date deletedAt;

    public static User fromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.userId = UUID.fromString(rs.getString("user_id"));
        user.name = rs.getString("name");
        user.email = rs.getString("email");
        user.phone = rs.getString("phone");
        user.age = rs.getInt("age");
        user.isActive = rs.getBoolean("is_active");
        user.balance = rs.getDouble("balance");
        user.birthDate = rs.getString("birth_date");
        user.createdAt = rs.getLong("created_at");
        java.sql.Timestamp timestamp = rs.getTimestamp( "deleted_at" ) ;
        user.setDeletedAt(timestamp == null ? null : new Date( timestamp.getTime() ));
        return user;
    }

    // Геттеры и сеттеры
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
}
