package bsr.server.database;
import bsr.server.models.AccountCounter;
import bsr.server.models.Session;
import bsr.server.models.SessionCounter;
import bsr.server.models.User;
import bsr.server.properties.BanksMap;
import bsr.server.properties.Config;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Impresyjna on 27.12.2016.
 */
public class DatabaseHandler {
    private static DatabaseHandler ourInstance = new DatabaseHandler();
    public static DatabaseHandler getInstance() {
        return ourInstance;
    }
    private Datastore mongoDataStore;

    private DatabaseHandler() {
    }

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
        Map<String, String> banksMap = BanksMap.getInstance().getBankIpMap();
        Iterator it = banksMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public Datastore getMongoDataStore() {
        return mongoDataStore;
    }
}
