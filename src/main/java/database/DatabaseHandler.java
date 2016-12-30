package database;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import models.Account;
import models.AccountCounter;
import models.User;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Impresyjna on 27.12.2016.
 */
public class DatabaseHandler {
    private static DatabaseHandler ourInstance = new DatabaseHandler();
    public static DatabaseHandler getInstance() {
        return ourInstance;
    }
    ConnectionSource connectionSource;
    private Dao<Account,String> accountDao;
    private Dao<User,String> userDao;
    private Dao<AccountCounter,String> accountCounterDao;
    private DatabaseHandler() {
    }

    public void initDatabase() {
        try {
            String databaseUrl = "jdbc:sqlite:bank.db";
            // create a connection source to our database
            connectionSource = new JdbcConnectionSource(databaseUrl);

            TableUtils.createTableIfNotExists(connectionSource, Account.class);
            TableUtils.createTableIfNotExists(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, AccountCounter.class);
            // instantiate the DAO to handle Account with String id
            accountDao = DaoManager.createDao(connectionSource, Account.class);
            userDao = DaoManager.createDao(connectionSource, User.class);
            accountCounterDao = DaoManager.createDao(connectionSource, AccountCounter.class);

            if(accountCounterDao.queryForAll().isEmpty()) {
                new DatabaseConfig().initAccountCounter();
            }

            if (userDao.queryForAll().isEmpty()) {
                new DatabaseConfig().initDatabase();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Opened database successfully");
    }

    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    public Dao<Account, String> getAccountDao() {
        return accountDao;
    }

    public Dao<User, String> getUserDao() {
        return userDao;
    }

    public Dao<AccountCounter, String> getAccountCounterDao() {
        return accountCounterDao;
    }

    public void closeConnection() {
        try {
            connectionSource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
