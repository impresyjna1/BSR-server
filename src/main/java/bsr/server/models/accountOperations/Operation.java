package bsr.server.models.accountOperations;

import bsr.server.exceptions.OperationException;
import bsr.server.models.Account;
import org.mongodb.morphia.annotations.Embedded;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

/**
 * Created by Impresyjna on 01.01.2017.
 */

/**
 * Basic class for command pattern. Deposit, Transfer, Withdraw and BankFee implements this.
 */
@Embedded
@XmlSeeAlso({Deposit.class, Transfer.class, Withdraw.class, BankFee.class})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "operation")
public abstract class Operation {
    @NotNull
    protected String title;
    @NotNull
    protected int amount;
    protected int balanceAfter;
    @XmlElement(name = "target_account")
    @NotNull
    protected String targetAccountNumber;
    @XmlTransient
    protected boolean executed;

    public Operation() {
    }

    public Operation(String title, int amount, String targetAccountNumber) {
        this.title = title;
        this.amount = amount;
        this.targetAccountNumber = targetAccountNumber;
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

    public String getTargetAccountNumber() {
        return targetAccountNumber;
    }

    public void setTargetAccountNumber(String targetAccountNumber) {
        this.targetAccountNumber = targetAccountNumber;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
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

    protected abstract void execute(Account account) throws OperationException;
}
