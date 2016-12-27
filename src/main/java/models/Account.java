package models;

/**
 * Created by Impresyjna on 27.12.2016.
 */
public class Account {
    private String accountNumber;
    private int accountAmount;
    private boolean open;
    private User owner;

    public Account() {
    }

    public Account(String accountNumber, int accountAmount, boolean open, User owner) {
        this.accountNumber = accountNumber;
        this.accountAmount = accountAmount;
        this.open = open;
        this.owner = owner;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAccountAmount() {
        return accountAmount;
    }

    public void setAccountAmount(int accountAmount) {
        this.accountAmount = accountAmount;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
