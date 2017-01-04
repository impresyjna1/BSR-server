package bsr.server.models;

import bsr.server.database.DatabaseHandler;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.query.Query;

import javax.validation.constraints.NotNull;

/**
 * Created by Asia on 30.12.2016.
 */
@Entity("sessions")
public class Session {
    @Id
    private ObjectId id;
    @NotNull
    @Indexed(name = "sessionId", unique = true)
    private int sessionId;
    @Reference
    @NotNull
    private User user;
    @NotNull
    private String timestamp;

    public Session() {
    }

    public Session(User user) {
        this.user = user;
        this.timestamp = Long.toString(System.currentTimeMillis());
        AccountCounter query = DatabaseHandler.getInstance().getMongoDataStore().find(AccountCounter.class).get();
        System.out.println(query.getAccountNumber());
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
