package util;

import model.Account;

/**
 * Lab 04 – Builds a formatted multi-line account statement using StringBuilder.
 */
public final class StatementFormatter {

    private StatementFormatter() { /* utility class */ }

    /**
     * Assembles a multi-line account statement for the given account.
     *
     * @param account the account to describe
     * @return a formatted statement string
     */
    public static String buildStatement(Account account) {
        StringBuilder sb = new StringBuilder();
        String line = "=".repeat(55);

        sb.append(line).append("\n");
        sb.append("          ACCOUNT STATEMENT\n");
        sb.append(line).append("\n");
        sb.append(String.format("  Account No   : %s%n", account.getAccountNumber()));
        sb.append(String.format("  Owner        : %s%n", account.getOwnerName()));
        sb.append(String.format("  Balance      : \u20B9%,d%n", account.getBalance()));
        sb.append(String.format("  Status       : %s%n", account.isActive() ? "Active" : "Inactive"));
        sb.append(String.format("  Interest     : %.2f%% p.a.%n", account.interestRate()));
        sb.append(String.format("  Yearly Int.  : \u20B9%.2f%n", account.yearlyInterest()));
        sb.append(String.format("  Monthly Int. : \u20B9%.2f%n", account.monthlyInterest()));
        sb.append(line).append("\n");

        return sb.toString();
    }
}
