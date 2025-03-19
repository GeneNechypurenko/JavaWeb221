package itstep.learning.dal.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CartItem {
    private UUID cartItemId;
    private UUID cartId;
    private UUID productId;
    private UUID actionId;
    private double cartItemPrice;
    private int quantity;

    public static CartItem fromResultSet(ResultSet rs) throws SQLException {
        CartItem cartItem = new CartItem();
        cartItem.setCartItemId(UUID.fromString(rs.getString("cart_item_id")));
        cartItem.setCartId(UUID.fromString(rs.getString("cart_id")));
        cartItem.setProductId(UUID.fromString(rs.getString("product_id")));
        String actionId = rs.getString("action_id");
        if(actionId != null) {
            cartItem.setActionId(UUID.fromString(rs.getString(actionId)));
        }
        cartItem.setCartItemPrice(rs.getDouble("cart_item_price"));
        cartItem.setQuantity(rs.getInt("quantity"));
        return cartItem;
    }

    public UUID getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(UUID cartItemId) {
        this.cartItemId = cartItemId;
    }

    public UUID getCartId() {
        return cartId;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getActionId() {
        return actionId;
    }

    public void setActionId(UUID actionId) {
        this.actionId = actionId;
    }

    public double getCartItemPrice() {
        return cartItemPrice;
    }

    public void setCartItemPrice(double cartItemPrice) {
        this.cartItemPrice = cartItemPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
