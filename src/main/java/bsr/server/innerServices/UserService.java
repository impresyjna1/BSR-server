package bsr.server.innerServices;

import bsr.server.database.DatabaseHandler;
import bsr.server.exceptions.AuthException;
import bsr.server.exceptions.NotValidException;
import bsr.server.exceptions.ServerException;
import bsr.server.models.Session;
import bsr.server.models.User;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.BindingType;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Impresyjna on 27.12.2016.
 */
@WebService
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class UserService {

    DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
    @WebMethod
    public int login(@WebParam(name = "clientNumber") @XmlElement(required = true) final String clientNumber,
                        @WebParam(name = "password") @XmlElement(required = true) final String password)
            throws AuthException, NotValidException, ServerException {
        Map<String, Object> userParamsMap = new HashMap<String, Object>() {{
            put("clientNumber", clientNumber);
            put("password", password);
        }};
        validateParams(userParamsMap);

        User user = null;
        try {
            List<User> users = databaseHandler.getUserDao().queryForFieldValues(userParamsMap);
            if (users.size() > 0) {
                user = users.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(user == null) {
            throw new AuthException("Bad auth");
        } else {
            Session session = new Session(user);
            try {
                databaseHandler.getSessionDao().create(session);
            } catch (SQLException e) {
                throw new ServerException("Can not init session");
            }
            return session.getId();
        }
    }

    public void getUser() {
        // TODO: To get data about user to show in client
    }

    private void validateParams(Map<String, Object> paramsMap) throws NotValidException {
        String exceptionMessage = "";
        for(Map.Entry<String, Object> param: paramsMap.entrySet()) {
            if(param.getValue() instanceof String) {
                String value = (String)param.getValue();
                if(value.length() == 0) {
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
