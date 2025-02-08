package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import itstep.learning.services.db.DbService;

import java.sql.SQLException;
import java.util.logging.Logger;

@Singleton
public class DataContext {

    private final Logger logger;
    private final UserDao userDao;
    private final Injector injector;

    @Inject
    public DataContext(DbService dbService, Logger logger, Injector injector) throws SQLException {
        this.logger = logger;
        this.injector = injector;
        userDao = injector.getInstance(UserDao.class) ;
    }

    public UserDao getUserDao() {
        return userDao;
    }
}
