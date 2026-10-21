package util;

/**
 * Lab 04 – Immutable record representing a parsed banking command.
 * A record auto-generates the constructor, getters and equals/hashCode/toString.
 *
 * @param type          the type of transaction (DEPOSIT / WITHDRAW / TRANSFER)
 * @param accountNumber the target account number
 * @param amount        the transaction amount in rupees
 */
public record Command(TransactionType type, String accountNumber, long amount) {
}
