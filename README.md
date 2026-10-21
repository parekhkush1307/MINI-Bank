# MiniBank — OOP Java Console Banking Application

A semester-long console banking application demonstrating core Java OOP concepts across 14 labs.

---

## Project Structure

```
MiniBank/
├── src/
│   ├── MiniBank.java                  # Lab 01–14: main class, BankInfo record, MenuOption enum
│   ├── model/
│   │   ├── Account.java               # Lab 02–12: abstract base, encapsulation, Serializable
│   │   ├── SavingsAccount.java        # Lab 05: 4% interest, min-balance withdrawal rule
│   │   ├── CurrentAccount.java        # Lab 05: 0% interest, overdraft-aware withdrawal
│   │   ├── FixedDepositAccount.java   # Lab 05: 7% interest, locked until maturity
│   │   ├── Customer.java              # Lab 02/03: encapsulation, nested Address, Cloneable
│   │   ├── Transactable.java          # Lab 06: deposit/withdraw interface
│   │   ├── InterestBearing.java       # Lab 06: default yearlyInterest() / monthlyInterest()
│   │   ├── WithdrawRule.java          # Lab 06: @FunctionalInterface for custom rules
│   │   ├── Premium.java               # Lab 06: marker interface
│   │   └── annotation/
│   │       ├── Id.java                # Lab 07: @Id – marks the primary key field
│   │       ├── Positive.java          # Lab 07: @Positive – value must be > 0
│   │       ├── MaxLength.java         # Lab 07: @MaxLength – max String length
│   │       └── Audit.java             # Lab 14: @Audit – marks service methods for logging
│   ├── exception/
│   │   ├── BankException.java         # Lab 08: checked base exception
│   │   ├── InsufficientFundsException.java  # Lab 08: with shortfall field
│   │   ├── AccountNotFoundException.java    # Lab 08
│   │   ├── InvalidAmountException.java      # Lab 08
│   │   └── DailyLimitExceededException.java # Lab 08 Supplementary
│   ├── util/
│   │   ├── Validator.java             # Lab 04: regex validators (mobile, email, PAN, IFSC)
│   │   ├── TransactionType.java       # Lab 04: enum DEPOSIT / WITHDRAW / TRANSFER
│   │   ├── Command.java               # Lab 04: record (type, accountNumber, amount)
│   │   ├── CommandParser.java         # Lab 04: parses "DEPOSIT AC0001 500"
│   │   ├── StatementFormatter.java    # Lab 04: StringBuilder account statement
│   │   ├── AnnotationValidator.java   # Lab 07: reflective @Positive / @MaxLength checker
│   │   ├── StatePersister.java        # Lab 11: ObjectOutputStream save / load
│   │   ├── TransactionLog.java        # Lab 11: NIO append-only log
│   │   └── ReportGenerator.java       # Lab 11: NIO DirectoryStream report
│   └── service/
│       ├── Repository.java            # Lab 13: generic Repository<T> with @Id reflection
│       ├── AccountStore.java          # Lab 12: ConcurrentHashMap, sorting, history
│       ├── AccountWorker.java         # Lab 09: Runnable for concurrent deposit demos
│       ├── TransactionProcessor.java  # Lab 10: ExecutorService thread pool
│       └── BankService.java           # Lab 14: service layer, @Audit methods
├── test/
│   └── MiniBankTest.java              # Lab 14: JUnit 5 tests
├── data/                              # Generated at runtime (accounts.dat, report.txt)
├── logs/                              # Generated at runtime (transactions.log)
├── lib/                               # Place JUnit jar here for tests
├── build.sh                           # Build / run / test script
└── README.md
```

---

## Prerequisites

