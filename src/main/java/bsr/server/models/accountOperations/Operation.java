package bsr.server.models.accountOperations;

import bsr.server.exceptions.OperationException;
import bsr.server.models.Account;
import bsr.server.models.User;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Impresyjna on 01.01.2017.
 */
@DatabaseTable(tableName = "operations")
public abstract class Operation {
    @DatabaseField(generatedId = true)
    protected int id;
    @DatabaseField(canBeNull = false)
    protected String title;
    @DatabaseField(canBeNull = false)
    protected int amount;
    @DatabaseField
    protected int balanceAfter;
    @DatabaseField
    protected boolean executed = false;
    @DatabaseField(canBeNull = false)
    protected String targetAccountNumber;
    @DatabaseField(canBeNull = false)
    protected String operationClassName;
    @DatabaseField(canBeNull = false, foreign = true)
    private Account operationAccount;

    public Operation() {
    }

    public Operation(String title, int amount, String targetAccountNumber) {
        this.title = title;
        this.amount = amount;
        this.targetAccountNumber = targetAccountNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(int balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public String getTargetAccountNumber() {
        return targetAccountNumber;
    }

    public void setTargetAccountNumber(String targetAccountNumber) {
        this.targetAccountNumber = targetAccountNumber;
    }

    public String getOperationClassName() {
        return operationClassName;
    }

    public void setOperationClassName(String operationClassName) {
        this.operationClassName = operationClassName;
    }

    public void doOperation(Account account) throws OperationException {
        if(executed) {
            throw new OperationException("Already executed");
        }
        if(amount < 0) {
            throw new OperationException("Negative amount");
        }

        execute(account);
        executed = true;
    }

    public Account getOperationAccount() {
        return operationAccount;
    }

    public void setOperationAccount(Account operationAccount) {
        this.operationAccount = operationAccount;
    }

    protected abstract void execute(Account account) throws OperationException;
}
