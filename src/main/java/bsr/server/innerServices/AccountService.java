package bsr.server.innerServices;

import bsr.server.database.AuthSessionFromDatabaseUtil;
import bsr.server.database.DatabaseHandler;
import bsr.server.exceptions.SessionException;
import bsr.server.exceptions.UserException;
import bsr.server.models.Account;
import bsr.server.models.User;
import bsr.server.models.accountOperations.Operation;

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
                                  @WebParam(name = "targetAccountNumber") @XmlElement(required = true) final String targetAccountNumber) {
        Map<String, Object> parametersMap = new HashMap<String, Object>() {{
            put("title", title);
            put("amount", amount);
            put("receiver account no", targetAccountNumber);
        }};
        ValidateParamsUtil.validate(parametersMap);

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        User user = AuthUtil.getUserFromWebServiceContext(context, datastore);

        BankAccount sourceBankAccount = datastore.find(BankAccount.class).field("accountNo").equal(sourceAccountNo).get();
        if(sourceBankAccount == null) {
            throw new BankServiceException("Source bank account does not exist");
        }
        if(!user.containsBankAccount(sourceAccountNo)) {
            throw new BankServiceException("Source account does not belong to user");
        }

        Transfer outTransfer = new Transfer(title, amount, sourceAccountNo, targetAccountNo, Transfer.TransferDirection.OUT);

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
}
