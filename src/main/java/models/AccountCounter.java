package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Impresyjna on 29.12.2016.
 */
@DatabaseTable(tableName = "accounts_counter")
public class AccountCounter {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private int accountNumber;

    public AccountCounter() {
    }

    public AccountCounter(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void incrementNumber() {
        accountNumber++;
    }
}
