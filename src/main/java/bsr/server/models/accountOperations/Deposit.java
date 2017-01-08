package bsr.server.models.accountOperations;

import bsr.server.exceptions.OperationException;
import bsr.server.models.Account;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Impresyjna on 01.01.2017.
 */
@XmlRootElement(name = "deposit")
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
        account.setBalance(account.getBalance() + amount);
        this.balanceAfter = account.getBalance();
        this.account = account;
        //TODO:
//        try {
//            DatabaseHandler.getInstance().getOperationDao().create(this);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
}
