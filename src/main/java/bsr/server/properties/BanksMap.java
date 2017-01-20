package bsr.server.properties;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Impresyjna on 11.01.2017.
 */

/**
 * Singleton class with map to banks read from file
 */
public class BanksMap {
    private static BanksMap instance = new BanksMap();
    private Map<String, String> bankIpMap;

    /**
     * Constructor reads bank ids and addresses and make it in map form
     */
    private BanksMap() {
        bankIpMap = new HashMap<>();

        ClassLoader classLoader = getClass().getClassLoader();
        try {
            Files.lines(Paths.get(classLoader.getResource(Config.BANK_TO_IP_FILE_PATH).toURI())).forEach(line -> {
                String[] values = line.split("=");
                bankIpMap.put(values[0], values[1]);
            });
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static BanksMap getInstance() {
        return instance;
    }

    public Map<String, String> getBankIpMap() {
        return bankIpMap;
    }
}
