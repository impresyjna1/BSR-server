package bsr.server.exceptions;

/**
 * Created by Impresyjna on 16.01.2017.
 */
public class AccountChecksumException extends Exception {
    public AccountChecksumException() {
    }

    public AccountChecksumException(String message) {
        super(message);
    }
}
