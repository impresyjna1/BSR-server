package bsr.server.innerServices;

import bsr.server.database.AuthSessionFromDatabaseUtil;
import bsr.server.database.DatabaseHandler;
import bsr.server.exceptions.SessionException;
import bsr.server.exceptions.UserException;
import bsr.server.models.Account;
import bsr.server.models.User;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
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
    public List<Account> getAccounts() throws SQLException, SessionException, UserException {
        final User user = AuthSessionFromDatabaseUtil.getUserFromWebServiceContext(context);
        Map<String, Object> queryParams = new HashMap<String, Object>() {{
            put("owner_id", user.getId());
        }};
        List<Account> accounts = databaseHandler.getAccountDao().queryForFieldValues(queryParams);
        for(Account account: accounts) {
            System.out.println(account.getAccountNumber());
        }
        return getAccounts();
    }
}
