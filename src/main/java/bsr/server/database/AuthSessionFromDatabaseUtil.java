package bsr.server.database;

import bsr.server.exceptions.SessionException;
import bsr.server.exceptions.UserException;
import bsr.server.models.Session;
import bsr.server.models.User;

import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Impresyjna on 01.01.2017.
 */

/**
 * Abstract class with methods to get user for auth session
 */
public abstract class AuthSessionFromDatabaseUtil {

    /**
     * Method to get sessionId from WebService context
     * @param context WebServicr context for request. Contains sessionId
     * @return Got from context sessionId
     * @throws SessionException Exception thrown when sessionId is not in context
     */
    public static String getSessionIdFromWebServiceContext(WebServiceContext context) throws SessionException {
        Map headers = (Map) context.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS);
        ArrayList sessionId = (ArrayList) headers.get("sessionId");
        if (sessionId == null) {
            throw new SessionException("Session id is missing");
        }

        return (String) sessionId.get(0);
    }

    /**
     * Method to get session object from database on the grounds of WebServiceContext
     * @param context WebServicr context for request. Contains sessionId
     * @return Got from database session object
     * @throws SessionException Exception thrown when user is not logged in or session has expired
     */
    public static Session getSessionFromWebServiceContext(WebServiceContext context) throws SessionException {
        String sessionId = getSessionIdFromWebServiceContext(context);
        Session session = DatabaseHandler.getInstance().getMongoDataStore()
                .find(Session.class)
                .field("sessionId")
                .equal(Integer.parseInt(sessionId)).get();
        if (session == null) {
            throw new SessionException("User is not logged in or session has expired");
        }
        return session;
    }

    /**
     * Method to get user from database on the grounds of WebServiceContext
     * @param context WebServicr context for request. Contains sessionId
     * @return Got from database user object
     * @throws UserException Exception thrown when user assigned to session not exists anymore
     * @throws SessionException Exception thrown when user is not logged in or session has expired or sessionId is not in context
     */
    public static User getUserFromWebServiceContext(WebServiceContext context) throws UserException, SessionException {
        Session session = getSessionFromWebServiceContext(context);
        User user = session.getUser();
        if (user == null) {
            DatabaseHandler.getInstance().getMongoDataStore().delete(session);
            throw new UserException("User assigned to this session not exists");
        }

        return user;
    }
}
