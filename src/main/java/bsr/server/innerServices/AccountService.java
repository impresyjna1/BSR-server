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

/**
 * Created by Impresyjna on 01.01.2017.
 */
@WebService
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class AccountService {
    @Resource
    private WebServiceContext context;

    Datastore mongoDataStore = DatabaseHandler.getInstance().getMongoDataStore();


    @WebMethod
    public List<Account> getAccounts() throws SessionException, UserException {
        User user = AuthSessionFromDatabaseUtil.getUserFromWebServiceContext(context);
        if (user != null) {
            return user.getAccounts();
        } else {
            throw new SessionException("No user for this session");
        }
    }

    @WebMethod
    public Operation depositMoney(@WebParam(name = "title") @XmlElement(required = true) final String title,
                                  @WebParam(name = "amount") @XmlElement(required = true) final String amount,
                                  @WebParam(name = "targetAccountNumber") @XmlElement(required = true) final String targetAccountNumber) throws NotValidException, SessionException, UserException, OperationException, AccountException, AccountServiceException {
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

    @WebMethod
    public Operation withdrawMoney(@WebParam(name = "title") @XmlElement(required = true) final String title,
                                   @WebParam(name = "amount") @XmlElement(required = true) final String amount,
                                   @WebParam(name = "targetAccountNumber") @XmlElement(required = true) final String targetAccountNumber) throws NotValidException, SessionException, UserException, OperationException, AccountException, AccountServiceException {
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

    @WebMethod
    public Operation getBankFeeFromAccount(@WebParam(name = "targetAccountNumber") @XmlElement(required = true) final String targetAccountNumber) throws NotValidException, SessionException, UserException, OperationException, AccountServiceException, AccountChecksumException {
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

    @WebMethod
    public Operation transferMoney(@WebParam(name = "title") @XmlElement(required = true) final String title,
                                   @WebParam(name = "amount") @XmlElement(required = true) final String amount,
                                   @WebParam(name = "sourceAccountNumber") @XmlElement(required = true) final String sourceAccountNumber,
                                   @WebParam(name = "targetAccountNumber") @XmlElement(required = true) final String targetAccountNumber) throws NotValidException, SessionException, UserException, AccountServiceException, OperationException, AccountException, IOException {
        Map<String, Object> parametersMap = new HashMap<String, Object>() {{
            put("targetAccountNumber", targetAccountNumber);
            put("sourceAccountNumber", sourceAccountNumber);
            put("amount", amount);
            put("title", title);
        }};
        validateParams(parametersMap);

        if (!sourceAccountNumber.equals(targetAccountNumber)) {
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

    private void validateParams(Map<String, Object> paramsMap) throws NotValidException {
        String exceptionMessage = "";
        for (Map.Entry<String, Object> param : paramsMap.entrySet()) {
            if (param.getKey().contains("amount")) {
                try {
                    String amountString = (String) param.getValue();
                    amountString = amountString.replace(",", ".");
                    double amount = Double.parseDouble(String.valueOf(amountString));
                    if (amount <= 0) {
                        exceptionMessage += param.getKey() + " ";
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
