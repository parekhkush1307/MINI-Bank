package exception;

/**
 * Lab 08 – Thrown when a withdrawal or transfer cannot be completed
 * because the account balance is insufficient.
 * The {@code shortfall} field tells the caller exactly how much more is needed.
 */
public class InsufficientFundsException extends BankException {

    private final long shortfall;

    public InsufficientFundsException(long shortfall) {
        super("Withdrawal failed: short by " + shortfall);
        this.shortfall = shortfall;
    }

    public long getShortfall() {
        return shortfall;
    }
}
