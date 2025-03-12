package itstep.learning.dal.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class Product {
    private UUID productId;
    private UUID categoryId;
    private String productSlug;
    private String productTitle;
    private String productDescription;
    private String productImageId;
    private double productPrice;
    private int productStock;
    private Date productDeleteMoment;

    public static Product fromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(UUID.fromString(rs.getString("product_id")));
        product.setCategoryId(UUID.fromString(rs.getString("category_id")));
        product.setProductSlug(rs.getString("product_slug"));
        product.setProductTitle(rs.getString("product_title"));
        product.setProductDescription(rs.getString("product_description"));
        product.setProductImageId(rs.getString("product_image_id"));
        product.setProductPrice(rs.getDouble("product_price"));
        product.setProductStock(rs.getInt("product_stock"));
        java.sql.Timestamp timestamp = rs.getTimestamp( "product_delete_moment" ) ;
        product.setProductDeleteMoment(timestamp == null ? null : new Date( timestamp.getTime() ));
        return product;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductSlug() {
        return productSlug;
    }

    public void setProductSlug(String productSlug) {
        this.productSlug = productSlug;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductImageId() {
        return productImageId;
    }

    public void setProductImageId(String productImageId) {
        this.productImageId = productImageId;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductStock() {
        return productStock;
    }

    public void setProductStock(int productStock) {
        this.productStock = productStock;
    }

    public Date getProductDeleteMoment() {
        return productDeleteMoment;
    }

    public void setProductDeleteMoment(Date productDeleteMoment) {
        this.productDeleteMoment = productDeleteMoment;
    }
}
