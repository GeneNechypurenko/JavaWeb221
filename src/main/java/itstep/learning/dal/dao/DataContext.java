package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.db.DbService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

@Singleton
public class DataContext {

    private final Connection connection;
    private final Logger logger;
    private final UserDao userDao;

    @Inject
    public DataContext(DbService dbService, Logger logger) throws SQLException {
        this.connection = dbService.getConnection();
        this.logger = logger;
        userDao = new UserDao(connection, logger);
    }

    public UserDao getUserDao() {
        return userDao;
    }
}
