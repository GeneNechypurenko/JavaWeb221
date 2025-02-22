package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import java.sql.SQLException;

@Singleton
public class DataContext {

    private final UserDao userDao;
    private final AccessTokenDao accessTokenDao;

    @Inject
    public DataContext(Injector injector) throws SQLException {
        userDao = injector.getInstance(UserDao.class) ;
        accessTokenDao = injector.getInstance(AccessTokenDao.class) ;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public AccessTokenDao getAccessTokenDao() {
        return accessTokenDao;
    }
}
