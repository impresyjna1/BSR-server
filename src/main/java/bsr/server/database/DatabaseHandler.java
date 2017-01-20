package bsr.server.database;
import bsr.server.models.AccountCounter;
import bsr.server.models.SessionCounter;
import bsr.server.models.User;
import bsr.server.properties.Config;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * Created by Impresyjna on 27.12.2016.
 */

/**
 * Singleton class to handle instance od Datastore to MongoDB
 */
public class DatabaseHandler {
    private static DatabaseHandler ourInstance = new DatabaseHandler();
    public static DatabaseHandler getInstance() {
        return ourInstance;
    }
    private Datastore mongoDataStore;

    private DatabaseHandler() {
    }

    /**
     * Method initializing connection to database on given address and port. Initialize objects if database empty
     */
    public void initDatabase() {
        final Morphia morphia = new Morphia();
        mongoDataStore = morphia.createDatastore(new MongoClient("localhost", Config.MONGODB_PORT), "bank");
        morphia.mapPackage("bsr.server.models");
        mongoDataStore.ensureIndexes();

        if(mongoDataStore.getCount(AccountCounter.class) == 0) {
            mongoDataStore.save(new AccountCounter());
        }
        if(mongoDataStore.getCount(SessionCounter.class) == 0) {
            mongoDataStore.save(new SessionCounter());
        }
        if(mongoDataStore.getCount(User.class) == 0) {
            new DatabaseConfig().initDatabase();
        }
        System.out.println("Opened bsr.server.database successfully");
    }

    public Datastore getMongoDataStore() {
        return mongoDataStore;
    }
}
