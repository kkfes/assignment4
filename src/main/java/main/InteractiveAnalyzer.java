package main;

import graph.util.*;
import graph.scc.*;
import graph.topo.*;
import graph.dagsp.*;
import java.io.IOException;
import java.util.*;

/**
 * Interactive analyzer for testing individual datasets and algorithms.
 */
public class InteractiveAnalyzer {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            System.out.print("Select option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    analyzeCustomDataset(scanner);
                    break;
                case "2":
                    testSCCAlgorithms(scanner);
                    break;
                case "3":
                    testTopoSort(scanner);
                    break;
                case "4":
                    testDAGShortestPaths(scanner);
                    break;
                case "5":
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("GRAPH ALGORITHMS INTERACTIVE ANALYZER");
        System.out.println("=".repeat(60));
        System.out.println("1. Analyze custom dataset from JSON");
        System.out.println("2. Test SCC algorithms");
        System.out.println("3. Test Topological Sort");
        System.out.println("4. Test DAG Shortest Paths");
        System.out.println("5. Exit");
        System.out.println("=".repeat(60));
    }

    private static void analyzeCustomDataset(Scanner scanner) {
        System.out.print("Enter JSON file path: ");
        String filePath = scanner.nextLine().trim();

        try {
            Graph graph = GraphJsonUtils.loadGraphFromJson(filePath);
            System.out.println("\nGraph Statistics:");
            System.out.println("  Vertices: " + graph.getVertexCount());
            System.out.println("  Edges: " + graph.getEdgeCount());
            System.out.println("  Density: " + String.format("%.4f",
                (double) graph.getEdgeCount() / (graph.getVertexCount() * (graph.getVertexCount() - 1))));

            // Run all analyses
            System.out.println("\n--- SCC Analysis (Tarjan) ---");
            MetricsImpl metrics1 = new MetricsImpl();
            SCCTarjan tarjan = new SCCTarjan(graph, metrics1);
            List<List<Integer>> components = tarjan.findSCCs();
            System.out.println("SCCs found: " + components.size());
            System.out.println("Metrics: " + metrics1.getSummary());

            System.out.println("\n--- Topological Sort (Kahn) ---");
            MetricsImpl metrics2 = new MetricsImpl();
            TopoKahn topo = new TopoKahn(graph, metrics2);
            List<Integer> order = topo.computeTopologicalOrder();
            if (topo.hasCycle()) {
                System.out.println("Graph has cycles!");
            } else {
                System.out.println("Topological order: " + order);
                System.out.println("Metrics: " + metrics2.getSummary());
            }

            System.out.println("\n--- DAG Shortest Paths ---");
            if (!topo.hasCycle()) {
                MetricsImpl metrics3 = new MetricsImpl();
                DAGShortestPaths dagsp = new DAGShortestPaths(graph, metrics3);
                double[] distances = dagsp.computeShortestPaths(0);
                System.out.println("Distances from vertex 0: " + Arrays.toString(distances));

                Map.Entry<Double, List<Integer>> critPath = dagsp.findCriticalPath(0);
                System.out.println("Critical path length: " + critPath.getKey());
                System.out.println("Critical path: " + critPath.getValue());
                System.out.println("Metrics: " + metrics3.getSummary());
            } else {
                System.out.println("Cannot compute DAG shortest paths for cyclic graph");
            }

        } catch (IOException e) {
            System.err.println("Error loading file: " + e.getMessage());
        }
    }

    private static void testSCCAlgorithms(Scanner scanner) {
        System.out.println("\nCreating test graph with cycles...");
        Graph graph = new Graph(6);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(5, 3);

        System.out.println("\n--- Tarjan's Algorithm ---");
        MetricsImpl metrics1 = new MetricsImpl();
        SCCTarjan tarjan = new SCCTarjan(graph, metrics1);
        List<List<Integer>> components1 = tarjan.findSCCs();
        System.out.println("SCCs: " + components1);
        System.out.println(metrics1.getSummary());

        System.out.println("\n--- Kosaraju's Algorithm ---");
        MetricsImpl metrics2 = new MetricsImpl();
        SCCKosaraju kosaraju = new SCCKosaraju(graph, metrics2);
        List<List<Integer>> components2 = kosaraju.findSCCs();
        System.out.println("SCCs: " + components2);
        System.out.println(metrics2.getSummary());

        System.out.println("\n--- Condensation Graph ---");
        CondensationGraph condensation = new CondensationGraph(components1, graph);
        System.out.println("Components in condensation: " + condensation.getComponentCount());
        System.out.println("Edges in condensation: " + condensation.getEdgeCount());
    }

    private static void testTopoSort(Scanner scanner) {
        System.out.println("\nCreating test DAG...");
        Graph graph = new Graph(5);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);

        System.out.println("\n--- Kahn's Algorithm ---");
        MetricsImpl metrics1 = new MetricsImpl();
        TopoKahn kahn = new TopoKahn(graph, metrics1);
        List<Integer> order1 = kahn.computeTopologicalOrder();
        System.out.println("Order: " + order1);
        System.out.println(metrics1.getSummary());

        System.out.println("\n--- DFS-based Algorithm ---");
        MetricsImpl metrics2 = new MetricsImpl();
        TopoDFS dfs = new TopoDFS(graph, metrics2);
        List<Integer> order2 = dfs.computeTopologicalOrder();
        System.out.println("Order: " + order2);
        System.out.println(metrics2.getSummary());
    }

    private static void testDAGShortestPaths(Scanner scanner) {
        System.out.println("\nCreating weighted DAG...");
        Graph graph = new Graph(6);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 5);
        graph.addEdge(3, 4, 3);
        graph.addEdge(3, 5, 2);
        graph.addEdge(4, 5, 2);

        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(graph, metrics);

        System.out.println("\n--- Shortest Paths from vertex 0 ---");
        double[] shortestDist = dagsp.computeShortestPaths(0);
        System.out.println("Distances: " + Arrays.toString(shortestDist));

        System.out.println("\n--- Longest Paths (Critical Path) ---");
        double[] longestDist = dagsp.computeLongestPaths(0);
        System.out.println("Distances: " + Arrays.toString(longestDist));

        Map.Entry<Double, List<Integer>> critPath = dagsp.findCriticalPath(0);
        System.out.println("Critical path: " + critPath.getValue());
        System.out.println("Critical path length: " + critPath.getKey());

        System.out.println("\nMetrics: " + metrics.getSummary());
    }
}

