package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.Cart;
import itstep.learning.dal.dto.CartItem;
import itstep.learning.dal.dto.Product;
import itstep.learning.services.db.DbService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class CartDao {
    private final DbService dbService;
    private final Logger logger;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Inject
    public CartDao(DbService dbService, Logger logger) {
        this.dbService = dbService;
        this.logger = logger;
    }

    public boolean installTables() {
        Future<Boolean> task1 = CompletableFuture
                .supplyAsync(this::installCartItems);

        Future<Boolean> task2 = CompletableFuture
                .supplyAsync(this::installCarts);
        try {
            boolean res1 = task1.get();
            boolean res2 = task2.get();
            try {
                dbService.getConnection().commit();
            } catch (SQLException ignore) {
            }
            return res1 && res2;

        } catch (ExecutionException | InterruptedException ignore) {
            return false;
        }
    }

    private boolean installCartItems() {
        String sql = "CREATE TABLE IF NOT EXISTS cart_items("
                + "cart_item_id  CHAR(36) PRIMARY KEY DEFAULT( UUID() ),"
                + "cart_id CHAR(36) NOT NULL,"
                + "product_id CHAR(36) NOT NULL,"
                + "cart_item_price DECIMAL(12, 2) NOT NULL,"
                + "quantity INT NOT NULL DEFAULT 1,"
                + "action_id CHAR(36) NULL"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";
        try (Statement statement = dbService.getConnection().createStatement()) {
            statement.executeUpdate(sql);
            logger.info("CartDao::installCartItems OK");
            return true;
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "CartDao::installCartItems {0} sql: '{1}'", new Object[]{ex.getMessage(), sql});
        }
        return false;
    }

    private boolean installCarts() {
        String sql = "CREATE TABLE IF NOT EXISTS cart("
                + "cart_id  CHAR(36) PRIMARY KEY DEFAULT( UUID() ),"
                + "user_access_id CHAR(36) NOT NULL,"
                + "cart_is_canceled TINYINT NOT NULL,"
                + "cart_price DECIMAL(14, 2) NOT NULL,"
                + "cart_created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "cart_closed_in DATETIME NULL"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";
        try (Statement statement = dbService.getConnection().createStatement()) {
            statement.executeUpdate(sql);
            logger.info("CartDao::installCarts OK");
            return true;
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "CartDao::installCarts {0} sql: {1}", new Object[]{ex.getMessage(), sql});
        }
        return false;
    }

    public Cart getCart(UUID cartId) {
        String sql = "SELECT * FROM cart c JOIN cart_items ci ON c.cart_id = ci.cart_id WHERE ci.cart_id = ? ";
        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, cartId.toString());
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return Cart.fromResultSet(rs);
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "CartDao::getCart {0} sql: {1}", new Object[]{ex.getMessage(), sql});
        }
        return null;
    }

    public Cart getUserCart(UUID userAccessId, boolean createNew) {
        String sql = String.format(Locale.ROOT,
                "SELECT * FROM cart c WHERE c.cart_closed_in IS NULL" +
                        " AND c.user_access_id = '%s'", userAccessId.toString());

        try (Statement stmt = dbService.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return Cart.fromResultSet(rs);
            } else if (createNew) {
                Cart cart = new Cart();
                cart.setCartId(UUID.randomUUID());
                cart.setUserAccessId(userAccessId);
                cart.setCartCreatedAt(new Date());
                sql = String.format(Locale.ROOT,
                        "INSERT INTO cart (cart_id, user_access_id, cart_created_at, cart_price, cart_is_canceled) VALUES('%s', '%s', '%s', 0, 0)",
                        cart.getCartId().toString(), userAccessId.toString(), dateFormat.format(cart.getCartCreatedAt()));
                stmt.executeUpdate(sql);
                dbService.getConnection().commit();
                return cart;
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "CartDao::getUserCart {0} sql: {1}", new Object[]{e.getMessage(), sql});
        }

        return null;
    }

    public boolean addToCart(Cart cart, Product product) {
        CartItem cartItem;
        boolean isNew;

        String sql = "SELECT * FROM cart_items ci WHERE ci.cart_id = ? AND ci.product_id = ?";

        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, cart.getCartId().toString());
            prep.setString(2, product.getProductId().toString());
            ResultSet rs = prep.executeQuery();
            double addedPrice = product.getProductPrice();;
            if (rs.next()) {
                cartItem = CartItem.fromResultSet(rs);
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartItem.setCartItemPrice(cartItem.getCartItemPrice() + product.getProductPrice());
                cart.setCartPrice(cart.getCartPrice() + addedPrice);
                isNew = false;
            } else {
                cartItem = new CartItem();
                cartItem.setCartItemId(UUID.randomUUID());
                cartItem.setCartId(cart.getCartId());
                cartItem.setProductId(product.getProductId());
                cartItem.setQuantity(1);
                cartItem.setCartItemPrice(addedPrice);
                isNew = true;
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "CartDao::addToCart {0} sql: '{1}'", new Object[]{ex.getMessage(), sql});
            return false;
        }

        sql = isNew
                ? "INSERT INTO cart_items(cart_id, product_id, quantity, cart_item_price, cart_item_id) VALUES(?, ?, ?, ?, ?)"
                : "UPDATE cart_items SET cart_id = ?, product_id = ?, quantity = ?, cart_item_price = ? WHERE cart_item_id = ?";

        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, cart.getCartId().toString());
            prep.setString(2, product.getProductId().toString());
            prep.setInt(3, cartItem.getQuantity());
            prep.setDouble(4, cartItem.getCartItemPrice());
            prep.setString(5, cartItem.getCartItemId().toString());
            prep.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "CartDao::addToCart {0} sql: '{1}'", new Object[]{ex.getMessage(), sql});
            return false;
        }

        sql = "UPDATE cart SET cart_price = ? WHERE cart_id = ?";
        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setDouble(1, cart.getCartPrice());
            prep.setString(2, cart.getCartId().toString());
            prep.executeUpdate();
            dbService.getConnection().commit();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "CartDao::addToCart {0} sql: '{1}'", new Object[]{ex.getMessage(), sql});
            return false;
        }

        return true;
    }
}
