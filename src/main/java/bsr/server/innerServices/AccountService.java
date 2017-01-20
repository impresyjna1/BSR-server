package bsr.server.innerServices;

import bsr.server.database.AuthSessionFromDatabaseUtil;
import bsr.server.database.DatabaseHandler;
import bsr.server.exceptions.*;
import bsr.server.models.Account;
import bsr.server.models.User;
import bsr.server.models.accountOperations.*;
import bsr.server.outerServices.SendTransferToOtherBank;
import bsr.server.properties.BanksMap;
import bsr.server.properties.Config;
import bsr.server.utils.AccountNumberAuthUtil;
import org.mongodb.morphia.Datastore;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Impresyjna on 01.01.2017.
 */

/**
 * Webservice to handle account operations for interal bank use
 */
@WebService
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class AccountService {
    @Resource
    private WebServiceContext context;

    Datastore mongoDataStore = DatabaseHandler.getInstance().getMongoDataStore();


    /**
     * Web Method to get list of accounts for user connected with sessionId in webService context
     * @return List of accounts from database
     * @throws SessionException Exception when there is no user for sessionId from context
     * @throws UserException Exception thrown when user assigned to session not exists anymore
     */
    @WebMethod
    public List<Account> getAccounts() throws SessionException, UserException {
        User user = AuthSessionFromDatabaseUtil.getUserFromWebServiceContext(context);
        if (user != null) {
            return user.getAccounts();
        } else {
            throw new SessionException("No user for this session");
        }
    }

    /**
     * Web Method to deposit money on account
     * @param title Title of deposit if user wants to have his own titles
     * @param amount Amount in pennies
     * @param targetAccountNumber Account number to target account
     * @return Operation object if created and succeeded
     * @throws NotValidException Exception if params not valid
     * @throws SessionException Exception thrown when user is not logged in or session has expired
     * @throws UserException Exception thrown when user assigned to session not exists anymore
     * @throws OperationException In this case never thrown because deposit with valid data always succeeded
     * @throws AccountServiceException Exception thrown if targetAccount doesn't exists
     * @throws TaxInspectorException Exception when amount is way bigger than allowed
     */
    @WebMethod
    public Operation depositMoney(@WebParam(name = "title") @XmlElement(required = true) final String title,
                                  @WebParam(name = "amount") @XmlElement(required = true) final String amount,
                                  @WebParam(name = "targetAccountNumber") @XmlElement(required = true) final String targetAccountNumber) throws NotValidException, SessionException, UserException, OperationException, AccountServiceException, TaxInspectorException {
        Map<String, Object> parametersMap = new HashMap<String, Object>() {{
            put("title", title);
            put("amount", amount);
            put("targetAccountNumber", targetAccountNumber);
        }};
        validateParams(parametersMap);

        User user = AuthSessionFromDatabaseUtil.getUserFromWebServiceContext(context);
        Account targetAccount = mongoDataStore.find(Account.class)
                .field("accountNumber")
                .equal(targetAccountNumber)
                .get();

        if(targetAccount == null) {
            throw new AccountServiceException("Account not found");
        }

        Deposit deposit = new Deposit(title, parseIntFromString(amount), targetAccountNumber);
        deposit.doOperation(targetAccount);
        mongoDataStore.save(targetAccount);
        return deposit;
    }

    /**
     * Web Method to withdraw money from account
     * @param title Title of deposit if user wants to have his own titles
     * @param amount Amount in pennies
     * @param targetAccountNumber Account number to target account
     * @return Operation object if created and succeeded
     * @throws NotValidException Exception if params not valid
     * @throws SessionException Exception thrown when user is not logged in or session has expired
     * @throws UserException Exception thrown when user assigned to session not exists anymore
     * @throws OperationException Exception when amount is more than balance on account
     * @throws AccountServiceException Exception thrown if targetAccount doesn't exists
     * @throws TaxInspectorException Exception when amount is way bigger than allowed
     */
    @WebMethod
    public Operation withdrawMoney(@WebParam(name = "title") @XmlElement(required = true) final String title,
                                   @WebParam(name = "amount") @XmlElement(required = true) final String amount,
                                   @WebParam(name = "targetAccountNumber") @XmlElement(required = true) final String targetAccountNumber) throws NotValidException, SessionException, UserException, OperationException, AccountServiceException, TaxInspectorException {
        Map<String, Object> parametersMap = new HashMap<String, Object>() {{
            put("title", title);
            put("amount", amount);
            put("targetAccountNumber", targetAccountNumber);
        }};
        validateParams(parametersMap);

        User user = AuthSessionFromDatabaseUtil.getUserFromWebServiceContext(context);
        Account targetAccount = mongoDataStore.find(Account.class)
                .field("accountNumber")
                .equal(targetAccountNumber)
                .get();

        if(targetAccount == null) {
            throw new AccountServiceException("Account not found");
        }

        Withdraw withdraw = new Withdraw(title, parseIntFromString(amount), targetAccountNumber);
        withdraw.doOperation(targetAccount);
        mongoDataStore.save(targetAccount);
        return withdraw;
    }

    /**
     * Web method to get bank fee from account
     * @param targetAccountNumber Account to charge money
     * @return Operation if succeed
     * @throws NotValidException Exception if params not valid
     * @throws SessionException Exception thrown when user is not logged in or session has expired
     * @throws UserException Exception thrown when user assigned to session not exists anymore
     * @throws OperationException Never thrown in this case. Bank always get what he wants.
     * @throws AccountServiceException Exception if targetAccount doesn't exists
     * @throws AccountChecksumException Exception if checksum of account not valid
     * @throws TaxInspectorException Never thrown in this case
     */
    @WebMethod
    public Operation getBankFeeFromAccount(@WebParam(name = "targetAccountNumber") @XmlElement(required = true) final String targetAccountNumber) throws NotValidException, SessionException, UserException, OperationException, AccountServiceException, AccountChecksumException, TaxInspectorException {
        Map<String, Object> parametersMap = new HashMap<String, Object>() {{
            put("targetAccountNumber", targetAccountNumber);
        }};
        validateParams(parametersMap);

        if(!AccountNumberAuthUtil.checkChecksum(targetAccountNumber)) {
            throw new AccountChecksumException("Checksum not valid");
        }

        User user = AuthSessionFromDatabaseUtil.getUserFromWebServiceContext(context);
        Account targetAccount = mongoDataStore.find(Account.class)
                .field("accountNumber")
                .equal(targetAccountNumber)
                .get();

        if (targetAccount == null) {
            throw new AccountServiceException("There is no account with this number");
        }

        BankFee bankFee = new BankFee("", targetAccount.getFeeCount(), targetAccountNumber);
        bankFee.doOperation(targetAccount);
        mongoDataStore.save(targetAccount);
        return bankFee;
    }


    /**
     * Web method to transfer money
     * @param title Title of transfer
     * @param amount Amount of transfer in string, needed to be parsed to int
     * @param sourceAccountNumber Account from we want transfer
     * @param targetAccountNumber Account to we want to transfer
     * @return Operation object if success
     * @throws NotValidException Exception if params not valid
     * @throws SessionException Exception thrown when user is not logged in or session has expired
     * @throws UserException Exception thrown when user assigned to session not exists anymore
     * @throws AccountServiceException Exception thrown when transfer and source account are the same, target bank doesn't exists if ELIXIR transfer
     * @throws OperationException Exception thrown if operation succeed
     * @throws AccountException Exception thrown if source account doesn't exists and if internal transfer if targetAccount doesn't exists
     * @throws IOException
     * @throws TaxInspectorException Exception when amount to transfer is way bigger than allowed
     */
    @WebMethod
    public Operation transferMoney(@WebParam(name = "title") @XmlElement(required = true) final String title,
                                   @WebParam(name = "amount") @XmlElement(required = true) final String amount,
                                   @WebParam(name = "sourceAccountNumber") @XmlElement(required = true) final String sourceAccountNumber,
                                   @WebParam(name = "targetAccountNumber") @XmlElement(required = true) final String targetAccountNumber) throws NotValidException, SessionException, UserException, AccountServiceException, OperationException, AccountException, IOException, TaxInspectorException {
        System.out.println(title);
        System.out.println(amount);
        Map<String, Object> parametersMap = new HashMap<String, Object>() {{
            put("targetAccountNumber", targetAccountNumber);
            put("sourceAccountNumber", sourceAccountNumber);
            put("amount", amount);
            put("title", title);
        }};
        validateParams(parametersMap);

        if (sourceAccountNumber.equals(targetAccountNumber)) {
            throw new AccountServiceException("Can not transfer for same account");
        }

        User user = AuthSessionFromDatabaseUtil.getUserFromWebServiceContext(context);

        Account sourceAccount = mongoDataStore.find(Account.class)
                .field("accountNumber")
                .equal(sourceAccountNumber)
                .get();

        Account targetAccount = mongoDataStore.find(Account.class)
                .field("accountNumber")
                .equal(targetAccountNumber)
                .get();

        if (sourceAccount == null) {
            throw new AccountException("Source account doesn't exists");
        }

        Transfer fromSourceAccountTransfer = new Transfer(title, parseIntFromString(amount), targetAccountNumber, Transfer.TransferEnum.OUT, sourceAccountNumber);
        if (targetAccount != null) {
            Transfer toTargetAccountTransfer = new Transfer(title, parseIntFromString(amount), targetAccountNumber, Transfer.TransferEnum.IN, sourceAccountNumber);
            innerTransfer(sourceAccount, targetAccount, fromSourceAccountTransfer, toTargetAccountTransfer);
        } else if (targetAccount == null && targetAccountNumber.substring(2,10).equals(Config.BANK_ID)){
            throw new AccountException("Account doesn't exists");
        } else {
            outerTransfer(targetAccountNumber, sourceAccount, fromSourceAccountTransfer);
        }
        return fromSourceAccountTransfer;
    }

    /**
     * Method called when transfer is not internal
     * @param targetAccountNumber Account number to transfer
     * @param sourceAccount Account number from transfer is made
     * @param fromSourceAccountTransfer Transfer operation for sourceAccount
     * @throws AccountServiceException Exception when unknown target bank
     * @throws OperationException Exception when operation for source account doesn't succeeded
     * @throws IOException
     */
    private void outerTransfer(String targetAccountNumber, Account sourceAccount, Transfer fromSourceAccountTransfer) throws AccountServiceException, OperationException, IOException {
        String bankIdentifier = targetAccountNumber.substring(2,10);
        Map<String, String> banksMap = BanksMap.getInstance().getBankIpMap();

        if(!banksMap.containsKey(bankIdentifier)) {
            throw new AccountServiceException("Unknown target bank");
        }

        fromSourceAccountTransfer.doOperation(sourceAccount);

        boolean requestSuccess = new SendTransferToOtherBank().makeRequest(banksMap.get(bankIdentifier), targetAccountNumber, sourceAccount.getAccountNumber(), fromSourceAccountTransfer.getTitle(), fromSourceAccountTransfer.getAmount());

        if(requestSuccess) {
            mongoDataStore.save(sourceAccount);
        }
    }

    /**
     * Method called when internal transfer to made
     * @param sourceAccount Source account number
     * @param targetAccount Target account number
     * @param fromSourceTransfer Source account transfer operation object
     * @param toTargetTransfer Target account transfer operation object
     * @throws OperationException Exception thrown when operation on source or target account doesn't succeed. Always looking for exception from source account
     * @throws AccountException Exception when source or target account is closed so transfer cannot be made
     */
    private void innerTransfer(Account sourceAccount, Account targetAccount, Transfer fromSourceTransfer, Transfer toTargetTransfer) throws OperationException, AccountException {
        if(!sourceAccount.isOpen()) {
            throw new AccountException("Source account is closed");
        }
        if(!targetAccount.isOpen()) {
            throw new AccountException("Target account is closed");
        }

        fromSourceTransfer.doOperation(sourceAccount);
        toTargetTransfer.doOperation(targetAccount);

        mongoDataStore.save(sourceAccount);
        mongoDataStore.save(targetAccount);
    }

    /**
     * Validation for params
     * @param paramsMap
     * @throws NotValidException Throw when params not valid
     * @throws TaxInspectorException Throw when amount to big
     */
    private void validateParams(Map<String, Object> paramsMap) throws NotValidException, TaxInspectorException {
        String exceptionMessage = "";
        for (Map.Entry<String, Object> param : paramsMap.entrySet()) {
            if (param.getKey().contains("amount")) {
                try {
                    String amountString = (String) param.getValue();
                    amountString = amountString.replace(",", ".");
                    System.out.println(amountString);
                    String[] parts = amountString.split(Pattern.quote("."));
                    System.out.println(parts[1]);
                    double amount = Double.parseDouble(String.valueOf(amountString));
                    if (amount <= 0 || parts[1].length()>2) {
                        exceptionMessage += param.getKey() + " ";
                    } else if (amount > 100000000) {
                        throw new TaxInspectorException("Tax inspector will contact you if you use that amount");
                    }
                } catch (Exception e) {
                    System.out.println("Exception");
                    exceptionMessage += param.getKey() + " ";
                }
            } else if (param.getKey().contains("AccountNumber")) {
                String value = (String) param.getValue();
                if (value.length() != 26 || !value.matches("\\d+")) {
                    exceptionMessage += param.getKey() + " ";
                }
            } else if (param.getValue() instanceof String) {
                String value = (String) param.getValue();
                value = value.replace("\\s", "");
                if (value.length() == 0) {
                    exceptionMessage += param.getKey() + " ";
                }
            }
        }
        if (exceptionMessage.length() > 0) {
            exceptionMessage += " is missing or is invalid";
            throw new NotValidException(exceptionMessage);
        }
    }

    private int parseIntFromString(String amount) {
        String amountString = amount.replace(",", ".");
        double amountDouble = Double.parseDouble(String.valueOf(amountString));

        return (int) (amountDouble*100);
    }
}
