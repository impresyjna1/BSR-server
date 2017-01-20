package bsr.server.innerServices;

import bsr.server.database.AuthSessionFromDatabaseUtil;
import bsr.server.database.DatabaseHandler;
import bsr.server.exceptions.*;
import bsr.server.models.Session;
import bsr.server.models.User;
import org.mongodb.morphia.Datastore;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Impresyjna on 27.12.2016.
 */

/**
 * WebService for user operations for internal bank use
 */
@WebService
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class UserService {
    @Resource
    private WebServiceContext context;

    Datastore mongoDataStore = DatabaseHandler.getInstance().getMongoDataStore();

    /**
     * Login service
     * @param clientNumber Client number e.g 111111
     * @param password Client password to auth if checks with client number
     * @return sessionId given by server
     * @throws AuthException Exception when user with given client number and client password doesn't exists
     * @throws NotValidException Exception when client number or password invalid
     */
    @WebMethod
    public int login(@WebParam(name = "clientNumber") @XmlElement(required = true) final String clientNumber,
                        @WebParam(name = "password") @XmlElement(required = true) final String password)
            throws AuthException, NotValidException {
        Map<String, Object> userParamsMap = new HashMap<String, Object>() {{
            put("clientNumber", clientNumber);
            put("password", password);
        }};
        validateParams(userParamsMap);

        User user = mongoDataStore.find(User.class)
                .field("clientNumber").equal(clientNumber)
                .field("password").equal(password)
                .get();
        if(user == null) {
            throw new AuthException("Bad auth");
        } else {
            Session session = new Session(user);
            mongoDataStore.save(session);
            return session.getSessionId();
        }
    }

    /**
     * Method to get user for sessionId in web context
     * @return User from database connected with sessionId
     * @throws SessionException Exception thrown when user is not logged in or session has expired
     * @throws UserException Exception thrown when user assigned to session not exists anymore
     */
    @WebMethod
    public User getUser() throws SessionException, UserException {
        User user = AuthSessionFromDatabaseUtil.getUserFromWebServiceContext(context);

        return user;
    }

    /**
     * Method validating given in map params
     * @param paramsMap Params in map, key is name of param, value is the value of param
     * @throws NotValidException When one param not valid throw exception with message
     */
    private void validateParams(Map<String, Object> paramsMap) throws NotValidException {
        String exceptionMessage = "";
        for(Map.Entry<String, Object> param: paramsMap.entrySet()) {
            if(param.getValue() instanceof String) {
                String value = (String)param.getValue();

                if(value.length() == 0 || !value.matches(".*\\w.*")) {
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
