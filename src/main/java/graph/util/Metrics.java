package graph.util;

/**
 * Metrics interface for tracking algorithm performance.
 * Counts operations and records execution time.
 */
public interface Metrics {
    /**
     * Increment a counter for the given operation.
     * @param operationName name of the operation to count
     */
    void incrementCounter(String operationName);

    /**
     * Add a value to a counter.
     * @param operationName name of the operation
     * @param value value to add
     */
    void addToCounter(String operationName, int value);

    /**
     * Get the value of a counter.
     * @param operationName name of the operation
     * @return counter value
     */
    int getCounter(String operationName);

    /**
     * Set execution time in nanoseconds.
     * @param nanos time in nanoseconds
     */
    void setExecutionTimeNanos(long nanos);

    /**
     * Get execution time in nanoseconds.
     * @return time in nanoseconds
     */
    long getExecutionTimeNanos();

    /**
     * Get execution time in milliseconds.
     * @return time in milliseconds
     */
    double getExecutionTimeMillis();

    /**
     * Reset all metrics.
     */
    void reset();

    /**
     * Get a summary string of all metrics.
     * @return formatted string
     */
    String getSummary();
}

