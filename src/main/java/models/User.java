package models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Impresyjna on 27.12.2016.
 */
@DatabaseTable(tableName = "users")
public class User {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String surname;
    @ForeignCollectionField(eager = false)
    ForeignCollection<Account> accounts;

    public User(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public ForeignCollection<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ForeignCollection<Account> accounts) {
        this.accounts = accounts;
    }
}
