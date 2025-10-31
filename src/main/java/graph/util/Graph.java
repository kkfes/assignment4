package graph.util;

import java.util.*;

/**
 * Generic directed graph representation using adjacency list.
 * Supports weighted edges (weights default to 1.0 if not specified).
 */
public class Graph {
    private final int vertices;
    private final Map<Integer, List<Edge>> adjacencyList;
    private final Map<String, Integer> nodeNameToId;
    private final Map<Integer, String> idToNodeName;

    public static class Edge {
        public final int to;
        public final double weight;

        public Edge(int to, double weight) {
            this.to = to;
            this.weight = weight;
        }

        public Edge(int to) {
            this(to, 1.0);
        }
    }

    /**
     * Create a graph with given number of vertices.
     * @param vertices number of vertices
     */
    public Graph(int vertices) {
        this.vertices = vertices;
        this.adjacencyList = new HashMap<>();
        this.nodeNameToId = new HashMap<>();
        this.idToNodeName = new HashMap<>();
        for (int i = 0; i < vertices; i++) {
            adjacencyList.put(i, new ArrayList<>());
            idToNodeName.put(i, String.valueOf(i));
        }
    }

    /**
     * Set a name for a vertex.
     * @param id vertex id
     * @param name name to assign
     */
    public void setVertexName(int id, String name) {
        if (id >= 0 && id < vertices) {
            nodeNameToId.put(name, id);
            idToNodeName.put(id, name);
        }
    }

    /**
     * Get vertex name.
     * @param id vertex id
     * @return name of vertex
     */
    public String getVertexName(int id) {
        return idToNodeName.getOrDefault(id, String.valueOf(id));
    }

    /**
     * Get vertex id by name.
     * @param name vertex name
     * @return id or -1 if not found
     */
    public int getVertexId(String name) {
        return nodeNameToId.getOrDefault(name, -1);
    }

    /**
     * Add an edge with weight.
     * @param from source vertex
     * @param to destination vertex
     * @param weight edge weight
     */
    public void addEdge(int from, int to, double weight) {
        if (from >= 0 && from < vertices && to >= 0 && to < vertices) {
            adjacencyList.get(from).add(new Edge(to, weight));
        }
    }

    /**
     * Add an unweighted edge (weight = 1.0).
     * @param from source vertex
     * @param to destination vertex
     */
    public void addEdge(int from, int to) {
        addEdge(from, to, 1.0);
    }

    /**
     * Get all edges from a vertex.
     * @param vertex vertex id
     * @return list of edges
     */
    public List<Edge> getAdjacentEdges(int vertex) {
        return adjacencyList.getOrDefault(vertex, new ArrayList<>());
    }

    /**
     * Get number of vertices.
     * @return vertex count
     */
    public int getVertexCount() {
        return vertices;
    }

    /**
     * Get total number of edges.
     * @return edge count
     */
    public int getEdgeCount() {
        int count = 0;
        for (List<Edge> edges : adjacencyList.values()) {
            count += edges.size();
        }
        return count;
    }

    /**
     * Get reverse graph (transpose).
     * @return new graph with reversed edges
     */
    public Graph getTranspose() {
        Graph transpose = new Graph(vertices);
        for (int i = 0; i < vertices; i++) {
            for (Edge edge : adjacencyList.get(i)) {
                transpose.addEdge(edge.to, i, edge.weight);
            }
            transpose.setVertexName(i, getVertexName(i));
        }
        return transpose;
    }

    /**
     * Check if vertex exists.
     * @param vertex vertex id
     * @return true if valid
     */
    public boolean isValidVertex(int vertex) {
        return vertex >= 0 && vertex < vertices;
    }
}

