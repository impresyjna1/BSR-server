package bsr.server.properties;

/**
 * Created by Impresyjna on 27.12.2016.
 */

/**
 * Class with configuration
 * Contains address for service, bank id, password for basic auth, ports for services and path to file with addresses and bank ids
 */
public abstract class Config {
    public static final String BANK_ID = "00109708";
    public static final String SERVER_ADDR = "localhost";
    public static final int SERVER_PORT_SOAP = 8080;
    public static final int SERVER_PORT_REST = 8079;
    public static final int MONGODB_PORT = 8078;
    public static final String BANK_TO_IP_FILE_PATH = "banksIpList.txt";
    public static final String AUTH_BANK_USERNAME = "admin";
    public static final String AUTH_BANK_PASSWORD = "admin";
}
