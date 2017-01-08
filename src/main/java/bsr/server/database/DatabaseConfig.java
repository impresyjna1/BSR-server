package bsr.server.database;

import bsr.server.models.Account;
import bsr.server.models.AccountCounter;
import bsr.server.models.User;
import org.mongodb.morphia.Datastore;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Impresyjna on 28.12.2016.
 */
public class DatabaseConfig {
    private ArrayList<User> users = new ArrayList<User>();

    public void initUsers() {
        User user1 = new User("Alicja", "Grzyb", "111111", "grzyb");
        User user2 = new User("Krzysztof", "Kowalski", "222222", "kowalski");
        User user3 = new User("Karolina", "Nowak", "333333", "nowak");
        User user4 = new User("Zbigniew", "Stonoga ", "444444", "stonoga");
        User user5 = new User("Olek", "Michnik", "555555", "michnik");
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);
    }

    public void initAccounts() {
        Datastore mongoDataStore = DatabaseHandler.getInstance().getMongoDataStore();

        User user = users.get(0);

        Account account1 = new Account("Account1", 500);
        Account account2 = new Account("Account2", 100);
        Account account3 = new Account("Account3", 200);
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);
        mongoDataStore.save(accounts);
        user.setAccounts(accounts);

        user = users.get(1);
        account1 = new Account("Account4", 100);
        account2 = new Account("Account5", 200);
        accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);
        mongoDataStore.save(accounts);
        user.setAccounts(accounts);

        user = users.get(2);
        account1 = new Account("Account6", 300);
        accounts = new ArrayList<>();
        accounts.add(account1);
        mongoDataStore.save(accounts);
        user.setAccounts(accounts);

        user = users.get(3);
        account1 = new Account("Account7", 400);
        account2 = new Account("Account8", 500);
        account3 = new Account("Account9", 100);
        Account account4 = new Account("Account10", 200);
        accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);
        accounts.add(account4);
        mongoDataStore.save(accounts);
        user.setAccounts(accounts);

        user = users.get(4);
        account1 = new Account("Account11", 300);
        accounts = new ArrayList<>();
        accounts.add(account1);
        mongoDataStore.save(accounts);
        user.setAccounts(accounts);
    }

    public void initDatabase() {
        Datastore mongoDataStore = DatabaseHandler.getInstance().getMongoDataStore();
        initUsers();
        initAccounts();
        mongoDataStore.save(users);
    }
}
