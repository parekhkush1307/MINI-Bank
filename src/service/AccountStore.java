package service;

import model.Account;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lab 12 – Wraps a HashMap (and a ConcurrentHashMap for thread safety) to
 * provide named add / get / getAll / sortedByBalance operations.
 *
 * <p>The {@code ConcurrentHashMap} is used by the TransactionProcessor so that
 * concurrent operations are safe without explicit synchronization.</p>
 */
public class AccountStore {

    // Primary store – keyed by account number
    private final ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();

    // Transaction history as an ordered list
    private final ArrayList<String> transactionHistory = new ArrayList<>();

    // ------------------------------------------------------------------ mutators

    /**
     * Adds an account to the store.
     * @param account the account to add (must have a unique account number)
     */
    public void add(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    /**
     * Records a transaction entry in the history list.
     * @param entry a description such as "DEPOSIT AC0001 500"
     */
    public void recordTransaction(String entry) {
        transactionHistory.add(entry);
    }

    // ------------------------------------------------------------------ queries

    /**
     * Returns the account with the given number, or {@code null} if not found.
     */
    public Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    /**
     * Returns all accounts as an ArrayList.
     */
    public ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    /**
     * Returns all accounts sorted by balance descending (Lab 12).
     */
    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> list = getAll();
        list.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return list;
    }

    /**
     * Lab 12 Supplementary – returns accounts whose balance exceeds {@code threshold}.
     */
    public ArrayList<Account> withBalanceAbove(long threshold) {
        ArrayList<Account> result = new ArrayList<>();
        for (Account a : accounts.values()) {
            if (a.getBalance() > threshold) result.add(a);
        }
        return result;
    }

    /**
     * Returns the transaction history list.
     */
    public ArrayList<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    /**
     * Returns the underlying ConcurrentHashMap (for direct use by the thread pool).
     */
    public ConcurrentHashMap<String, Account> getConcurrentMap() {
        return accounts;
    }

    public int size() { return accounts.size(); }
}
