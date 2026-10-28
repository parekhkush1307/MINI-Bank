package model;

import exception.InvalidAmountException;
import exception.InsufficientFundsException;

public interface Transactable {

    /**
     * Deposits the given amount.
     * @param amount amount to deposit, must be &gt; 0
     * @throws InvalidAmountException if amount is not positive
     */
    void deposit(long amount) throws InvalidAmountException;

    /**
     * Withdraws the given amount.
     * @param amount amount to withdraw, must be &gt; 0
     * @return {@code true} on success
     * @throws InvalidAmountException     if amount is not positive
     * @throws InsufficientFundsException if the balance is insufficient
     */
    boolean withdraw(long amount) throws InvalidAmountException, InsufficientFundsException;
}
