package bsr.server.models.accountOperations;

import bsr.server.exceptions.OperationException;
import bsr.server.models.Account;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

/**
 * Created by Impresyjna on 08.01.2017.
 */
@XmlRootElement(name = "transfer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Transfer extends Operation{
    public enum TransferEnum {
        IN, OUT
    }

    @XmlTransient
    private TransferEnum transferType;
    @XmlElement (name = "from")
    @NotNull
    private String sourceAccountNumber;

    public Transfer() {

    }

    public Transfer(String title, int amount, String targetAccountNumber, TransferEnum transferType, String sourceAccountNumber) {
        super(title, amount, targetAccountNumber);
        this.transferType = transferType;
        this.sourceAccountNumber = sourceAccountNumber;
    }

    public TransferEnum getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferEnum transferType) {
        this.transferType = transferType;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    @Override
    protected void execute(Account account) throws OperationException {
        if(transferType == TransferEnum.IN) {
            account.setBalance(account.getBalance()+amount);
        } else {
            if (amount > account.getBalance()) {
                throw new OperationException("Not enough money");
            }
            account.setBalance(account.getBalance()-amount);
        }
        this.balanceAfter = account.getBalance();
        account.addBankOperation(this);
    }
}
