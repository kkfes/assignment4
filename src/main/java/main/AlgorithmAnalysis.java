package main;

import graph.util.*;
import graph.scc.*;
import graph.topo.*;
import graph.dagsp.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Main demonstration and analysis program for graph algorithms.
 */
public class AlgorithmAnalysis {

    public static void main(String[] args) {
        try {
            // Generate all datasets
            System.out.println("=".repeat(80));
            System.out.println("GENERATING TEST DATASETS");
            System.out.println("=".repeat(80));
            DatasetGenerator.generateAllDatasets();

            // Analyze all datasets
            System.out.println("\n" + "=".repeat(80));
            System.out.println("ANALYZING DATASETS");
            System.out.println("=".repeat(80));
            analyzeAllDatasets();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void analyzeAllDatasets() throws IOException {
        String[] datasets = {
            "data/small_cyclic_1.json",
            "data/small_dag_1.json",
            "data/small_dag_2.json",
            "data/medium_multiple_scc.json",
            "data/medium_sparse_dag.json",
            "data/medium_dense_cyclic.json",
            "data/large_sparse_dag.json",
            "data/large_dense_dag.json",
            "data/large_complex_scc.json"
        };

        for (String dataset : datasets) {
            if (Files.exists(Paths.get(dataset))) {
                System.out.println("\n" + "-".repeat(80));
                System.out.println("Analyzing: " + dataset);
                System.out.println("-".repeat(80));
                analyzeDataset(dataset);
            }
        }
    }

    private static void analyzeDataset(String filePath) throws IOException {
        Graph graph = GraphJsonUtils.loadGraphFromJson(filePath);

        System.out.println("Graph Statistics:");
        System.out.println("  Vertices: " + graph.getVertexCount());
        System.out.println("  Edges: " + graph.getEdgeCount());
        System.out.println("  Density: " + String.format("%.3f",
            (double) graph.getEdgeCount() / (graph.getVertexCount() * (graph.getVertexCount() - 1))));

        // Analyze with Tarjan's SCC
        System.out.println("\nTarjan's SCC Algorithm:");
        analyzeSCC(graph, new SCCTarjan(graph, new MetricsImpl()));

        // Analyze with Kosaraju's SCC
        System.out.println("\nKosaraju's SCC Algorithm:");
        analyzeSCC(graph, new SCCKosaraju(graph, new MetricsImpl()));

        // Analyze topological sort
        System.out.println("\nTopological Sort (Kahn's Algorithm):");
        analyzeTopologicalSort(graph);

        // Analyze DAG shortest paths (if DAG)
        System.out.println("\nDAG Shortest Paths Analysis:");
        analyzeDAGShortestPaths(graph);
    }

    private static void analyzeSCC(Graph graph, Object sccAlgorithm) {
        MetricsImpl metrics = new MetricsImpl();
        List<List<Integer>> components;

        if (sccAlgorithm instanceof SCCTarjan tarjan) {
            components = tarjan.findSCCs();
        } else if (sccAlgorithm instanceof SCCKosaraju kosaraju) {
            components = kosaraju.findSCCs();
        } else {
            return;
        }

        System.out.println("  Number of SCCs: " + components.size());
        System.out.println("  Component sizes: ");
        List<Integer> sizes = components.stream()
                .map(List::size)
                .sorted(Comparator.reverseOrder())
                .toList();
        for (int i = 0; i < Math.min(5, sizes.size()); i++) {
            System.out.println("    Component " + (i + 1) + ": " + sizes.get(i) + " vertices");
        }
        if (sizes.size() > 5) {
            System.out.println("    ... and " + (sizes.size() - 5) + " more");
        }

        // Check if graph is DAG
        boolean isDAG = components.size() == graph.getVertexCount();
        System.out.println("  Is DAG: " + isDAG);

        if (!isDAG) {
            System.out.println("  Largest SCC: " + sizes.get(0) + " vertices");
        }
    }

    private static void analyzeTopologicalSort(Graph graph) {
        MetricsImpl metrics = new MetricsImpl();
        TopoKahn topoKahn = new TopoKahn(graph, metrics);
        List<Integer> order = topoKahn.computeTopologicalOrder();

        if (topoKahn.hasCycle()) {
            System.out.println("  Status: Graph has cycle - topological sort not possible");
        } else {
            System.out.println("  Status: Successfully sorted");
            System.out.println("  Vertices in order: " + (order.size() <= 10 ? order :
                order.subList(0, 10).toString() + " ... (truncated)"));
            System.out.println("  Execution time: " + String.format("%.4f ms", metrics.getExecutionTimeMillis()));
            System.out.println("  Pushes: " + metrics.getCounter("pushes"));
            System.out.println("  Pops: " + metrics.getCounter("pops"));
        }
    }

    private static void analyzeDAGShortestPaths(Graph graph) {
        MetricsImpl metrics = new MetricsImpl();
        TopoKahn topoCheck = new TopoKahn(graph, new MetricsImpl());
        topoCheck.computeTopologicalOrder();

        if (topoCheck.hasCycle()) {
            System.out.println("  Status: Graph has cycle - skipping DAG analysis");
            return;
        }

        DAGShortestPaths dagsp = new DAGShortestPaths(graph, metrics);
        double[] shortestPaths = dagsp.computeShortestPaths(0);

        System.out.println("  Source: vertex 0");
        System.out.println("  Shortest paths computed: " +
            Arrays.stream(shortestPaths).filter(d -> d != Double.POSITIVE_INFINITY).count() +
            " reachable vertices");
        System.out.println("  Execution time: " + String.format("%.4f ms", metrics.getExecutionTimeMillis()));
        System.out.println("  Relaxations: " + metrics.getCounter("relaxations"));

        // Find critical path
        Map.Entry<Double, List<Integer>> criticalPath = dagsp.findCriticalPath(0);
        System.out.println("  Critical path (longest): " +
            String.format("%.0f", criticalPath.getKey()));
        if (!criticalPath.getValue().isEmpty()) {
            System.out.println("  Path: " + criticalPath.getValue());
        }
    }
}

