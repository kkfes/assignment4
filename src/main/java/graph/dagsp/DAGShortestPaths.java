package graph.dagsp;

import graph.util.Graph;
import graph.util.Metrics;
import graph.topo.TopoDFS;
import java.util.*;

/**
 * Shortest Paths in DAG using dynamic programming over topological order.
 * Supports both shortest and longest paths.
 * Time complexity: O(V + E)
 *
 * Note: Edge weights are used to represent costs/durations in the task graph.
 * For this implementation, we use edge weights to denote the cost of completing
 * the destination task (node duration model).
 */
public class DAGShortestPaths {
    private final Graph graph;
    private final Metrics metrics;
    private final double[] distances;
    private final int[] parent;
    private int sourceVertex;
    private List<Integer> topoOrder;

    public DAGShortestPaths(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
        this.distances = new double[graph.getVertexCount()];
        this.parent = new int[graph.getVertexCount()];
    }

    /**
     * Compute shortest paths from source vertex to all others.
     * @param source source vertex id
     * @return array of shortest distances (POSITIVE_INFINITY if unreachable)
     */
    public double[] computeShortestPaths(int source) {
        long startTime = System.nanoTime();

        if (!graph.isValidVertex(source)) {
            return distances;
        }

        this.sourceVertex = source;

        // Get topological order
        TopoDFS topoDFS = new TopoDFS(graph, new graph.util.MetricsImpl());
        topoOrder = topoDFS.computeTopologicalOrder();

        if (topoOrder.isEmpty()) {
            // Graph has cycle, cannot compute shortest paths
            Arrays.fill(distances, Double.POSITIVE_INFINITY);
            return distances;
        }

        // Initialize distances
        Arrays.fill(distances, Double.POSITIVE_INFINITY);
        distances[source] = 0;
        Arrays.fill(parent, -1);

        // Relax edges in topological order
        for (int u : topoOrder) {
            if (distances[u] != Double.POSITIVE_INFINITY) {
                for (Graph.Edge edge : graph.getAdjacentEdges(u)) {
                    metrics.incrementCounter("relaxations");
                    if (distances[u] + edge.weight < distances[edge.to]) {
                        distances[edge.to] = distances[u] + edge.weight;
                        parent[edge.to] = u;
                    }
                }
            }
        }

        long endTime = System.nanoTime();
        metrics.setExecutionTimeNanos(endTime - startTime);

        return distances;
    }

    /**
     * Compute longest paths from source vertex (critical path).
     * Uses negation trick: find shortest path in graph with negated weights.
     * @param source source vertex id
     * @return array of longest distances (NEGATIVE_INFINITY if unreachable)
     */
    public double[] computeLongestPaths(int source) {
        long startTime = System.nanoTime();

        if (!graph.isValidVertex(source)) {
            return distances;
        }

        this.sourceVertex = source;

        // Get topological order
        TopoDFS topoDFS = new TopoDFS(graph, new graph.util.MetricsImpl());
        topoOrder = topoDFS.computeTopologicalOrder();

        if (topoOrder.isEmpty()) {
            Arrays.fill(distances, Double.NEGATIVE_INFINITY);
            return distances;
        }

        // Initialize distances for longest path (maximize)
        Arrays.fill(distances, Double.NEGATIVE_INFINITY);
        distances[source] = 0;
        Arrays.fill(parent, -1);

        // Relax edges in topological order (for longest paths)
        for (int u : topoOrder) {
            if (distances[u] != Double.NEGATIVE_INFINITY) {
                for (Graph.Edge edge : graph.getAdjacentEdges(u)) {
                    metrics.incrementCounter("relaxations");
                    if (distances[u] + edge.weight > distances[edge.to]) {
                        distances[edge.to] = distances[u] + edge.weight;
                        parent[edge.to] = u;
                    }
                }
            }
        }

        long endTime = System.nanoTime();
        metrics.setExecutionTimeNanos(endTime - startTime);

        return distances;
    }

    /**
     * Get distance to target vertex.
     * @param target target vertex id
     * @return distance (INFINITY if unreachable)
     */
    public double getDistance(int target) {
        if (graph.isValidVertex(target)) {
            return distances[target];
        }
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Reconstruct path from source to target.
     * @param target target vertex id
     * @return list of vertices on path (empty if no path exists)
     */
    public List<Integer> reconstructPath(int target) {
        List<Integer> path = new ArrayList<>();

        if (!graph.isValidVertex(target) || distances[target] == Double.POSITIVE_INFINITY) {
            return path;
        }

        int current = target;
        while (current != -1) {
            path.add(0, current);
            current = parent[current];
        }

        return path;
    }

    /**
     * Find critical path (longest path) from source.
     * @param source source vertex id
     * @return pair of [path length, path as list of vertices]
     */
    public Map.Entry<Double, List<Integer>> findCriticalPath(int source) {
        computeLongestPaths(source);

        double maxDist = Double.NEGATIVE_INFINITY;
        int endVertex = -1;

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (distances[i] > maxDist && distances[i] != Double.NEGATIVE_INFINITY) {
                maxDist = distances[i];
                endVertex = i;
            }
        }

        List<Integer> path = new ArrayList<>();
        if (endVertex != -1) {
            path = reconstructPath(endVertex);
        }

        return new AbstractMap.SimpleEntry<>(maxDist, path);
    }

    /**
     * Get topological order used.
     * @return list of vertices in topological order
     */
    public List<Integer> getTopologicalOrder() {
        return new ArrayList<>(topoOrder);
    }
}

