package model;

/**
 * Lab 06 – Functional interface representing a custom withdrawal rule.
 * Can be implemented as an anonymous class or a lambda expression.
 *
 * <p>Example (lambda):</p>
 * <pre>
 *   WithdrawRule noWeekendWithdraw = (acc, amt) -&gt; {
 *       int day = LocalDate.now().getDayOfWeek().getValue();
 *       return day != 6 &amp;&amp; day != 7;
 *   };
 * </pre>
 */
@FunctionalInterface
public interface WithdrawRule {

    /**
     * Returns {@code true} if the withdrawal of {@code amount} from
     * {@code account} is permitted under this rule.
     */
    boolean allow(Account account, long amount);
}
