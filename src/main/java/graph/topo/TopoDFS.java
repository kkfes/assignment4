package graph.topo;

import graph.util.Graph;
import graph.util.Metrics;
import java.util.*;

/**
 * Topological sorting using DFS-based approach.
 * Time complexity: O(V + E)
 */
public class TopoDFS {
    private final Graph graph;
    private final Metrics metrics;
    private List<Integer> topoOrder;
    private int[] color; // 0: white, 1: gray, 2: black
    private boolean hasCycle;

    private static final int WHITE = 0;
    private static final int GRAY = 1;
    private static final int BLACK = 2;

    public TopoDFS(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
        this.topoOrder = new ArrayList<>();
    }

    /**
     * Compute topological order.
     * @return list of vertices in topological order, or empty if cycle detected
     */
    public List<Integer> computeTopologicalOrder() {
        long startTime = System.nanoTime();
        topoOrder.clear();
        int n = graph.getVertexCount();
        color = new int[n];
        hasCycle = false;

        Stack<Integer> result = new Stack<>();

        for (int i = 0; i < n; i++) {
            if (color[i] == WHITE && !hasCycle) {
                dfsTopo(i, result);
            }
        }

        while (!result.isEmpty()) {
            topoOrder.add(result.pop());
        }

        long endTime = System.nanoTime();
        metrics.setExecutionTimeNanos(endTime - startTime);

        if (hasCycle) {
            return new ArrayList<>();
        }

        return topoOrder;
    }

    /**
     * DFS traversal for topological sort.
     */
    private void dfsTopo(int vertex, Stack<Integer> result) {
        metrics.incrementCounter("dfs_visits");
        color[vertex] = GRAY;

        for (Graph.Edge edge : graph.getAdjacentEdges(vertex)) {
            metrics.incrementCounter("edges_examined");
            if (color[edge.to] == GRAY) {
                hasCycle = true;
                return;
            }
            if (color[edge.to] == WHITE) {
                dfsTopo(edge.to, result);
            }
        }

        color[vertex] = BLACK;
        result.push(vertex);
    }

    /**
     * Check if graph has cycle.
     * @return true if cycle exists
     */
    public boolean hasCycle() {
        return hasCycle;
    }

    /**
     * Get topological order.
     * @return list of vertices in topological order
     */
    public List<Integer> getTopologicalOrder() {
        return new ArrayList<>(topoOrder);
    }
}

