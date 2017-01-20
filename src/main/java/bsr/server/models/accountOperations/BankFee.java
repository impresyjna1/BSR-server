package bsr.server.models.accountOperations;

import bsr.server.exceptions.OperationException;
import bsr.server.models.Account;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Impresyjna on 08.01.2017.
 */

/**
 * Class to bank fee operation.
 */
@XmlRootElement(name = "bank_fee")
public class BankFee extends Operation{

    public BankFee() {
    }

    public BankFee(String title, int amount, String targetAccountNumber) {
        super("Ban fee", amount, targetAccountNumber);
    }

    /**
     * Method executes operation on given account
     * @param account Account to make operation
     * @throws OperationException Never thrown, bank always get what he want
     */
    @Override
    protected void execute(Account account) throws OperationException {
        account.setBalance(account.getBalance()-amount);
        this.balanceAfter = account.getBalance();
        account.addBankOperation(this);
    }
}
