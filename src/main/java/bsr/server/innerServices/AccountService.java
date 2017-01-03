package bsr.server.innerServices;

import bsr.server.database.AuthSessionFromDatabaseUtil;
import bsr.server.database.DatabaseHandler;
import bsr.server.exceptions.*;
import bsr.server.models.Account;
import bsr.server.models.User;
import bsr.server.models.accountOperations.Deposit;
import bsr.server.models.accountOperations.Operation;
import com.j256.ormlite.stmt.PreparedQuery;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import java.sql.SQLException;
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

    DatabaseHandler databaseHandler = DatabaseHandler.getInstance();


    @WebMethod
    public List<Account> getAccounts() throws SessionException, UserException {
        User user = null;
        try {
            user = AuthSessionFromDatabaseUtil.getUserFromWebServiceContext(context);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (user != null) {
            final User finalUser = user;
            Map<String, Object> queryParams = new HashMap<String, Object>() {{
                put("owner_id", finalUser.getId());
            }};
            List<Account> accounts = null;
            try {
                accounts = databaseHandler.getAccountDao().queryForFieldValues(queryParams);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return accounts;
        }
        return null;
    }

    @WebMethod
    public Operation depositMoney(@WebParam(name = "title") @XmlElement(required = true) final String title,
                                  @WebParam(name = "amount") @XmlElement(required = true) final double amount,
                                  @WebParam(name = "targetAccountNumber") @XmlElement(required = true) final String targetAccountNumber) throws NotValidException, ServerException, AccountServiceException {
        Map<String, Object> parametersMap = new HashMap<String, Object>() {{
            put("title", title);
            put("amount", amount);
            put("receiver account no", targetAccountNumber);
        }};
        validateParams(parametersMap);

        User user = null;
        try {
            user = AuthSessionFromDatabaseUtil.getUserFromWebServiceContext(context);
        } catch (UserException | SessionException | SQLException e) {
            e.printStackTrace();
        }
        Account targetAccount = null;
        try {
            final User finalUser = user;
            Map<String, Object> accountParams = new HashMap<String, Object>() {{
                put("owner_id", finalUser.getId());
                put("accountNumber", targetAccountNumber);
            }};
            targetAccount = databaseHandler.getAccountDao().queryForFieldValues(accountParams).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AccountServiceException("Source bank account does not exist");
        }
        Deposit deposit = new Deposit(title, (int) (amount*100), targetAccountNumber);

        if(targetAccountNo.substring(2, 10).equals(ConstantsUtil.BANK_ID)) {
            BankAccount targetBankAccount = datastore.find(BankAccount.class).field("accountNo").equal(targetAccountNo).get();
            if(targetBankAccount == null) {
                throw new BankServiceException("Target bank account does not exist");
            }
            Transfer inTransfer = new Transfer(title, amount, sourceAccountNo, targetAccountNo, Transfer.TransferDirection.IN);
            makeInternalTransfer(datastore, sourceBankAccount, targetBankAccount, inTransfer, outTransfer);
        } else {
            makeExternalTransfer(datastore, sourceBankAccount, outTransfer);
        }

        return outTransfer;
    }

    private void validateParams(Map<String, Object> paramsMap) throws NotValidException {
        String exceptionMessage = "";
        for(Map.Entry<String, Object> param: paramsMap.entrySet()) {
            if(param.getValue() instanceof String) {
                String value = (String)param.getValue();
                if(value.length() == 0) {
                    exceptionMessage += param.getKey() + " ";
                }
            } else if(param.getKey() == "amount") {
                try {
                    int amount = Integer.parseInt(String.valueOf(param));
                    if(amount<=0) {
                        exceptionMessage += param.getKey() + " ";
                    }
                } catch (Exception e) {
                    exceptionMessage += param.getKey() + " ";
                }
            }
        }

        if(exceptionMessage.length() > 0) {
            exceptionMessage += " is missing or is invalid";
            throw new NotValidException(exceptionMessage);
        }
    }
}
