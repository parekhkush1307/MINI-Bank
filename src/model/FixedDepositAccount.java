package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Lab 05 – Fixed Deposit account subclass.
 * Interest rate: 7.0%.
 * Withdrawal is not allowed until the maturity date (Lab 05 Supplementary).
 */
public class FixedDepositAccount extends Account implements Serializable {

    private static final long serialVersionUID = 4L;

    private final LocalDate maturityDate;

    /**
     * Creates a FixedDepositAccount locked until {@code maturityDate}.
     */
    public FixedDepositAccount(String ownerName, long openingBalance, LocalDate maturityDate) {
        super(ownerName, openingBalance);
        this.maturityDate = maturityDate;
    }

    /** Creates a FixedDepositAccount maturing one year from today. */
    public FixedDepositAccount(String ownerName, long openingBalance) {
        this(ownerName, openingBalance, LocalDate.now().plusYears(1));
    }

    @Override
    public double interestRate() {
        return 7.0;
    }

    /**
     * Withdrawal is only allowed on or after the maturity date (Lab 05 Supplementary).
     * Before maturity, returns {@code false}.
     */
    @Override
    public boolean canWithdraw(long amount) {
        return !LocalDate.now().isBefore(maturityDate);
    }

    public LocalDate getMaturityDate() { return maturityDate; }

    @Override
    public String toString() {
        return super.toString() + " | Matures: " + maturityDate + " | Type: FIXED DEPOSIT";
    }
}
