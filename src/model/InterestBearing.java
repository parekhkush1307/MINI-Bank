package model;

/**
 * Lab 06 – Interface for accounts that accrue interest.
 * Provides a default implementation of yearlyInterest() so that
 * implementing classes only need to supply interestRate() and getBalance().
 */
public interface InterestBearing {

    /** Annual interest rate as a percentage (e.g. 4.0 for 4%). */
    double interestRate();

    /** Current account balance in rupees. */
    long getBalance();

    /**
     * Default method – calculates yearly interest from the rate and balance.
     * Implementing classes can override for a different formula.
     */
    default double yearlyInterest() {
        return (getBalance() * interestRate()) / 100.0;
    }

    /**
     * Lab 06 Supplementary – second default method returning monthly interest.
     */
    default double monthlyInterest() {
        return yearlyInterest() / 12.0;
    }
}
