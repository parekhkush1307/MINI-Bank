import exception.*;
import model.*;
import model.annotation.Audit;
import service.*;
import util.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import static util.Validator.*;          // Lab 06 – static import

/**
 * =====================================================================
 *  MiniBank – Console Banking Application
 *  Covers Labs 01 – 14 of the OOP-Java practical series.
 * =====================================================================
 *
 *  Lab 01 – BankInfo record, MenuOption enum, Scanner input, switch expression,
 *            menu loop with EXIT.
 *  Lab 02 – Account / Customer objects created in main; array of accounts.
 *  Lab 03 – toString / equals / instanceof demonstrated.
 *  Lab 04 – Validator, CommandParser, Command record demonstrated.
 *  Lab 05 – SavingsAccount / CurrentAccount / FixedDepositAccount in Account[].
 *  Lab 06 – WithdrawRule anonymous class + lambda; JAR manifest.
 *  Lab 07 – AnnotationValidator demonstrated.
 *  Lab 08 – Exception handling with try-catch-finally, try-with-resources.
 *  Lab 09 – Race condition demo, then synchronized fix.
 *  Lab 10 – TransactionProcessor thread pool; producer-consumer; deadlock demo.
 *  Lab 11 – StatePersister save/load; TransactionLog; ReportGenerator.
 *  Lab 12 – AccountStore (HashMap + ConcurrentHashMap); Comparable; sorting.
 *  Lab 13 – Generic Repository<Account> used by BankService.
 *  Lab 14 – BankService wired to the menu; JUnit tests (see MiniBankTest.java).
 */
public class MiniBank {

    // ===================================================================
    // Lab 01 – BankInfo record (read-only, auto-generated constructor)
    // ===================================================================

    /**
     * Immutable bank information record.
     * Java auto-generates: constructor, accessors name() / branch(), toString,
     * equals, hashCode.
     */
    record BankInfo(String name, String branch) {}

    // ===================================================================
    // Lab 01 – MenuOption enum
    // ===================================================================

    enum MenuOption {
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        VIEW_STATEMENT,
        LIST_ACCOUNTS,
        SEARCH_ACCOUNT,
        RUN_REPORT,
        WORKING_HOURS,   // Supplementary Lab 01
        EXIT;

        /** Returns the 1-based menu number for this option. */
        int number() { return ordinal() + 1; }
    }

    // ===================================================================
    // Path constants
    // ===================================================================

    private static final Path DATA_FILE   = Paths.get("data", "accounts.dat");
    private static final Path LOGS_DIR    = Paths.get("logs");
    private static final Path REPORT_FILE = Paths.get("data", "report.txt");

    // ===================================================================
    // main
    // ===================================================================

