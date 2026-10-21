package exception;

/**
 * Lab 08 – Thrown when a deposit or withdrawal amount is invalid
 * (e.g., zero or negative).
 */
public class InvalidAmountException extends BankException {

    public InvalidAmountException(long amount) {
        super("Invalid amount: " + amount + ". Amount must be greater than zero.");
    }
}
