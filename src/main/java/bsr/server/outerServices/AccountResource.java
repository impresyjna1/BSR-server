package bsr.server.outerServices;

import bsr.server.database.DatabaseHandler;
import bsr.server.exceptions.AccountServiceException;
import bsr.server.exceptions.NotValidException;
import bsr.server.exceptions.OperationException;
import bsr.server.models.Account;
import bsr.server.models.accountOperations.Transfer;
import org.mongodb.morphia.Datastore;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Impresyjna on 11.01.2017.
 */
@Path("/accounts")
public class AccountResource {
    private Datastore mongoDataStore = DatabaseHandler.getInstance().getMongoDataStore();

    @POST
    @Path("/{accountNumber}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response makeTransfer(@PathParam("accountNumber") final String accountNumber, @NotNull String from, @NotNull String title, @NotNull int amount) {
        validateParams(from, title, amount);

        if (amount < 0 ){
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("{\n" +
                    "  \"error\": \"amount is negative\"\n" +
                    "}").build());
        }
        Account targetAccount = mongoDataStore.find(Account.class)
                .field("accountNumber")
                .equal(accountNumber)
                .get();

        if (targetAccount == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("{\n" +
                    "  \"error\": \"target account not exists\"\n" +
                    "}").build());
        }

        Transfer commingTransfer = new Transfer(title, amount, accountNumber, Transfer.TransferEnum.IN, from);

        try {
            commingTransfer.doOperation(targetAccount);
        } catch (OperationException e) {
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\n" +
                    "  \"error\": \"operation internal server problems\"\n" +
                    "}").build());
        }
        mongoDataStore.save(targetAccount);

        return Response.created(null).build();
    }

    private void validateParams(String from, String title, int amount) {
        String invalidFields = "";

        if (amount == 0) {
            invalidFields += "amount";
        }

        if (from == null || !from.matches("\\d+") || from.length()!=26) {
            invalidFields += "from";
        }

        if (title == null || title.length() == 0 || title.matches(".*\\w.*")) {
            invalidFields += "title";
        }

        if(invalidFields.length() > 0) {
            invalidFields = invalidFields.substring(0, invalidFields.length() -1);
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("{\n" +
                    "  \"error\": \"" + invalidFields + " invalid\"\n" +
                    "}").build());
        }
    }
}
