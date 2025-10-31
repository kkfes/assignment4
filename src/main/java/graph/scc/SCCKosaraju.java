package graph.scc;

import graph.util.Graph;
import graph.util.Metrics;
import java.util.*;

/**
 * Kosaraju's algorithm for finding Strongly Connected Components (SCC).
 * Time complexity: O(V + E)
 */
public class SCCKosaraju {
    private final Graph graph;
    private final Metrics metrics;
    private List<List<Integer>> components;
    private boolean[] visited;
    private Stack<Integer> stack;

    public SCCKosaraju(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
        this.components = new ArrayList<>();
    }

    /**
     * Find all strongly connected components.
     * @return list of SCCs, each SCC is a list of vertices
     */
    public List<List<Integer>> findSCCs() {
        long startTime = System.nanoTime();
        components.clear();
        int n = graph.getVertexCount();
        visited = new boolean[n];
        stack = new Stack<>();

        // Step 1: Fill stack with vertices in order of finish time
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsFirst(i);
            }
        }

        // Step 2: Create transpose graph
        Graph transpose = graph.getTranspose();

        // Step 3: DFS on transpose in reverse order of finish times
        Arrays.fill(visited, false);
        while (!stack.isEmpty()) {
            int vertex = stack.pop();
            if (!visited[vertex]) {
                List<Integer> component = new ArrayList<>();
                dfsSecond(vertex, transpose, component);
                components.add(component);
            }
        }

        long endTime = System.nanoTime();
        metrics.setExecutionTimeNanos(endTime - startTime);

        return components;
    }

    /**
     * First DFS pass - fill stack with finish order.
     */
    private void dfsFirst(int vertex) {
        metrics.incrementCounter("dfs_visits");
        visited[vertex] = true;
        for (Graph.Edge edge : graph.getAdjacentEdges(vertex)) {
            metrics.incrementCounter("edges_examined");
            if (!visited[edge.to]) {
                dfsFirst(edge.to);
            }
        }
        stack.push(vertex);
    }

    /**
     * Second DFS pass - collect component vertices.
     */
    private void dfsSecond(int vertex, Graph transpose, List<Integer> component) {
        metrics.incrementCounter("dfs_visits");
        visited[vertex] = true;
        component.add(vertex);
        for (Graph.Edge edge : transpose.getAdjacentEdges(vertex)) {
            metrics.incrementCounter("edges_examined");
            if (!visited[edge.to]) {
                dfsSecond(edge.to, transpose, component);
            }
        }
    }

    /**
     * Get the list of SCCs.
     * @return list of components
     */
    public List<List<Integer>> getComponents() {
        return components;
    }

    /**
     * Get sizes of each SCC.
     * @return list of sizes
     */
    public List<Integer> getComponentSizes() {
        return components.stream()
                .map(List::size)
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    /**
     * Get mapping from vertex to component id.
     * @return array where result[v] = component id
     */
    public int[] getVertexToComponentMap() {
        int[] map = new int[graph.getVertexCount()];
        for (int i = 0; i < components.size(); i++) {
            for (int vertex : components.get(i)) {
                map[vertex] = i;
            }
        }
        return map;
    }
}

