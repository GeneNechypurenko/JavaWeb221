package itstep.learning.services.db;

import com.google.inject.Singleton;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

@Singleton
public class MySqlDbService implements DbService {
    private Connection connection;

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            MysqlDataSource mds = new MysqlDataSource();
            mds.setURL("jdbc:mysql://localhost:3308/java221");
            connection = mds.getConnection("user221", "pass221");
            connection.setAutoCommit(false);
        }
        return connection;
    }
}
