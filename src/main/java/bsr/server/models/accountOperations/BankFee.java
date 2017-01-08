package bsr.server.models.accountOperations;

import bsr.server.exceptions.OperationException;
import bsr.server.models.Account;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Impresyjna on 08.01.2017.
 */
@XmlRootElement(name = "bank_fee")
public class BankFee extends Operation{

    public BankFee() {
    }

    public BankFee(String title, int amount, String targetAccountNumber) {
        super("Ban fee", amount, targetAccountNumber);
    }

    @Override
    protected void execute(Account account) throws OperationException {
        account.setBalance(account.getBalance()-amount);
        this.balanceAfter = account.getBalance();
        account.addBankOperation(this);
    }
}
