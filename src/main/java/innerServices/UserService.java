package innerServices;

import database.DatabaseHandler;
import exceptions.AuthException;
import exceptions.NotValidException;
import exceptions.ServerException;
import models.Session;
import models.User;

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
            user = databaseHandler.getUserDao().queryForFieldValues(userParamsMap).get(0);
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

    public void getAllUsers() {

    }

    public void getUser() {

    }

    public void createUser() {

    }

    public void updateUser() {

    }

    public void deleteUser() {

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
