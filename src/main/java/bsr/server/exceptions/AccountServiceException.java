package bsr.server.exceptions;

/**
 * Created by maciej on 03.01.2017.
 */
public class AccountServiceException extends Exception{
    public AccountServiceException() {
        super();
    }

    public  AccountServiceException(String message) {
        super(message);
    }
}
