package itstep.learning.dal.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class Cart {
    private UUID cartId;
    private UUID userAccessId;
    private byte isCartCanceled;
    private double cartPrice;
    private Date cartCreatedAt;
    private Date cartClosedAt;

    public static Cart fromResultSet(ResultSet rs) throws SQLException {
        Cart cart = new Cart();
        cart.setCartId(UUID.fromString(rs.getString("cart_id")));
        cart.setUserAccessId(UUID.fromString(rs.getString("user_access_id")));
        cart.setIsCartCanceled(rs.getByte("cart_is_canceled"));
        cart.setCartPrice(rs.getDouble("cart_price"));
        cart.setCartCreatedAt(rs.getDate("cart_created_at"));
        java.sql.Timestamp timestamp = rs.getTimestamp( "cart_closed_in" ) ;
        cart.setCartClosedAt(timestamp == null ? null : new Date( timestamp.getTime() ));
        return cart;
    }

    public UUID getCartId() {
        return cartId;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
    }

    public UUID getUserAccessId() {
        return userAccessId;
    }

    public void setUserAccessId(UUID userAccessId) {
        this.userAccessId = userAccessId;
    }

    public byte getIsCartCanceled() {
        return isCartCanceled;
    }

    public void setIsCartCanceled(byte isCartCanceled) {
        this.isCartCanceled = isCartCanceled;
    }

    public double getCartPrice() {
        return cartPrice;
    }

    public void setCartPrice(double cartPrice) {
        this.cartPrice = cartPrice;
    }

    public Date getCartCreatedAt() {
        return cartCreatedAt;
    }

    public void setCartCreatedAt(Date cartCreatedAt) {
        this.cartCreatedAt = cartCreatedAt;
    }

    public Date getCartClosedAt() {
        return cartClosedAt;
    }

    public void setCartClosedAt(Date cartClosedAt) {
        this.cartClosedAt = cartClosedAt;
    }
}