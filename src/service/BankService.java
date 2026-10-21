package service;

import model.Account;
import model.SavingsAccount;
import model.CurrentAccount;
import model.FixedDepositAccount;
import exception.*;
import util.*;
import model.annotation.Audit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Lab 14 – The application service layer.
 * Wires the generic Repository, Validator, and exception classes together.
 * Each public method is the single entry point for one banking operation;
 * the {@code @Audit} annotation marks methods for reflective logging.
 */
public class BankService {

    private final Repository<Account> repository = new Repository<>();
    private final Path logFile = Paths.get("logs", "transactions.log");

    // ------------------------------------------------------------------ account management

    /**
     * Opens a new savings account after validating the mobile number.
     *
     * @param ownerName     the account owner's name
     * @param mobile        mobile number for validation
     * @param openingBalance the initial deposit
     * @return the newly created account
     * @throws BankException if mobile is invalid or amount is non-positive
     */
    @Audit(description = "Open savings account")
    public Account openSavingsAccount(String ownerName, String mobile, long openingBalance)
            throws BankException {
        if (!Validator.isValidMobile(mobile)) {
            throw new InvalidAmountException(0); // reuse for simplicity; use custom in real apps
        }
        if (openingBalance < 0) {
            throw new InvalidAmountException(openingBalance);
        }
        Account acc = new SavingsAccount(ownerName, openingBalance);
        repository.save(acc);
        logTransaction("OPEN_ACCOUNT " + acc.getAccountNumber() + " " + openingBalance);
        return acc;
    }

    /**
     * Opens a new current account.
     */
    @Audit(description = "Open current account")
    public Account openCurrentAccount(String ownerName, long openingBalance)
            throws BankException {
        Account acc = new CurrentAccount(ownerName, openingBalance);
        repository.save(acc);
        logTransaction("OPEN_ACCOUNT " + acc.getAccountNumber() + " " + openingBalance);
        return acc;
    }

    // ------------------------------------------------------------------ transactions

    /**
     * Deposits {@code amount} into the account identified by {@code accountNumber}.
     */
    @Audit(description = "Deposit")
    public void deposit(String accountNumber, long amount)
            throws AccountNotFoundException, InvalidAmountException {
        Account acc = findOrThrow(accountNumber);
        acc.deposit(amount);
        logTransaction("DEPOSIT " + accountNumber + " " + amount);
    }

    /**
     * Withdraws {@code amount} from the account identified by {@code accountNumber}.
     */
    @Audit(description = "Withdraw")
    public void withdraw(String accountNumber, long amount)
            throws AccountNotFoundException, InvalidAmountException, InsufficientFundsException {
        Account acc = findOrThrow(accountNumber);
        acc.withdraw(amount);
        logTransaction("WITHDRAW " + accountNumber + " " + amount);
    }

    /**
     * Transfers {@code amount} from one account to another.
     */
    @Audit(description = "Transfer")
    public void transfer(String fromNumber, String toNumber, long amount)
            throws AccountNotFoundException, BankException {
        Account from = findOrThrow(fromNumber);
        Account to   = findOrThrow(toNumber);
        from.transfer(to, amount);
        logTransaction("TRANSFER " + fromNumber + " -> " + toNumber + " " + amount);
    }

    // ------------------------------------------------------------------ queries

    /**
     * Returns a formatted statement for the given account.
     */
    public String statement(String accountNumber) throws AccountNotFoundException {
        Account acc = findOrThrow(accountNumber);
        return StatementFormatter.buildStatement(acc);
    }

    /**
     * Returns all accounts in the repository.
     */
    public List<Account> listAccounts() {
        return repository.findAll();
    }

    /**
     * Lab 14 Supplementary – searches for accounts whose owner name contains the query string.
     */
    public List<Account> searchByOwner(String query) {
        List<Account> results = new ArrayList<>();
        for (Account a : repository.findAll()) {
            if (a.getOwnerName().toLowerCase().contains(query.toLowerCase())) {
                results.add(a);
            }
        }
        return results;
    }

    // ------------------------------------------------------------------ persistence

    /**
     * Saves all accounts to the given file path.
     */
    public void saveState(Path file) throws IOException {
        List<Account> all = repository.findAll();
        StatePersister.save(all.toArray(new Account[0]), file);
    }

    /**
     * Loads accounts from the file path into the repository.
     */
    public void loadState(Path file) throws IOException, ClassNotFoundException {
        Account[] loaded = StatePersister.load(file);
        for (Account a : loaded) {
            repository.save(a);
        }
    }

    // ------------------------------------------------------------------ internal helpers

    private Account findOrThrow(String accountNumber) throws AccountNotFoundException {
        Account acc = repository.findById(accountNumber);
        if (acc == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return acc;
    }

    private void logTransaction(String entry) {
        try {
            TransactionLog.append(logFile, entry);
        } catch (IOException e) {
            System.err.println("[BankService] Failed to log transaction: " + e.getMessage());
        }
    }
}
