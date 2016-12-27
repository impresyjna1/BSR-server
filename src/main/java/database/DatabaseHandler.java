package database;
import java.sql.*;

/**
 * Created by Impresyjna on 27.12.2016.
 */
public class DatabaseHandler {
    private static DatabaseHandler ourInstance = new DatabaseHandler();
    Connection c = null;
    public static DatabaseHandler getInstance() {
        return ourInstance;
    }

    private DatabaseHandler() {
    }

    public void initDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:bank.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
}
