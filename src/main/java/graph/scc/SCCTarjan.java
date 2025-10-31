package graph.scc;

import graph.util.Graph;
import graph.util.Metrics;
import java.util.*;

/**
 * Tarjan's algorithm for finding Strongly Connected Components (SCC).
 * Time complexity: O(V + E)
 */
public class SCCTarjan {
    private final Graph graph;
    private final Metrics metrics;
    private List<List<Integer>> components;
    private Stack<Integer> stack;
    private int[] ids;
    private int[] lowValues;
    private boolean[] onStack;
    private int idCounter;

    public SCCTarjan(Graph graph, Metrics metrics) {
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
        stack = new Stack<>();
        int n = graph.getVertexCount();
        ids = new int[n];
        lowValues = new int[n];
        onStack = new boolean[n];
        idCounter = 0;

        Arrays.fill(ids, -1);

        // Run DFS from all unvisited vertices
        for (int i = 0; i < n; i++) {
            if (ids[i] == -1) {
                dfs(i);
            }
        }

        long endTime = System.nanoTime();
        metrics.setExecutionTimeNanos(endTime - startTime);
        metrics.addToCounter("dfs_visits", idCounter);

        return components;
    }

    /**
     * DFS traversal for Tarjan's algorithm.
     */
    private void dfs(int at) {
        metrics.incrementCounter("dfs_visits");
        stack.push(at);
        onStack[at] = true;
        ids[at] = lowValues[at] = idCounter++;

        List<Graph.Edge> edges = graph.getAdjacentEdges(at);
        for (Graph.Edge edge : edges) {
            metrics.incrementCounter("edges_examined");
            int to = edge.to;
            if (ids[to] == -1) {
                dfs(to);
            }
            if (onStack[to]) {
                lowValues[at] = Math.min(lowValues[at], lowValues[to]);
            }
        }

        // Found SCC root
        if (ids[at] == lowValues[at]) {
            List<Integer> component = new ArrayList<>();
            while (true) {
                int node = stack.pop();
                onStack[node] = false;
                component.add(node);
                if (node == at) break;
            }
            components.add(component);
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

