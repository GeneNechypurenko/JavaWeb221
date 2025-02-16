package itstep.learning.dal.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class User {
    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private int age;
    private boolean isActive;
    private double balance;
    private String birthDate;
    private long createdAt;

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

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
