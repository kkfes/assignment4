package graph.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of Metrics interface.
 */
public class MetricsImpl implements Metrics {
    private final Map<String, Integer> counters = new HashMap<>();
    private long executionTimeNanos = 0;

    @Override
    public void incrementCounter(String operationName) {
        counters.put(operationName, counters.getOrDefault(operationName, 0) + 1);
    }

    @Override
    public void addToCounter(String operationName, int value) {
        counters.put(operationName, counters.getOrDefault(operationName, 0) + value);
    }

    @Override
    public int getCounter(String operationName) {
        return counters.getOrDefault(operationName, 0);
    }

    @Override
    public void setExecutionTimeNanos(long nanos) {
        this.executionTimeNanos = nanos;
    }

    @Override
    public long getExecutionTimeNanos() {
        return executionTimeNanos;
    }

    @Override
    public double getExecutionTimeMillis() {
        return executionTimeNanos / 1_000_000.0;
    }

    @Override
    public void reset() {
        counters.clear();
        executionTimeNanos = 0;
    }

    @Override
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Metrics Summary ===\n");
        for (Map.Entry<String, Integer> entry : counters.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        sb.append("Execution time: ").append(String.format("%.3f ms", getExecutionTimeMillis())).append("\n");
        return sb.toString();
    }
}

