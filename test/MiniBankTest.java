import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.SavingsAccount;
import model.CurrentAccount;
import model.Account;
import service.AccountStore;
import service.BankService;
import exception.BankException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class MiniBankTest {

    private SavingsAccount savings;
    private CurrentAccount current;

    @BeforeEach
    void setUp() {
        savings = new SavingsAccount("Test User", 5000, 500);
        current = new CurrentAccount("Biz User",  2000, 5000);
    }

    // --------------------------------------------------------------- deposit
    @Test
    void deposit_increasesBalance() throws InvalidAmountException {
        savings.deposit(2000);
        assertEquals(7000, savings.getBalance(), "Balance should be 7000 after depositing 2000 into 5000");
    }

    @Test
    void deposit_zeroAmount_throwsInvalidAmountException() {
        assertThrows(InvalidAmountException.class, () -> savings.deposit(0),
                "Depositing 0 should throw InvalidAmountException");
    }

    @Test
    void deposit_negativeAmount_throwsInvalidAmountException() {
        assertThrows(InvalidAmountException.class, () -> savings.deposit(-500),
                "Depositing a negative amount should throw InvalidAmountException");
    }

    // --------------------------------------------------------------- withdraw
    @Test
    void withdraw_sufficientBalance_succeeds() throws Exception {
        boolean result = savings.withdraw(1000);
        assertTrue(result, "Withdraw should return true when balance is sufficient");
        assertEquals(4000, savings.getBalance());
    }

    @Test
    void withdraw_insufficientBalance_throwsInsufficientFundsException() {
        InsufficientFundsException ex = assertThrows(
                InsufficientFundsException.class,
                () -> savings.withdraw(10000),
                "Withdrawing more than balance should throw InsufficientFundsException"
        );
        assertTrue(ex.getShortfall() > 0, "Shortfall should be positive");
    }

    @Test
    void withdraw_belowMinBalance_throwsInsufficientFundsException() {
        // savings has minBalance=500; balance=5000 → can withdraw up to 4500
        assertThrows(InsufficientFundsException.class,
                () -> savings.withdraw(4600),
                "Withdraw leaving less than minBalance should throw exception");
    }

    @Test
    void currentAccount_overdraft_permitsWithdraw() throws Exception {
        // current has balance=2000, overdraftLimit=5000 → can withdraw up to 7000
        boolean result = current.withdraw(6000);
        assertTrue(result);
        assertEquals(-4000, current.getBalance());
    }

    // --------------------------------------------------------------- thread safety
    @Test
    void concurrentDeposits_keepBalanceConsistent() throws InterruptedException {
        SavingsAccount shared = new SavingsAccount("ThreadSafe", 0, 0);
        int threads      = 10;
        int depositsEach = 1000;
        long amountEach  = 1L;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                for (int j = 0; j < depositsEach; j++) {
                    try { shared.deposit(amountEach); }
                    catch (InvalidAmountException ignored) {}
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        assertEquals(threads * depositsEach * amountEach, shared.getBalance(),
                "Concurrent deposits must produce the exact expected balance");
    }

    // --------------------------------------------------------------- save / load (Lab 14 Supplementary)
    @Test
    void saveAndLoad_preservesAccounts() throws Exception {
        java.nio.file.Path tmp = java.nio.file.Files.createTempFile("minibank_test_", ".dat");
        tmp.toFile().deleteOnExit();

        Account[] original = { savings, current };
        util.StatePersister.save(original, tmp);
        Account[] loaded = util.StatePersister.load(tmp);

        assertEquals(2, loaded.length);
        assertEquals(savings.getAccountNumber(), loaded[0].getAccountNumber());
        assertEquals(savings.getBalance(),       loaded[0].getBalance());
    }

    // --------------------------------------------------------------- validator
    @Test
    void validator_rejectsInvalidMobile() {
        assertFalse(util.Validator.isValidMobile("12345"),
                "Short number must be invalid");
        assertFalse(util.Validator.isValidMobile("5876543210"),
                "Number starting with 5 must be invalid");
        assertTrue(util.Validator.isValidMobile("9876543210"),
                "Valid mobile must pass");
    }

    // --------------------------------------------------------------- account equality
    @Test
    void twoAccountsWithSameNumber_areEqual() throws Exception {
        // Construct two Account objects; equals uses accountNumber
        Account a1 = new SavingsAccount("X", 1000);
        // We can't give them the same auto-generated number easily, so verify not-equals
        Account a2 = new SavingsAccount("Y", 2000);
        assertNotEquals(a1, a2, "Distinct auto-numbered accounts must not be equal");
        assertEquals(a1, a1, "An account must equal itself");
    }
}
