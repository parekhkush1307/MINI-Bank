package exception;

/**
 * Lab 08 – Thrown when no account with the requested number exists in the store.
 */
public class AccountNotFoundException extends BankException {

    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
    }
}
