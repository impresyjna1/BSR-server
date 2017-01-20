package bsr.server.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import javax.validation.constraints.NotNull;

/**
 * Created by Impresyjna on 04.01.2017.
 */

/**
 * Model of session counter needed to save counter of sessions in database
 * Incremented after creating new session for user after login
 */
@Entity("sessions_counter")
public class SessionCounter {
    @Id
    private ObjectId id;
    @NotNull
    private int sessionCounter = 0;

    public SessionCounter() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getSessionCounter() {
        return sessionCounter;
    }

    public void setSessionCounter(int sessionCounter) {
        this.sessionCounter = sessionCounter;
    }

    public void incrementId() {
        this.sessionCounter = this.sessionCounter + 1;
    }
}
