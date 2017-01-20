package bsr.server.outerServices;

import bsr.server.database.DatabaseHandler;
import bsr.server.exceptions.OperationException;
import bsr.server.models.Account;
import bsr.server.models.accountOperations.Transfer;
import bsr.server.utils.AccountNumberAuthUtil;
import org.mongodb.morphia.Datastore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Impresyjna on 11.01.2017.
 */

/**
 * Class with method to serve REST connection with bank
 */
@Path("/accounts")
public class AccountResource {
    private Datastore mongoDataStore = DatabaseHandler.getInstance().getMongoDataStore();

    /**
     * Method called when user send POST request to bank address on REST port e.g. localhost:8080/12345678901234567890123456
     * @param accountNumber Param from address where to transfer money
     * @param transfer Object to what parse given json
     * @return Response with code if success, otherwise WebApplicationException with code and message
     */
    @POST
    @Path("/{accountNumber}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response makeTransfer(@PathParam("accountNumber") final String accountNumber, Transfer transfer) {
        String from = transfer.getSourceAccountNumber();
        String title = transfer.getTitle();
        int amount = transfer.getAmount();
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

    /**
     * Method to validate params
     * @param from Account number from transfer is made, checks if is not null, contains only digits, length is 26 and checksum is valid
     * @param title Title of transfer, checks if is not null, length is more than 0 and contains other letters than white signs
     * @param amount Amount of transfer in pennies, checks if amount is more than 0 so transfer can be made
     */
    private void validateParams(String from, String title, int amount) {
        String invalidFields = "";

        if (amount <= 0) {
            invalidFields += "amount";
        }

        if (from == null || !from.matches("\\d+") || from.length()!=26 || !AccountNumberAuthUtil.checkChecksum(from)) {
            invalidFields += "from";
        }

        if (title == null || title.length() == 0 || !title.matches(".*\\w.*")) {
            invalidFields += "title";
        }

        if(invalidFields.length() > 0) {
            invalidFields = invalidFields.substring(0, invalidFields.length());
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("{\n" +
                    "  \"error\": \"" + invalidFields + " invalid\"\n" +
                    "}").build());
        }
    }
}
