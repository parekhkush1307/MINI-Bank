package model;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.Positive;

import java.io.Serializable;

/**
 * Lab 02 – Base Account class (made abstract in Lab 05).
 * Lab 03 – Overrides toString, equals, hashCode; implements Cloneable.
 * Lab 05 – Abstract class with abstract interestRate() and canWithdraw().
 * Lab 06 – Implements Transactable and InterestBearing interfaces.
 * Lab 07 – @Id on accountNumber, @Positive on balance.
 * Lab 08 – deposit/withdraw throw checked exceptions.
 * Lab 09 – deposit/withdraw are synchronized for thread-safety.
 * Lab 11 – Implements Serializable for object persistence.
 * Lab 12 – Implements Comparable<Account> by account number.
 */
public abstract class Account implements Transactable, InterestBearing, Serializable, Comparable<Account> {

    private static final long serialVersionUID = 1L;

    /** Auto-generated unique account number, e.g. "AC0001". */
    private static long accountCounter = 0;

    @Id
    private final String accountNumber;

    private String ownerName;

    @Positive(message = "Balance must be > 0")
    private long balance;

    private boolean active;

    /** Daily withdrawal limit (Supplementary Lab 08). Default: no limit. */
    private long dailyLimit = Long.MAX_VALUE;

    // ------------------------------------------------------------------ constructors

    /**
     * Primary constructor – generates a unique account number.
     *
     * @param ownerName     the name of the account holder
     * @param openingBalance the initial balance in rupees (whole)
     */
    public Account(String ownerName, long openingBalance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    /**
     * Secondary constructor – opens an account with zero balance.
     * Uses constructor chaining with {@code this()}.
     *
     * @param ownerName the name of the account holder
     */
    public Account(String ownerName) {
        this(ownerName, 0);
    }

    // ------------------------------------------------------------------ id generation

    private static synchronized String generateAccountNumber() {
        accountCounter++;
        return String.format("AC%04d", accountCounter);
    }

    // ------------------------------------------------------------------ abstract methods (Lab 05)

    /** Returns the annual interest rate applicable to this account type. */
    public abstract double interestRate();

    /**
     * Determines whether a withdrawal of {@code amount} is allowed
     * given the current balance and account type's rules.
     *
     * @param amount the withdrawal amount (positive)
     * @return {@code true} if the withdrawal is permitted
     */
    public abstract boolean canWithdraw(long amount);

    // ------------------------------------------------------------------ InterestBearing default (Lab 06)

    /**
     * Returns the yearly interest amount based on current balance.
     * Default implementation in the interface is invoked; subclasses may override.
     */
    @Override
    public double yearlyInterest() {
        return (balance * interestRate()) / 100.0;
    }

    /**
     * Lab 05 Supplementary – Monthly interest calculated from yearly.
     */
    public double monthlyInterest() {
        return yearlyInterest() / 12.0;
    }

    // ------------------------------------------------------------------ Transactable operations (Lab 08)

    /**
     * Deposits {@code amount} into this account.
     * Thread-safe (Lab 09). Throws {@link InvalidAmountException} for non-positive amounts.
     *
     * @param amount the amount to deposit (must be > 0)
     * @throws InvalidAmountException if amount ≤ 0
     */
    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        balance += amount;
    }

    /**
     * Withdraws {@code amount} from this account if permitted.
     * Thread-safe (Lab 09). Throws exceptions on invalid or insufficient amounts.
     *
     * @param amount the amount to withdraw (must be > 0)
     * @throws InvalidAmountException      if amount ≤ 0
     * @throws InsufficientFundsException  if the account type rules disallow the withdrawal
     */
    @Override
    public synchronized boolean withdraw(long amount)
            throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        if (!canWithdraw(amount)) {
            throw new InsufficientFundsException(amount - balance);
        }
        balance -= amount;
        return true;
    }

    /**
     * Transfers {@code amount} from this account to {@code to}.
     * Uses try-catch-finally and re-throws as BankException (Lab 08).
     *
     * @param to     destination account
     * @param amount amount to transfer
     * @throws BankException on any failure
     */
    public synchronized void transfer(Account to, long amount) throws BankException {
        try {
            this.withdraw(amount);
            to.deposit(amount);
        } catch (InvalidAmountException | InsufficientFundsException e) {
            throw new BankException("Transfer failed: " + e.getMessage(), e);
        } finally {
            System.out.println("[Transfer] Attempted transfer of ₹" + amount
                    + " from " + accountNumber + " to " + to.getAccountNumber());
        }
    }

    // ------------------------------------------------------------------ Comparable (Lab 12)

    @Override
    public int compareTo(Account other) {
        return this.accountNumber.compareTo(other.accountNumber);
    }

    // ------------------------------------------------------------------ Object overrides (Lab 03)

    /**
     * Returns a readable one-line description of this account.
     * Lab 03 Supplementary – also shows the active status.
     */
    @Override
    public String toString() {
        return String.format("%-8s | %-20s | ₹%-10d | %-8s | Rate: %.1f%%",
                accountNumber, ownerName, balance,
                active ? "Active" : "Inactive",
                interestRate());
    }

    /**
     * Two accounts are equal if and only if they share the same account number.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account other)) return false;
        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }

    // ------------------------------------------------------------------ getters

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName()    { return ownerName; }
    public long   getBalance()      { return balance; }
    public boolean isActive()       { return active; }
    public long   getDailyLimit()   { return dailyLimit; }

    // ------------------------------------------------------------------ setters (controlled)

    public void setOwnerName(String ownerName)  { this.ownerName = ownerName; }
    public void setActive(boolean active)        { this.active = active; }
    public void setDailyLimit(long dailyLimit)   { this.dailyLimit = dailyLimit; }

    /**
     * Lab 03 Supplementary – A formatted multi-line statement for this account.
     */
    public String toStatement() {
        return "=".repeat(55) + "\n"
             + "  Account Statement\n"
             + "=".repeat(55) + "\n"
             + "  Account No : " + accountNumber + "\n"
             + "  Owner      : " + ownerName + "\n"
             + "  Balance    : ₹" + balance + "\n"
             + "  Status     : " + (active ? "Active" : "Inactive") + "\n"
             + "  Int. Rate  : " + interestRate() + "%\n"
             + "  Yearly Int.: ₹" + String.format("%.2f", yearlyInterest()) + "\n"
             + "=".repeat(55);
    }
}
