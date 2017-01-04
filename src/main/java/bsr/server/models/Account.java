package bsr.server.models;

import bsr.server.database.DatabaseHandler;
import bsr.server.models.accountOperations.Operation;
import bsr.server.properties.Config;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.sun.xml.internal.xsom.impl.scd.Iterators;


import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Impresyjna on 27.12.2016.
 */
@XmlSeeAlso({Operation.class})
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
    @ForeignCollectionField(eager = false)
    ForeignCollection<Operation> operations;

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

    @XmlElementWrapper(name="operations")
    @XmlElementRef()
    public ForeignCollection<Operation> getOperations() {
        return operations;
    }

    public void setOperations(ForeignCollection<Operation> operations) {
        this.operations = operations;
    }

    public String generateAccountNo() {
        //TODO:
//        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
//        try {
//            AccountCounter accountCounter = databaseHandler.getAccountCounterDao().queryForFirst(databaseHandler.getAccountCounterDao().queryBuilder().prepare());
//            accountCounter.incrementNumber();
//            String accountNo = Config.BANK_ID + String.format("%016d", accountCounter.getAccountNumber());
//            String tmpNo = accountNo + "101100";
//            String part1 = tmpNo.substring(0, 15);
//            String part2 = tmpNo.substring(15);
//            long rest1 = Long.parseLong(part1)%97;
//            long rest2 = Long.parseLong(rest1 + part2)%97;
//            long checkSum = 98 - rest2;
//
//            accountNo = String.format("%02d", checkSum) + accountNo;
//            System.out.println(accountNo);
//            databaseHandler.getAccountCounterDao().update(accountCounter);
//            return accountNo;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        return "";
    }

    public void addBankOperation(Operation operation) {
        operations.add(operation);
    }
}
