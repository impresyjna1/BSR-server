package bsr.server.models.accountOperations;

import bsr.server.exceptions.OperationException;
import bsr.server.models.Account;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Impresyjna on 08.01.2017.
 */
@XmlRootElement(name = "withdraw")
public class Withdraw extends Operation {

    public Withdraw() {
    }

    public Withdraw(String title, int amount, String targetAccountNumber) {
        super(title, amount, targetAccountNumber);
    }

    @Override
    protected void execute(Account account) throws OperationException {

        if(amount>account.getBalance()) {
            throw new OperationException("Not enough money");
        } else {
            account.setBalance(account.getBalance()-amount);
            this.balanceAfter = account.getBalance();
            account.addBankOperation(this);
        }
    }
}
