package graph.util;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * JSON utilities for reading/writing graph data.
 */
public class GraphJsonUtils {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * JSON structure for graph data.
     */
    public static class GraphData {
        public String name;
        public int vertices;
        public List<EdgeData> edges;
        public List<String> vertexNames;

        public GraphData() {
            this.edges = new ArrayList<>();
            this.vertexNames = new ArrayList<>();
        }
    }

    /**
     * JSON structure for edge data.
     */
    public static class EdgeData {
        public int from;
        public int to;
        public double weight;

        public EdgeData() {}
        public EdgeData(int from, int to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    /**
     * Load graph from JSON file.
     * @param filePath path to JSON file
     * @return Graph object
     * @throws IOException if file not found or invalid
     */
    public static Graph loadGraphFromJson(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        GraphData data = gson.fromJson(content, GraphData.class);

        if (data == null || data.vertices <= 0) {
            throw new IOException("Invalid graph data");
        }

        Graph graph = new Graph(data.vertices);

        // Set vertex names
        for (int i = 0; i < Math.min(data.vertexNames.size(), data.vertices); i++) {
            graph.setVertexName(i, data.vertexNames.get(i));
        }

        // Add edges
        for (EdgeData edgeData : data.edges) {
            if (graph.isValidVertex(edgeData.from) && graph.isValidVertex(edgeData.to)) {
                graph.addEdge(edgeData.from, edgeData.to, edgeData.weight);
            }
        }

        return graph;
    }

    /**
     * Save graph to JSON file.
     * @param graph graph to save
     * @param filePath path to save JSON file
     * @throws IOException if write fails
     */
    public static void saveGraphToJson(Graph graph, String filePath) throws IOException {
        GraphData data = new GraphData();
        data.name = "Task Graph";
        data.vertices = graph.getVertexCount();

        // Add vertex names
        for (int i = 0; i < graph.getVertexCount(); i++) {
            data.vertexNames.add(graph.getVertexName(i));
        }

        // Add edges
        for (int i = 0; i < graph.getVertexCount(); i++) {
            for (Graph.Edge edge : graph.getAdjacentEdges(i)) {
                data.edges.add(new EdgeData(i, edge.to, edge.weight));
            }
        }

        String json = gson.toJson(data);
        Files.createDirectories(Paths.get(filePath).getParent());
        Files.write(Paths.get(filePath), json.getBytes());
    }

    /**
     * Create a graph from adjacency list specification.
     * @param vertices number of vertices
     * @param edges list of edges [from, to, weight]
     * @param names optional vertex names
     * @return Graph object
     */
    public static Graph createGraph(int vertices, List<int[]> edges, String[] names) {
        Graph graph = new Graph(vertices);

        if (names != null) {
            for (int i = 0; i < Math.min(names.length, vertices); i++) {
                graph.setVertexName(i, names[i]);
            }
        }

        for (int[] edge : edges) {
            if (edge.length >= 2) {
                double weight = edge.length >= 3 ? edge[2] : 1.0;
                graph.addEdge(edge[0], edge[1], weight);
            }
        }

        return graph;
    }
}

