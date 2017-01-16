package bsr.server.models;

import bsr.server.database.DatabaseHandler;
import bsr.server.models.accountOperations.Operation;
import bsr.server.properties.Config;
import bsr.server.utils.AccountNumberAuthUtil;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Impresyjna on 27.12.2016.
 */
@XmlSeeAlso({Operation.class})
@Entity("accounts")
public class Account {
    @Id
    @XmlTransient
    private ObjectId id;
    @NotNull
    @Indexed(name = "accountNumber", unique = true)
    private String accountNumber;
    @NotNull
    private int balance;
    @NotNull
    private String titleOFAccount;
    @NotNull
    private boolean open;
    @NotNull
    private int feeCount;
    @Embedded
    private List<Operation> operations;

    public Account() {
        if (operations == null) {
            operations = new ArrayList<>();
        }
    }

    public Account(String titleOFAccount, int feeCount) {
        this.accountNumber = generateAccountNo();
        this.balance = 0;
        this.open = true;
        this.operations = new ArrayList<>();
        this.titleOFAccount = titleOFAccount;
        this.feeCount = feeCount;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getTitleOFAccount() {
        return titleOFAccount;
    }

    public void setTitleOFAccount(String titleOFAccount) {
        this.titleOFAccount = titleOFAccount;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getFeeCount() {
        return feeCount;
    }

    public void setFeeCount(int feeCount) {
        this.feeCount = feeCount;
    }

    @XmlElementWrapper(name = "operations")
    @XmlElementRef()
    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public String generateAccountNo() {
        Datastore mongoDataStore = DatabaseHandler.getInstance().getMongoDataStore();
        AccountCounter accountCounter = mongoDataStore.find(AccountCounter.class).get();
        accountCounter.incrementNumber();

        String accountNo = Config.BANK_ID + String.format("%016d", accountCounter.getAccountNumber());
//        String tmpNo = accountNo + "101100";
//        String part1 = tmpNo.substring(0, 15);
//        String part2 = tmpNo.substring(15);
//        long rest1 = Long.parseLong(part1) % 97;
//        long rest2 = Long.parseLong(rest1 + part2) % 97;
//        long checkSum = 98 - rest2;
//
//        accountNo = String.format("%02d", checkSum) + accountNo;
        System.out.println(accountNo);
        accountNo = AccountNumberAuthUtil.calculateChecksum(accountNo);
        System.out.println(accountNo);


        Query<AccountCounter> query = mongoDataStore.createQuery(AccountCounter.class).field("id").equal(accountCounter.getId());
        UpdateOperations<AccountCounter> ops = mongoDataStore.createUpdateOperations(AccountCounter.class).set("accountNumber", accountCounter.getAccountNumber());
        mongoDataStore.update(query, ops);

        return accountNo;

    }

    public void addBankOperation(Operation operation) {
        operations.add(operation);
    }
}
