package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import java.sql.SQLException;

@Singleton
public class DataContext {

    private final UserDao userDao;
    private final AccessTokenDao accessTokenDao;
    private final CategoryDao categoryDao;
    private final ProductDao productDao;
    private final CartDao cartDao;

    @Inject
    public DataContext(Injector injector) throws SQLException {
        userDao = injector.getInstance(UserDao.class);
        accessTokenDao = injector.getInstance(AccessTokenDao.class);
        categoryDao = injector.getInstance(CategoryDao.class);
        productDao = injector.getInstance(ProductDao.class);
        cartDao = injector.getInstance(CartDao.class);
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public AccessTokenDao getAccessTokenDao() {
        return accessTokenDao;
    }

    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public ProductDao getProductDao() {
        return productDao;
    }

    public CartDao getCartDao() {
        return cartDao;
    }
}
