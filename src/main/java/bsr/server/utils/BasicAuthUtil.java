package bsr.server.utils;

import bsr.server.properties.Config;
import org.glassfish.jersey.internal.util.Base64;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by Impresyjna on 11.01.2017.
 */

/**
 * Class to handle basic auth while connecting to other bank
 */
public class BasicAuthUtil implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String auth = containerRequestContext.getHeaderString("Authorization");
        if(auth == null){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"missing bank authentication data\"}").build());
        }

        String[] credentials = Base64.decodeAsString(auth.substring(6)).split(":");
        if(credentials.length < 2) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"missing bank login or password\"}").build());
        }
        if(!credentials[1].equals(Config.AUTH_BANK_PASSWORD)) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"wrong bank password\"}").build());
        }

        if(!credentials[0].equals(Config.AUTH_BANK_USERNAME)) {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\": \"unknown bank\"}").build());
        }
    }
}
