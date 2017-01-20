package bsr.server.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import javax.validation.constraints.NotNull;

/**
 * Created by Impresyjna on 29.12.2016.
 */

/**
 * Model needed to create accounts counter and save that value in database. After creating each account value for only
 * one entity of this model incremented
 */
@Entity("accounts_counter")
public class AccountCounter {
    @Id
    private ObjectId id;
    @NotNull
    private int accountNumber = 0;

    public AccountCounter() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void incrementNumber() {
        accountNumber++;
    }
}

