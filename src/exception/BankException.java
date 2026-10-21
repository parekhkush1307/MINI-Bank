package exception;

/**
 * Lab 08 – Base checked exception for all MiniBank domain errors.
 * Every specific banking exception extends this class.
 */
public class BankException extends Exception {

    public BankException(String message) {
        super(message);
    }

    public BankException(String message, Throwable cause) {
        super(message, cause);
    }
}
