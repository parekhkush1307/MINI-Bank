package model;

import java.io.Serializable;

/**
 * Lab 05 – Current (checking) account subclass.
 * Interest rate: 0%.
 * Allows the balance to go negative up to {@code overdraftLimit}.
 */
public class CurrentAccount extends Account implements Serializable {

    private static final long serialVersionUID = 3L;

    private final long overdraftLimit;

    /**
     * Creates a CurrentAccount with the given owner, opening balance, and overdraft limit.
     */
    public CurrentAccount(String ownerName, long openingBalance, long overdraftLimit) {
        super(ownerName, openingBalance);
        this.overdraftLimit = overdraftLimit;
    }

    /** Creates a CurrentAccount with a default overdraft limit of 10 000. */
    public CurrentAccount(String ownerName, long openingBalance) {
        this(ownerName, openingBalance, 10_000L);
    }

    @Override
    public double interestRate() {
        return 0.0;
    }

    /**
     * Withdrawal is permitted as long as (balance - amount) ≥ -overdraftLimit.
     */
    @Override
    public boolean canWithdraw(long amount) {
        return (getBalance() - amount) >= -overdraftLimit;
    }

    public long getOverdraftLimit() { return overdraftLimit; }

    @Override
    public String toString() {
        return super.toString() + " | Overdraft: ₹" + overdraftLimit + " | Type: CURRENT";
    }
}
