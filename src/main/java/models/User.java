package models;

/**
 * Created by Impresyjna on 27.12.2016.
 */
public class User {
    private String name;
    private String surname;
    private Account account;

    public User(String name, String surname, Account account) {
        this.name = name;
        this.surname = surname;
        this.account = account;
    }
}
