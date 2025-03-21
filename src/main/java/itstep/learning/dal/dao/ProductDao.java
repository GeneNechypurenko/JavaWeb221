package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.Product;
import itstep.learning.services.db.DbService;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class ProductDao {
    private final Logger logger;
    private final DbService dbService;

    @Inject
    public ProductDao(Logger logger, DbService dbService) {
        this.logger = logger;
        this.dbService = dbService;
    }

    public Product addNewProduct(Product product) {
        product.setProductId(UUID.randomUUID());
        String sql = "INSERT INTO products" +
                " (product_id," +
                " category_id," +
                " product_title," +
                " product_description," +
                " product_slug," +
                " product_image_id," +
                " product_delete_moment," +
                " product_price," +
                " product_stock)" +
                " VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, product.getProductId().toString());
            prep.setString(2, product.getCategoryId().toString());
            prep.setString(3, product.getProductTitle());
            prep.setString(4, product.getProductDescription());
            prep.setString(5, product.getProductSlug());
            prep.setString(6, product.getProductImageId());
            prep.setTimestamp(7, product.getProductDeleteMoment() == null ? null : new Timestamp(product.getProductDeleteMoment().getTime()));
            prep.setDouble(8, product.getProductPrice());
            prep.setInt(9, product.getProductStock());
            prep.executeUpdate();
            dbService.getConnection().commit();
            return product;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "ProductDao::addNewProduct {0}, sql: {1}", new Object[]{e.getMessage(), sql});
        }
        return null;
    }

    public boolean installTable() {
        String sql = "CREATE TABLE IF NOT EXISTS products("
                + "product_id CHAR(36) PRIMARY KEY DEFAULT(UUID()), "
                + "category_id CHAR(36) NOT NULL, "
                + "product_slug VARCHAR(64) NULL, "
                + "product_title VARCHAR(64) NOT NULL, "
                + "product_description VARCHAR(256) NOT NULL, "
                + "product_image_id VARCHAR(64) NULL, "
                + "product_delete_moment DATETIME NULL, "
                + "product_price FLOAT NOT NULL, "
                + "product_stock INT NOT NULL, "
                + "UNIQUE(product_slug) "
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";

        try (Statement statement = dbService.getConnection().createStatement()) {
            statement.executeUpdate(sql);
            logger.info("ProductDao::installTable: OK");
            return true;
        } catch (SQLException e) {
            logger.warning("ProductDao::installTable: " + e.getMessage());
        }
        return false;
    }

    public Map<UUID, Integer> getProductsCountByCategories() {
        String sql = "SELECT category_id, COUNT(*) AS product_count FROM products GROUP BY category_id";
        Map<UUID, Integer> categoryProductCounts = new HashMap<>();

        try (Statement statement = dbService.getConnection().createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                UUID categoryId = UUID.fromString(rs.getString("category_id"));
                int productCount = rs.getInt("product_count");
                categoryProductCounts.put(categoryId, productCount);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "ProductDao::getProductsCountByCategories {0}, sql: {1}", new Object[]{e.getMessage(), sql});
        }
        return categoryProductCounts;
    }

    public Product getProductById(UUID productId) {
        String sql = "SELECT * FROM products p WHERE p.product_id = ?";
        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, productId.toString());
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                return Product.fromResultSet(rs);
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "ProductDao::getProductById {0} sql: '{1}'", new Object[]{ex.getMessage(), sql});
        }
        return null;
    }
}
