package bsr.server.models.accountOperations;

import bsr.server.exceptions.OperationException;
import bsr.server.models.Account;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Impresyjna on 01.01.2017.
 */
@DatabaseTable(tableName = "deposits")
public class Deposit extends Operation {

    public Deposit() {
        super();
        this.operationClassName = this.getClass().getName();
    }

    public Deposit(String title, int amount, String targetAccountNumber) {
        super(title, amount, targetAccountNumber);
        this.operationClassName = this.getClass().getName();
    }

    protected void execute(Account account) throws OperationException {
        account.setAccountAmount(account.getAccountAmount() + amount);
        this.balanceAfter = account.getAccountAmount();
        account.addBankOperation(this);
    }
}
