package model;


@FunctionalInterface
public interface WithdrawRule {

    /**
     * Returns {@code true} if the withdrawal of {@code amount} from
     * {@code account} is permitted under this rule.
     */
    boolean allow(Account account, long amount);
}