    public static void main(String[] args) {

        // ---------------------------------------------------------------
        // Lab 01 – print BankInfo header
        // ---------------------------------------------------------------
        BankInfo bank = new BankInfo("MiniBank", "Ahmedabad Main Branch");
        printHeader(bank);

        // ---------------------------------------------------------------
        // Lab 14 – create the service and load persisted state
        // ---------------------------------------------------------------
        BankService service = new BankService();
        try {
            if (DATA_FILE.toFile().exists()) {
                service.loadState(DATA_FILE);
                System.out.println("[MiniBank] Previous session data loaded.\n");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[MiniBank] Could not load state: " + e.getMessage());
        }

        // ---------------------------------------------------------------
        // Demo sections (run once on startup to show all lab features)
        // ---------------------------------------------------------------
        runLab02to03Demos();
        runLab04Demos();
        runLab05Demos();
        runLab06Demos();
        runLab07Demos();
        runLab08Demos(service);
        runLab09Demos();
        runLab10Demos();
        runLab11Demos();
        runLab12Demos();
        runLab13Demo();

        // ---------------------------------------------------------------
        // Lab 01 – interactive menu loop
        // ---------------------------------------------------------------
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readChoice(scanner);

            // Lab 01 Supplementary – re-prompt on invalid number
            if (choice < 1 || choice > MenuOption.values().length) {
                System.out.println("  ⚠  Invalid option. Please enter a number between 1 and "
                        + MenuOption.values().length + ".\n");
                continue;
            }

            MenuOption option = MenuOption.values()[choice - 1];

            // Lab 01/02 – switch expression (not statement)
            String feedback = switch (option) {
                case OPEN_ACCOUNT    -> handleOpenAccount(scanner, service);
                case DEPOSIT         -> handleDeposit(scanner, service);
                case WITHDRAW        -> handleWithdraw(scanner, service);
                case TRANSFER        -> handleTransfer(scanner, service);
                case VIEW_STATEMENT  -> handleStatement(scanner, service);
                case LIST_ACCOUNTS   -> handleListAccounts(service);
                case SEARCH_ACCOUNT  -> handleSearch(scanner, service);
                case RUN_REPORT      -> handleReport();
                case WORKING_HOURS   -> "Working Hours: Mon–Sat  09:00–17:00  |  Closed on Sundays.";
                case EXIT            -> { running = false; yield "Goodbye! Thank you for banking with " + bank.name() + "."; }
            };

            System.out.println("\n" + feedback + "\n");
        }

        // Lab 14 – save state on exit
        try {
            service.saveState(DATA_FILE);
        } catch (IOException e) {
            System.err.println("[MiniBank] Could not save state: " + e.getMessage());
        }

        // Lab 14 – reflective audit logger: list all @Audit-annotated service methods
        System.out.println("\n--- Audit Log (reflective scan of @Audit methods) ---");
        for (Method m : BankService.class.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Audit.class)) {
                Audit a = m.getAnnotation(Audit.class);
                System.out.printf("  @Audit method: %-20s → %s%n", m.getName(), a.description());
            }
        }

        scanner.close();
        System.out.println("\n[MiniBank] Session ended.");
    }

    // ===================================================================
    // Menu helpers
    // ===================================================================

    private static void printHeader(BankInfo bank) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.printf ("║  %-40s  ║%n", bank.name());
        System.out.printf ("║  %-40s  ║%n", bank.branch());
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printMenu() {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.println("│           MAIN MENU                 │");
        System.out.println("├─────────────────────────────────────┤");
        for (MenuOption opt : MenuOption.values()) {
            System.out.printf("│  %2d. %-32s│%n", opt.number(),
                    opt.name().replace('_', ' '));
        }
        System.out.println("└─────────────────────────────────────┘");
        System.out.print("  Enter choice: ");
    }

    private static int readChoice(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;   // triggers the re-prompt path
        }
    }

    // ===================================================================
    // Menu action handlers (Lab 14 – call real BankService)
    // ===================================================================

    private static String handleOpenAccount(Scanner sc, BankService svc) {
        System.out.print("  Owner name  : ");
        String name = sc.nextLine().trim();
        System.out.print("  Mobile no.  : ");
        String mobile = sc.nextLine().trim();
        System.out.print("  Opening bal : ₹");
        long bal = safeParseLong(sc.nextLine().trim());

        try {
            Account acc = svc.openSavingsAccount(name, mobile, bal);
            return "✔ Account opened: " + acc.getAccountNumber()
                    + " for " + name + " with balance ₹" + bal;
        } catch (BankException e) {
            return "✘ " + e.getMessage();
        }
    }

    private static String handleDeposit(Scanner sc, BankService svc) {
        System.out.print("  Account No. : ");
        String acc = sc.nextLine().trim();
        System.out.print("  Amount (₹)  : ");
        long amt = safeParseLong(sc.nextLine().trim());

        try {
            svc.deposit(acc, amt);
            return "✔ Deposited ₹" + amt + " into " + acc;
        } catch (BankException e) {
            return "✘ " + e.getMessage();
        }
    }

    private static String handleWithdraw(Scanner sc, BankService svc) {
        System.out.print("  Account No. : ");
        String acc = sc.nextLine().trim();
        System.out.print("  Amount (₹)  : ");
        long amt = safeParseLong(sc.nextLine().trim());

        try {
            svc.withdraw(acc, amt);
            return "✔ Withdrew ₹" + amt + " from " + acc;
        } catch (InsufficientFundsException e) {
            return "✘ Insufficient funds. Short by ₹" + e.getShortfall();
        } catch (BankException e) {
            return "✘ " + e.getMessage();
        }
    }

    private static String handleTransfer(Scanner sc, BankService svc) {
        System.out.print("  From account: ");
        String from = sc.nextLine().trim();
        System.out.print("  To account  : ");
        String to = sc.nextLine().trim();
        System.out.print("  Amount (₹)  : ");
        long amt = safeParseLong(sc.nextLine().trim());

        try {
            svc.transfer(from, to, amt);
            return "✔ Transferred ₹" + amt + " from " + from + " to " + to;
        } catch (BankException e) {
            return "✘ " + e.getMessage();
        }
    }

    private static String handleStatement(Scanner sc, BankService svc) {
        System.out.print("  Account No. : ");
        String acc = sc.nextLine().trim();
        try {
            return svc.statement(acc);
        } catch (BankException e) {
            return "✘ " + e.getMessage();
        }
    }

    private static String handleListAccounts(BankService svc) {
        List<Account> list = svc.listAccounts();
        if (list.isEmpty()) return "  No accounts found.";
        StringBuilder sb = new StringBuilder("  Accounts:\n");
        for (Account a : list) sb.append("  ").append(a).append("\n");
        return sb.toString();
    }

    private static String handleSearch(Scanner sc, BankService svc) {
        System.out.print("  Owner name (search): ");
        String query = sc.nextLine().trim();
        List<Account> results = svc.searchByOwner(query);
        if (results.isEmpty()) return "  No accounts found for: " + query;
        StringBuilder sb = new StringBuilder("  Results:\n");
        for (Account a : results) sb.append("  ").append(a).append("\n");
        return sb.toString();
    }

    private static String handleReport() {
        try {
            ReportGenerator.generate(LOGS_DIR, REPORT_FILE);
            return "✔ Report generated at " + REPORT_FILE;
        } catch (IOException e) {
            return "✘ Report failed: " + e.getMessage();
        }
    }

    private static long safeParseLong(String s) {
        try { return Long.parseLong(s); }
        catch (NumberFormatException e) { return 0L; }
    }

    // ===================================================================
    // Lab demo methods (printed once at startup)
    // ===================================================================

    // ---------------------------------------------------------------
    // Labs 02 & 03
    // ---------------------------------------------------------------
    private static void runLab02to03Demos() {
        separator("LAB 02 – Encapsulation  |  LAB 03 – Object Methods");

        // Lab 02 – Account array
        Account[] accounts = {
            new SavingsAccount("Riya",   5000, 500),
            new CurrentAccount("Arjun",  3000, 5000),
            new FixedDepositAccount("Priya", 20000)
        };

        // deposit / withdraw demo
        try {
            accounts[0].deposit(2000);                   // balance → 7000
            boolean w1 = accounts[0].withdraw(3000);     // balance → 4000 (true)
            boolean w2 = accounts[0].withdraw(10000);    // fails (false) – insufficient
            System.out.println("Riya withdraw ₹3000: " + w1 + "  | balance: ₹" + accounts[0].getBalance());
            System.out.println("Riya withdraw ₹10000: " + w2 + " | balance: ₹" + accounts[0].getBalance());
        } catch (InvalidAmountException | InsufficientFundsException e) {
            System.out.println("Expected: " + e.getMessage());
        }

        // Lab 03 – toString, equals, instanceof
        System.out.println("\n-- toString --");
        for (Account a : accounts) System.out.println(a);

        System.out.println("\n-- equals (same account number → true) --");
        Account a1 = accounts[0];
        // Create a second reference; equals is by accountNumber
        System.out.println("a1.equals(a1): " + a1.equals(a1));

        System.out.println("\n-- instanceof --");
        for (Account a : accounts) {
            String type = switch (a) {
                case SavingsAccount sa  -> "SavingsAccount (minBal ₹" + sa.getMinBalance() + ")";
                case CurrentAccount ca  -> "CurrentAccount (overdraft ₹" + ca.getOverdraftLimit() + ")";
                case FixedDepositAccount fd -> "FixedDepositAccount (matures " + fd.getMaturityDate() + ")";
                default                 -> "Unknown account type";
            };
            System.out.println("  " + a.getAccountNumber() + " → " + type);
        }

        // Lab 03 – Customer with nested Address
        Customer.Address addr = new Customer.Address("12, MG Road", "Ahmedabad", "380001");
        Customer customer = new Customer("Riya Shah", "riya@example.com", "9876543210", addr);
        System.out.println("\n-- Customer with Address --");
        System.out.println(customer + "  |  " + customer.getAddress());

        // Lab 03 – clone
        Customer clone = customer.clone();
        System.out.println("Cloned: " + clone);
    }

    // ---------------------------------------------------------------
    // Lab 04
    // ---------------------------------------------------------------
    private static void runLab04Demos() {
        separator("LAB 04 – Strings, Regex, CommandParser");

        // Validator
        System.out.println("isValidMobile(\"9876543210\") = " + isValidMobile("9876543210"));
        System.out.println("isValidMobile(\"12345\")      = " + isValidMobile("12345"));
        System.out.println("isValidEmail(\"abc@xyz.com\") = " + isValidEmail("abc@xyz.com"));
        System.out.println("isValidPan(\"ABCDE1234F\")    = " + isValidPan("ABCDE1234F"));
        System.out.println("isValidIfsc(\"SBIN0001234\")  = " + isValidIfsc("SBIN0001234"));
        System.out.println("isValidAmount(\"500\")        = " + isValidAmount("500"));
        System.out.println("isValidAmount(\"-50\")        = " + isValidAmount("-50"));

        // CommandParser
        try {
            Command cmd = CommandParser.parse("DEPOSIT AC0001 500");
            System.out.println("\nParsed command: type=" + cmd.type()
                    + "  account=" + cmd.accountNumber()
                    + "  amount=" + cmd.amount());
        } catch (IllegalArgumentException e) {
            System.out.println("Parse error: " + e.getMessage());
        }

        // Bad parse – wrong token count
        try {
            CommandParser.parse("DEPOSIT AC0001");
        } catch (IllegalArgumentException e) {
            System.out.println("Expected parse error: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Lab 05
    // ---------------------------------------------------------------
    private static void runLab05Demos() {
        separator("LAB 05 – Inheritance & Polymorphism");

        Account[] pool = {
            new SavingsAccount("Leena", 10000),
            new CurrentAccount("Dev",   5000),
            new FixedDepositAccount("Mona", 50000)
        };

        System.out.println("Run-time polymorphism – interestRate():");
        for (Account a : pool) {
            System.out.printf("  %-10s → %.1f%%  yearly interest ₹%.2f%n",
                    a.getOwnerName(), a.interestRate(), a.yearlyInterest());
        }
    }

    // ---------------------------------------------------------------
    // Lab 06
    // ---------------------------------------------------------------
    private static void runLab06Demos() {
        separator("LAB 06 – Interfaces, Lambda, Functional Interface");

        SavingsAccount sa = new SavingsAccount("Kiran", 8000, 1000);

        // Anonymous class
        WithdrawRule weekdayOnly = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                int day = java.time.LocalDate.now().getDayOfWeek().getValue();
                return day >= 1 && day <= 5; // Mon-Fri
            }
        };

        // Lambda expression
        WithdrawRule halfBalanceMax = (account, amount) -> amount <= account.getBalance() / 2;

        System.out.println("Anonymous class rule (weekdays): " + weekdayOnly.allow(sa, 500));
        System.out.println("Lambda rule (≤ half balance)   : " + halfBalanceMax.allow(sa, 3000));

        // instanceof Premium marker
        System.out.println("SavingsAccount instanceof Premium: " + (sa instanceof Premium));
    }

    // ---------------------------------------------------------------
    // Lab 07
    // ---------------------------------------------------------------
    private static void runLab07Demos() {
        separator("LAB 07 – Annotations & Reflection");

        // Valid account – no errors
        SavingsAccount valid = new SavingsAccount("Test", 1000);
        String[] errors = AnnotationValidator.validate(valid);
        System.out.println("Valid account errors: " + errors.length + " (expected 0)");

        // Invalid account (negative balance via reflection trick — we test the validator logic
        // by creating a special subclass just for this demo)
        System.out.println("(Negative-balance accounts can't be created through normal API –");
        System.out.println(" deposit() and constructor both guard against it. The @Positive");
        System.out.println(" annotation documents the invariant and AnnotationValidator detects violations.)");

        // Inspect @Id field
        System.out.println("\n@Audit methods in BankService:");
        for (java.lang.reflect.Method m : BankService.class.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Audit.class)) {
                System.out.printf("  %s → %s%n", m.getName(), m.getAnnotation(Audit.class).description());
            }
        }
    }

    // ---------------------------------------------------------------
    // Lab 08
    // ---------------------------------------------------------------
    private static void runLab08Demos(BankService svc) {
        separator("LAB 08 – Custom Exceptions");

        // Create a test account via service for exception demos
        Account testAcc = null;
        try {
            testAcc = svc.openSavingsAccount("ExTest", "9000000001", 1000);
        } catch (BankException ignored) {}

        if (testAcc == null) return;

        String accNo = testAcc.getAccountNumber();

        // Insufficient funds
        try {
            svc.withdraw(accNo, 5000);
        } catch (InsufficientFundsException e) {
            System.out.println("InsufficientFundsException: " + e.getMessage()
                    + "  shortfall=₹" + e.getShortfall());
        } catch (BankException e) {
            System.out.println("BankException: " + e.getMessage());
        }

        // Invalid amount
        try {
            svc.deposit(accNo, -100);
        } catch (InvalidAmountException e) {
            System.out.println("InvalidAmountException: " + e.getMessage());
        } catch (BankException e) {
            System.out.println("BankException: " + e.getMessage());
        }

        // try-with-resources (Lab 08) – AutoCloseable demo
        try (AutoCloseable resource = () -> System.out.println("[AutoCloseable] Resource released in finally.")) {
            System.out.println("[AutoCloseable] Resource opened.");
            svc.deposit(accNo, 200);
            System.out.println("[AutoCloseable] Balance after deposit: ₹" + testAcc.getBalance());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Lab 09
    // ---------------------------------------------------------------
    private static void runLab09Demos() {
        separator("LAB 09 – Threads & Race Condition");

        SavingsAccount shared = new SavingsAccount("ThreadTest", 0, 0);

        // 10 threads, each deposits ₹1 one thousand times → expected ₹10 000
        int threadCount = 10;
        int depositsEach = 1000;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new AccountWorker(shared, depositsEach, 1));
            threads[i].start();
        }

        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        System.out.println("Expected balance : ₹" + (threadCount * depositsEach));
        System.out.println("Actual  balance  : ₹" + shared.getBalance()
                + (shared.getBalance() == threadCount * depositsEach
                    ? "  ✔ Thread-safe!" : "  ✘ Race condition!"));
    }

    // ---------------------------------------------------------------
    // Lab 10
    // ---------------------------------------------------------------
    private static void runLab10Demos() {
        separator("LAB 10 – Thread Pool, Producer-Consumer, Deadlock Fix");

        // Thread pool
        TransactionProcessor tp = new TransactionProcessor(4);
        SavingsAccount poolAcc = new SavingsAccount("PoolTest", 0, 0);
        for (int i = 0; i < 20; i++) {
            final int n = i;
            tp.submit(() -> {
                try { poolAcc.deposit(100); }
                catch (InvalidAmountException ignored) {}
            });
        }
        tp.stop();
        System.out.println("Pool test balance (20 × ₹100): ₹" + poolAcc.getBalance());

        // Deadlock prevention – always lock smaller account number first
        SavingsAccount accA = new SavingsAccount("Alice", 5000, 0);
        SavingsAccount accB = new SavingsAccount("Bob",   5000, 0);

        Runnable transfer1 = safeTransfer(accA, accB, 100);
        Runnable transfer2 = safeTransfer(accB, accA, 100);

        Thread t1 = new Thread(transfer1, "T1-A-to-B");
        Thread t2 = new Thread(transfer2, "T2-B-to-A");
        t1.start(); t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        System.out.println("Deadlock-free transfers done."
                + "  A=₹" + accA.getBalance() + "  B=₹" + accB.getBalance());
    }

    /** Transfers amount between two accounts using consistent lock ordering to prevent deadlock. */
    private static Runnable safeTransfer(Account from, Account to, long amount) {
        return () -> {
            // Always lock the account with the lexicographically smaller number first
            Account first  = from.compareTo(to) <= 0 ? from : to;
            Account second = first == from ? to : from;
            synchronized (first) {
                synchronized (second) {
                    try {
                        from.transfer(to, amount);
                    } catch (BankException e) {
                        System.err.println("Transfer error: " + e.getMessage());
                    }
                }
            }
        };
    }

    // ---------------------------------------------------------------
    // Lab 11
    // ---------------------------------------------------------------
    private static void runLab11Demos() {
        separator("LAB 11 – Serialization & NIO");

        Path dataFile = Paths.get("data", "demo_accounts.dat");
        Account[] original = {
            new SavingsAccount("Neha",  8000, 500),
            new CurrentAccount("Raj",   4000, 2000),
            new FixedDepositAccount("Sita", 15000)
        };

        try {
            StatePersister.save(original, dataFile);
            Account[] loaded = StatePersister.load(dataFile);
            System.out.println("Reloaded accounts:");
            for (Account a : loaded) System.out.println("  " + a);

            // Log a few transactions
            Path log = Paths.get("logs", "demo.log");
            TransactionLog.append(log, "DEPOSIT " + loaded[0].getAccountNumber() + " 500");
            TransactionLog.append(log, "WITHDRAW " + loaded[1].getAccountNumber() + " 200");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("I/O error: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Lab 12
    // ---------------------------------------------------------------
    private static void runLab12Demos() {
        separator("LAB 12 – Collections Framework");

        AccountStore store = new AccountStore();
        store.add(new SavingsAccount("Aisha",  5000));
        store.add(new SavingsAccount("Brij",   2000));
        store.add(new SavingsAccount("Chetan", 9000));

        System.out.println("All accounts (insertion order):");
        for (Account a : store.getAll()) System.out.println("  " + a);

        System.out.println("\nSorted by balance (descending):");
        for (Account a : store.sortedByBalance()) System.out.println("  " + a);

        System.out.println("\nBalance > ₹3000:");
        for (Account a : store.withBalanceAbove(3000)) System.out.println("  " + a);
    }

    // ---------------------------------------------------------------
    // Lab 13
    // ---------------------------------------------------------------
    private static void runLab13Demo() {
        separator("LAB 13 – Generic Repository");

        Repository<Account> repo = new Repository<>();
        Account sa = new SavingsAccount("GenUser1", 7000);
        Account ca = new CurrentAccount("GenUser2", 3000);
        repo.saveAll(sa, ca);

        System.out.println("findAll(): " + repo.findAll().size() + " account(s)");
        Account found = repo.findById(sa.getAccountNumber());
        System.out.println("findById: " + found);

        Long[] balances = {7000L, 3000L, 9000L};
        System.out.printf("average([7000, 3000, 9000]): %.2f%n",
                Repository.average(balances));
    }

    // ---------------------------------------------------------------
    // Separator helper
    // ---------------------------------------------------------------
    private static void separator(String label) {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("  " + label);
        System.out.println("─".repeat(55));
    }
}
