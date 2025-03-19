package itstep.learning.dal.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Category {
    private UUID categoryId;
    private String categorySlug;
    private String categoryTitle;
    private String categoryDescription;
    private String categoryImageId;
    private Date categoryDeleteMoment;

    private List<Product> products;

    public static Category fromResultSet(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(UUID.fromString(rs.getString("category_id")));
        category.setCategorySlug(rs.getString("category_slug"));
        category.setCategoryTitle(rs.getString("category_title"));
        category.setCategoryDescription(rs.getString("category_description"));
        category.setCategoryImageId(rs.getString("category_image_id"));
        java.sql.Timestamp timestamp = rs.getTimestamp( "category_delete_moment" ) ;
        category.setCategoryDeleteMoment(timestamp == null ? null : new Date( timestamp.getTime() ));
        return category;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryImageId() {
        return categoryImageId;
    }

    public void setCategoryImageId(String categoryImageId) {
        this.categoryImageId = categoryImageId;
    }

    public Date getCategoryDeleteMoment() {
        return categoryDeleteMoment;
    }

    public void setCategoryDeleteMoment(Date categoryDeleteMoment) {
        this.categoryDeleteMoment = categoryDeleteMoment;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
