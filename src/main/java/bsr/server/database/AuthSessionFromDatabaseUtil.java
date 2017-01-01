package bsr.server.database;

import bsr.server.exceptions.AuthException;
import bsr.server.exceptions.SessionException;
import bsr.server.exceptions.UserException;
import bsr.server.models.Session;
import bsr.server.models.User;

import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Impresyjna on 01.01.2017.
 */
public abstract class AuthSessionFromDatabaseUtil {
    private static DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
    public static String getSessionIdFromWebServiceContext(WebServiceContext context) throws SessionException {
        Map headers = (Map)context.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS);
        ArrayList sessionId = (ArrayList)headers.get("sessionId");
        if(sessionId == null) {
            throw new SessionException("Session id is missing");
        }

        return (String)sessionId.get(0);
    }

    public static Session getSessionFromWebServiceContext(WebServiceContext context) throws SessionException, SQLException {
        String sessionId = getSessionIdFromWebServiceContext(context);
        Session session = databaseHandler.getSessionDao().queryForId(sessionId);
        if(session == null) {
            throw new SessionException("User is not logged in or session has expired");
        }

        return session;
    }

    public static User getUserFromWebServiceContext(WebServiceContext context) throws UserException, SessionException, SQLException {
        Session session = getSessionFromWebServiceContext(context);
        User user = session.getUser();
        if(user == null) {
            databaseHandler.getSessionDao().delete(session);
            throw new UserException("User assigned to this session not exists");
        }

        return user;
    }
}
