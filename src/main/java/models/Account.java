package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import database.DatabaseHandler;
import javafx.scene.chart.PieChart;
import properties.Config;

import java.sql.SQLException;

/**
 * Created by Impresyjna on 27.12.2016.
 */
@DatabaseTable(tableName = "accounts")
public class Account {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String accountNumber;
    @DatabaseField
    private int accountAmount;
    @DatabaseField
    private boolean open;
    @DatabaseField(canBeNull = false, foreign = true)
    private User owner;

    public Account() {
    }

    public Account(User owner) {
        this.accountNumber = generateAccountNo();
        this.accountAmount = 0;
        this.open = true;
        this.owner = owner;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAccountAmount() {
        return accountAmount;
    }

    public void setAccountAmount(int accountAmount) {
        this.accountAmount = accountAmount;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String generateAccountNo() {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        try {
            AccountCounter accountCounter = databaseHandler.getAccountCounterDao().queryForFirst(databaseHandler.getAccountCounterDao().queryBuilder().prepare());
            accountCounter.incrementNumber();
            String accountNo = Config.BANK_ID + String.format("%016d", accountCounter.getAccountNumber());
            String tmpNo = accountNo + "101100";
            String part1 = tmpNo.substring(0, 15);
            String part2 = tmpNo.substring(15);
            long rest1 = Long.parseLong(part1)%97;
            long rest2 = Long.parseLong(rest1 + part2)%97;
            long checkSum = 98 - rest2;

            accountNo = String.format("%02d", checkSum) + accountNo;
            System.out.println(accountNo);
            databaseHandler.getAccountCounterDao().update(accountCounter);
            return accountNo;
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        Query<Counter> query = datastore.find(Counter.class, "id", "accountNoCounter");
//        UpdateOperations<Counter> operation = datastore.createUpdateOperations(Counter.class).inc("seq");
//        long count = datastore.findAndModify(query, operation).getSeq();
//

//
//
        return "";
    }
}