- **JDK 17 or later** — [Download Temurin](https://adoptium.net/)
- **JUnit 5** (for tests only) — download `junit-platform-console-standalone-*.jar` and place in `lib/`

---

## How to Compile and Run

### Option A – Shell script (macOS / Linux)

```bash
chmod +x build.sh

# Compile and run interactively
./build.sh

# Build a runnable JAR then execute it
./build.sh jar
java -jar minibank.jar

# Run JUnit tests (requires JUnit jar in lib/)
./build.sh test

# Clean build outputs
./build.sh clean
```

### Option B – Manual commands

```bash
# 1. Compile all sources
mkdir -p out
javac -d out --source-path src $(find src -name "*.java")

# 2. Run
java -cp out MiniBank

# 3. Build JAR
echo "Main-Class: MiniBank" > manifest.txt
jar cfm minibank.jar manifest.txt -C out .
java -jar minibank.jar
```

---

## Expected Interaction

```
╔══════════════════════════════════════════╗
║  MiniBank                                ║
║  Ahmedabad Main Branch                   ║
╚══════════════════════════════════════════╝

┌─────────────────────────────────────┐
│           MAIN MENU                 │
├─────────────────────────────────────┤
│   1. OPEN ACCOUNT                   │
│   2. DEPOSIT                        │
│   3. WITHDRAW                       │
│   4. TRANSFER                       │
│   5. VIEW STATEMENT                 │
│   6. LIST ACCOUNTS                  │
│   7. SEARCH ACCOUNT                 │
│   8. RUN REPORT                     │
│   9. WORKING HOURS                  │
│  10. EXIT                           │
└─────────────────────────────────────┘
  Enter choice: 2
  Account No. : AC0001
  Amount (₹)  : 5000
✔ Deposited ₹5000 into AC0001

  Enter choice: 10
Goodbye! Thank you for banking with MiniBank.
```

---

## Lab Coverage Summary

| Lab | Topic | Key Classes |
|-----|-------|-------------|
| 01 | Menu shell | `BankInfo` record, `MenuOption` enum, switch expression |
| 02 | Encapsulation | `Account`, `Customer`, array of objects |
| 03 | Object methods | `toString`, `equals`, `hashCode`, `clone`, nested `Address` |
| 04 | Strings & regex | `Validator`, `Command` record, `CommandParser`, `StringBuilder` |
| 05 | Inheritance | `SavingsAccount`, `CurrentAccount`, `FixedDepositAccount`, polymorphism |
| 06 | Interfaces | `Transactable`, `InterestBearing`, `WithdrawRule` (lambda), `Premium` |
| 07 | Annotations | `@Id`, `@Positive`, `@MaxLength`, `@Audit`, `AnnotationValidator` |
| 08 | Exceptions | `BankException` hierarchy, try-catch-finally, try-with-resources |
| 09 | Threads | `AccountWorker`, race condition, `synchronized` fix |
| 10 | Thread pool | `TransactionProcessor`, producer-consumer, deadlock prevention |
| 11 | I/O & NIO | `StatePersister`, `TransactionLog`, `ReportGenerator` |
| 12 | Collections | `AccountStore`, `ConcurrentHashMap`, `Comparator`, `Comparable` |
| 13 | Generics | `Repository<T>`, bounded type parameter, `@SafeVarargs` |
| 14 | Capstone | `BankService`, menu wiring, JUnit tests, JAR packaging |

---

## Key Design Decisions

- **`balance` has no public setter** — only `deposit()` / `withdraw()` can modify it, enforcing encapsulation.
- **`accountNumber` is `final`** — an account's identity never changes after creation.
- **`synchronized` on deposit/withdraw** — protects the `balance` field from concurrent corruption.
- **Generic `Repository<T>`** — finds the map key via `@Id` reflection, making it reusable for any entity.
- **Consistent lock ordering** — when transferring between two accounts, always lock the one with the lower `accountNumber` first, preventing deadlock.

---

## Git Tags

| Tag | Covers |
|-----|--------|
| `lab-01` | Menu shell |
| `lab-02` | Account & Customer |
| `lab-03` | Object methods |
| `lab-04` | Strings & regex |
| `lab-05` | Inheritance |
| `lab-06` | Interfaces & JAR |
| `lab-07` | Annotations |
| `lab-08` | Exceptions *(Milestone 1)* |
| `lab-09` | Threads |
| `lab-10` | Thread pool |
| `lab-11` | I/O & NIO |
| `lab-12` | Collections *(Milestone 2)* |
| `lab-13` | Generics |
| `lab-14` | Capstone *(Milestone 3)* |
