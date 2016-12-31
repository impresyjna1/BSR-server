package models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Impresyjna on 27.12.2016.
 */
@DatabaseTable(tableName = "users")
public class User {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String surname;
    @DatabaseField
    private String clientNumber;
    @DatabaseField
    private String password;
    @ForeignCollectionField(eager = false)
    ForeignCollection<Account> accounts;

    public User() {
    }

    public User(String name, String surname, String clientNumber, String password) {
        this.name = name;
        this.surname = surname;
        this.clientNumber = clientNumber;
        this.password = hashPassword(password);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public ForeignCollection<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ForeignCollection<Account> accounts) {
        this.accounts = accounts;
    }

    public String getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(String userNumber) {
        this.clientNumber = clientNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String hashPassword(String password) {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(password.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        System.out.println(generatedPassword);
        return generatedPassword;
    }
}
