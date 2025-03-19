package itstep.learning.models;

import itstep.learning.dal.dto.AccessToken;
import itstep.learning.dal.dto.Cart;
import itstep.learning.dal.dto.User;
import itstep.learning.dal.dto.UserAccess;

public class UserAuthViewModel {
    private User user;
    private UserAccess userAccess;
    private AccessToken accessToken;
    private Cart cart;

    public UserAccess getUserAccess() {
        return userAccess;
    }

    public void setUserAccess(UserAccess userAccess) {
        this.userAccess = userAccess;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public UserAuthViewModel() {
    }

    public UserAuthViewModel(User user, UserAccess userAccess, AccessToken accessToken, Cart cart)
    {
        this.user = user;
        this.userAccess = userAccess;
        this.accessToken = accessToken;
        this.cart = cart;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }
}
