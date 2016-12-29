package database;

import models.Account;
import models.AccountCounter;
import models.User;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Impresyjna on 28.12.2016.
 */
public class DatabaseConfig {
    private ArrayList<User> users = new ArrayList<User>();
    private ArrayList<Account> accounts = new ArrayList<Account>();
    private AccountCounter accountCounter;
    DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    public ArrayList<User> getUsers() {
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
        return users;
    }

    public ArrayList<Account> getAccounts() {
        User user = users.get(0);
        Account account1 = new Account(user);
        Account account2 = new Account(user);
        Account account3 = new Account(user);
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);

        user = users.get(1);
        account1 = new Account(user);
        account2 = new Account(user);
        accounts.add(account1);
        accounts.add(account2);

        user = users.get(2);
        account1 = new Account(user);
        accounts.add(account1);

        user = users.get(3);
        account1 = new Account(user);
        account2 = new Account(user);
        account3 = new Account(user);
        Account account4 = new Account(user);
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);
        accounts.add(account4);

        user = users.get(4);
        account1 = new Account(user);
        accounts.add(account1);

        return accounts;
    }

    public void initAccountCounter() {
        accountCounter = new AccountCounter(1);
        try {
            databaseHandler.getAccountCounterDao().create(accountCounter);
        } catch (SQLException e) {
            System.out.println("Account counter not initialized");
        }
    }

    public void initDatabase() {
        try {
            databaseHandler.getUserDao().create(getUsers());
        } catch (SQLException e) {
            System.out.println("Users not initialized");
        }

    }
}
