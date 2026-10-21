package service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Lab 10 – Manages a fixed-size thread pool to process batches of
 * Runnable tasks safely and efficiently.
 *
 * <p>Usage:</p>
 * <pre>
 *   TransactionProcessor tp = new TransactionProcessor(4);
 *   tp.submit(() -&gt; account.deposit(100));
 *   tp.stop();
 * </pre>
 */
public class TransactionProcessor {

    private final ExecutorService pool;

    /**
     * Creates a processor backed by a fixed thread pool of {@code poolSize} threads.
     */
    public TransactionProcessor(int poolSize) {
        this.pool = Executors.newFixedThreadPool(poolSize);
    }

    /**
     * Submits a task to the pool for asynchronous execution.
     */
    public void submit(Runnable task) {
        pool.execute(task);
    }

    /**
     * Shuts down the pool and waits up to 60 seconds for all tasks to finish.
     */
    public void stop() {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                System.err.println("[TransactionProcessor] Tasks did not finish in time; forcing shutdown.");
                pool.shutdownNow();
            } else {
                System.out.println("[TransactionProcessor] All tasks completed.");
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
