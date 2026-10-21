package service;

import model.Account;

/**
 * Lab 09 – Implements Runnable to deposit a fixed amount into an account
 * a given number of times.  Demonstrates race conditions (before
 * synchronisation) and thread-safe execution (after synchronisation).
 */
public class AccountWorker implements Runnable {

    private final Account account;
    private final int     times;
    private final long    amount;

    public AccountWorker(Account account, int times, long amount) {
        this.account = account;
        this.times   = times;
        this.amount  = amount;
    }

    @Override
    public void run() {
        for (int i = 0; i < times; i++) {
            try {
                account.deposit(amount);
            } catch (exception.InvalidAmountException e) {
                System.err.println(Thread.currentThread().getName()
                        + " deposit error: " + e.getMessage());
            }
        }
        System.out.println(Thread.currentThread().getName()
                + " completed " + times + " deposit(s) of ₹" + amount);
    }
}
