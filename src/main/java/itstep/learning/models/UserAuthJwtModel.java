package itstep.learning.models;

import itstep.learning.dal.dto.User;

public class UserAuthJwtModel {
    private User user;
    private String jwtToken;

    public UserAuthJwtModel() {
    }

    public UserAuthJwtModel(User user, String jwtToken) {
        this.user = user;
        this.jwtToken = jwtToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
