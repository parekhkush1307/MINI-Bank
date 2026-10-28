package exception;


public class InvalidAmountException extends BankException {

    public InvalidAmountException(long amount) {
        super("Invalid amount: " + amount + ". Amount must be greater than zero.");
    }
}
