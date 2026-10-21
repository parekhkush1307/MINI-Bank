package util;

/**
 * Lab 04 – Parses a command line such as "DEPOSIT AC0001 500" into a Command record.
 * Lab 04 Supplementary – reports an error when the line has the wrong number of parts.
 */
public final class CommandParser {

    private CommandParser() { /* utility class */ }

    /**
     * Parses a space-delimited line into a {@link Command}.
     *
     * <p>Expected format: {@code TYPE ACCOUNT_NUMBER AMOUNT}
     *
     * @param line the raw input line
     * @return a Command object with the parsed fields
     * @throws IllegalArgumentException if the line cannot be parsed
     */
    public static Command parse(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Command line is empty or null.");
        }

        String[] parts = line.trim().split("\\s+");

        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Invalid command format. Expected: TYPE ACCOUNT_NUMBER AMOUNT"
                    + " (got " + parts.length + " token(s))");
        }

        TransactionType type;
        try {
            type = TransactionType.valueOf(parts[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown transaction type: " + parts[0]);
        }

        String accountNumber = parts[1];

        if (!Validator.isValidAmount(parts[2])) {
            throw new IllegalArgumentException("Invalid amount: " + parts[2]);
        }

        long amount = Long.parseLong(parts[2]);

        return new Command(type, accountNumber, amount);
    }
}
