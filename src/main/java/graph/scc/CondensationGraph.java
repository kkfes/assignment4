package graph.scc;

import graph.util.Graph;
import java.util.*;

/**
 * Condensation graph - DAG of SCCs.
 * Represents the structure after compressing all SCCs into single nodes.
 */
public class CondensationGraph {
    private final int componentCount;
    private final Graph graph;
    private final Map<Integer, Set<Integer>> adjacencyList;
    private final int[] vertexToComponent;
    private final List<List<Integer>> components;

    /**
     * Create condensation graph from SCCs.
     * @param sccComponents list of components (result from SCC algorithm)
     * @param originalGraph original directed graph
     */
    public CondensationGraph(List<List<Integer>> sccComponents, Graph originalGraph) {
        this.componentCount = sccComponents.size();
        this.components = sccComponents;
        this.adjacencyList = new HashMap<>();
        this.vertexToComponent = new int[originalGraph.getVertexCount()];

        // Initialize adjacency list
        for (int i = 0; i < componentCount; i++) {
            adjacencyList.put(i, new HashSet<>());
        }

        // Map vertices to components
        for (int i = 0; i < componentCount; i++) {
            for (int vertex : sccComponents.get(i)) {
                vertexToComponent[vertex] = i;
            }
        }

        // Build condensation graph
        buildCondensationGraph(originalGraph);
        this.graph = new Graph(componentCount);
        for (int i = 0; i < componentCount; i++) {
            for (int j : adjacencyList.get(i)) {
                graph.addEdge(i, j);
            }
        }
    }

    /**
     * Build the condensation graph edges.
     */
    private void buildCondensationGraph(Graph originalGraph) {
        for (int v = 0; v < originalGraph.getVertexCount(); v++) {
            int componentV = vertexToComponent[v];
            for (Graph.Edge edge : originalGraph.getAdjacentEdges(v)) {
                int componentU = vertexToComponent[edge.to];
                // Only add edge if it connects different components
                if (componentV != componentU) {
                    adjacencyList.get(componentV).add(componentU);
                }
            }
        }
    }

    /**
     * Get number of components.
     * @return component count
     */
    public int getComponentCount() {
        return componentCount;
    }

    /**
     * Get edges from component.
     * @param componentId component id
     * @return set of component ids this component points to
     */
    public Set<Integer> getAdjacentComponents(int componentId) {
        return new HashSet<>(adjacencyList.getOrDefault(componentId, new HashSet<>()));
    }

    /**
     * Get vertices in a component.
     * @param componentId component id
     * @return list of vertex ids
     */
    public List<Integer> getComponentVertices(int componentId) {
        if (componentId >= 0 && componentId < componentCount) {
            return new ArrayList<>(components.get(componentId));
        }
        return new ArrayList<>();
    }

    /**
     * Get component for a vertex.
     * @param vertex vertex id
     * @return component id
     */
    public int getComponentId(int vertex) {
        return vertexToComponent[vertex];
    }

    /**
     * Get the underlying graph of components.
     * @return graph representation
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Get number of edges in condensation graph.
     * @return edge count
     */
    public int getEdgeCount() {
        return adjacencyList.values().stream()
                .mapToInt(Set::size)
                .sum();
    }
}

