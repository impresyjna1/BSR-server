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
}
