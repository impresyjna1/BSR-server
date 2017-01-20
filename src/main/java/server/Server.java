package server;

import bsr.server.database.DatabaseHandler;
import bsr.server.innerServices.AccountService;
import bsr.server.innerServices.UserService;
import bsr.server.outerServices.AccountResource;
import bsr.server.properties.Config;
import bsr.server.utils.BasicAuthUtil;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.jaxws.JaxwsHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * Created by Impresyjna on 27.12.2016.
 */

/**
 * Main method to start server
 */
public class Server {

    private static HttpServer serverInstance;

    public static void main( String[] args ){
        try {
            DatabaseHandler.getInstance().initDatabase();
            initializeServerAndRESTPort();
            initializeSOAPServer();
            serverInstance.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to initialize server and rest on given port and address
     */
    private static void initializeServerAndRESTPort() {
        URI baseUri = UriBuilder.fromUri("http://" + Config.SERVER_ADDR).port(Config.SERVER_PORT_REST).build();
        ResourceConfig config = new ResourceConfig()
                .register(AccountResource.class)
                .register(BasicAuthUtil.class)
                .register(JacksonJaxbJsonProvider.class);
        serverInstance = GrizzlyHttpServerFactory.createHttpServer(baseUri, config, false);
    }

    /**
     * Method to inialize SOAP server on given port
     */
    private static void initializeSOAPServer() {
        NetworkListener networkListener = new NetworkListener("jaxws-listener", "0.0.0.0", Config.SERVER_PORT_SOAP);
        serverInstance.getServerConfiguration().addHttpHandler(new JaxwsHandler(new UserService()), "/users");
        serverInstance.getServerConfiguration().addHttpHandler(new JaxwsHandler(new AccountService()), "/internalAccounts");
        serverInstance.addListener(networkListener);
    }
}
