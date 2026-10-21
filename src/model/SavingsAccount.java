package model;

import java.io.Serializable;

/**
 * Lab 05 – Savings account subclass.
 * Interest rate: 4.0%.
 * Withdrawal allowed only if the remaining balance ≥ minBalance.
 */
public class SavingsAccount extends Account implements Serializable {

    private static final long serialVersionUID = 2L;

    private final long minBalance;

    /**
     * Creates a SavingsAccount with the given owner, opening balance, and minimum balance.
     */
    public SavingsAccount(String ownerName, long openingBalance, long minBalance) {
        super(ownerName, openingBalance);
        this.minBalance = minBalance;
    }

    /** Creates a SavingsAccount with a default minimum balance of 500. */
    public SavingsAccount(String ownerName, long openingBalance) {
        this(ownerName, openingBalance, 500L);
    }

    @Override
    public double interestRate() {
        return 4.0;
    }

    /**
     * Withdrawal is permitted only when the resulting balance ≥ minBalance.
     */
    @Override
    public boolean canWithdraw(long amount) {
        return (getBalance() - amount) >= minBalance;
    }

    public long getMinBalance() { return minBalance; }

    @Override
    public String toString() {
        return super.toString() + " | MinBal: ₹" + minBalance + " | Type: SAVINGS";
    }
}
