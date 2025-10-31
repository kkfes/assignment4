package graph.topo;

import graph.util.Graph;
import graph.util.Metrics;
import java.util.*;

/**
 * Topological sorting using Kahn's algorithm (BFS-based).
 * Time complexity: O(V + E)
 */
public class TopoKahn {
    private final Graph graph;
    private final Metrics metrics;
    private List<Integer> topoOrder;

    public TopoKahn(Graph graph, Metrics metrics) {
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

        // Calculate in-degrees
        int[] inDegree = new int[n];
        for (int i = 0; i < n; i++) {
            for (Graph.Edge edge : graph.getAdjacentEdges(i)) {
                inDegree[edge.to]++;
            }
        }

        // Queue of vertices with in-degree 0
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        // Process vertices
        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            metrics.incrementCounter("pops");
            topoOrder.add(vertex);

            for (Graph.Edge edge : graph.getAdjacentEdges(vertex)) {
                metrics.incrementCounter("edges_examined");
                inDegree[edge.to]--;
                if (inDegree[edge.to] == 0) {
                    queue.offer(edge.to);
                    metrics.incrementCounter("pushes");
                }
            }
        }

        long endTime = System.nanoTime();
        metrics.setExecutionTimeNanos(endTime - startTime);

        // Check for cycles
        if (topoOrder.size() != n) {
            return new ArrayList<>(); // Cycle detected
        }

        return topoOrder;
    }

    /**
     * Check if graph has cycle.
     * @return true if cycle exists
     */
    public boolean hasCycle() {
        return topoOrder.size() != graph.getVertexCount();
    }

    /**
     * Get topological order.
     * @return list of vertices in topological order
     */
    public List<Integer> getTopologicalOrder() {
        return new ArrayList<>(topoOrder);
    }
}

