package bsr.server.models.accountOperations;

import bsr.server.exceptions.OperationException;
import bsr.server.models.Account;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Impresyjna on 01.01.2017.
 */
@XmlRootElement(name = "deposit")
public class Deposit extends Operation {

    public Deposit() {
        super();
    }

    public Deposit(String title, int amount, String targetAccountNumber) {
        super(title, amount, targetAccountNumber);
    }

    protected void execute(Account account) throws OperationException {
        account.setBalance(account.getBalance() + amount);
        this.balanceAfter = account.getBalance();
        account.addBankOperation(this);
    }
}
