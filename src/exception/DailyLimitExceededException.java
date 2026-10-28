package exception;


public class DailyLimitExceededException extends BankException {

    private final long limit;
    private final long requested;

    public DailyLimitExceededException(long requested, long limit) {
        super("Daily limit exceeded: requested " + requested + ", limit is " + limit);
        this.requested = requested;
        this.limit = limit;
    }

    public long getLimit() { return limit; }
    public long getRequested() { return requested; }
}
