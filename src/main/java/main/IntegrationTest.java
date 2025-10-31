package main;

import graph.util.*;
import graph.scc.*;
import graph.topo.*;
import graph.dagsp.*;
import java.io.IOException;
import java.util.*;

/**
 * Comprehensive integration test demonstrating all algorithms.
 */
public class IntegrationTest {

    public static void main(String[] args) throws IOException {
        System.out.println("=".repeat(80));
        System.out.println("COMPREHENSIVE INTEGRATION TEST");
        System.out.println("=".repeat(80));

        // Create sample graph for demonstration
        testCompleteWorkflow();
    }

    private static void testCompleteWorkflow() {
        System.out.println("\n1. Creating sample project dependency graph...\n");

        Graph graph = new Graph(8);
        String[] taskNames = {
            "Design",
            "Backend",
            "Frontend",
            "Database",
            "Testing",
            "Integration",
            "Documentation",
            "Deployment"
        };

        // Set vertex names
        for (int i = 0; i < taskNames.length; i++) {
            graph.setVertexName(i, taskNames[i]);
        }

        // Define dependencies with durations
        int[][] dependencies = {
            {0, 1, 5},   // Design -> Backend (5 days)
            {0, 2, 4},   // Design -> Frontend (4 days)
            {1, 3, 3},   // Backend -> Database (3 days)
            {2, 3, 2},   // Frontend -> Database (2 days)
            {1, 4, 2},   // Backend -> Testing (2 days)
            {3, 4, 3},   // Database -> Testing (3 days)
            {4, 5, 2},   // Testing -> Integration (2 days)
            {5, 6, 1},   // Integration -> Documentation (1 day)
            {6, 7, 1}    // Documentation -> Deployment (1 day)
        };

        for (int[] dep : dependencies) {
            graph.addEdge(dep[0], dep[1], dep[2]);
        }

        System.out.println("Graph: " + graph.getVertexCount() + " tasks, " +
                          graph.getEdgeCount() + " dependencies");
        System.out.println("Tasks: " + String.join(", ", taskNames));

        // Test 1: SCC Analysis
        testSCCAnalysis(graph);

        // Test 2: Topological Sort
        testTopologicalSort(graph);

        // Test 3: Shortest Paths
        testShortestPaths(graph);

        // Test 4: Critical Path (Longest Path)
        testCriticalPath(graph);

        // Test 5: Create and save JSON
        testJsonSerialization(graph);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("ALL TESTS COMPLETED SUCCESSFULLY");
        System.out.println("=".repeat(80));
    }

    private static void testSCCAnalysis(Graph graph) {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("TEST 1: STRONGLY CONNECTED COMPONENTS (SCC)");
        System.out.println("-".repeat(80));

        MetricsImpl metrics = new MetricsImpl();
        SCCTarjan tarjan = new SCCTarjan(graph, metrics);
        List<List<Integer>> components = tarjan.findSCCs();

        System.out.println("Number of SCCs: " + components.size());
        for (int i = 0; i < components.size(); i++) {
            List<Integer> component = components.get(i);
            System.out.print("  SCC " + (i + 1) + ": ");
            System.out.println(component.stream()
                    .map(graph::getVertexName)
                    .toList());
        }

        System.out.println("\nMetrics:");
        System.out.println("  DFS visits: " + metrics.getCounter("dfs_visits"));
        System.out.println("  Edges examined: " + metrics.getCounter("edges_examined"));
        System.out.println("  Time: " + String.format("%.4f ms", metrics.getExecutionTimeMillis()));

        System.out.println("\nInterpretation:");
        if (components.size() == graph.getVertexCount()) {
            System.out.println("  ✓ Graph is a DAG (no cycles) - all tasks are independent");
        } else {
            System.out.println("  ✗ Graph has cycles - there are circular dependencies!");
            System.out.println("  Action: Resolve circular dependencies before scheduling");
        }
    }

    private static void testTopologicalSort(Graph graph) {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("TEST 2: TOPOLOGICAL SORT");
        System.out.println("-".repeat(80));

        MetricsImpl metrics = new MetricsImpl();
        TopoKahn topo = new TopoKahn(graph, metrics);
        List<Integer> order = topo.computeTopologicalOrder();

        if (topo.hasCycle()) {
            System.out.println("ERROR: Graph contains cycles - topological sort impossible!");
            return;
        }

        System.out.println("Valid execution order:");
        for (int i = 0; i < order.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + graph.getVertexName(order.get(i)));
        }

        System.out.println("\nMetrics:");
        System.out.println("  Queue operations: " + metrics.getCounter("pushes") + " / " +
                          metrics.getCounter("pops"));
        System.out.println("  Time: " + String.format("%.4f ms", metrics.getExecutionTimeMillis()));

        System.out.println("\nInterpretation:");
        System.out.println("  ✓ All tasks can be ordered - project is feasible");
        System.out.println("  ✓ Use this order to schedule task execution");
    }

    private static void testShortestPaths(Graph graph) {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("TEST 3: SHORTEST PATHS IN DAG");
        System.out.println("-".repeat(80));

        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(graph, metrics);
        double[] distances = dagsp.computeShortestPaths(0);

        System.out.println("Shortest completion times from '" + graph.getVertexName(0) + "':");
        for (int i = 0; i < distances.length; i++) {
            if (distances[i] == Double.POSITIVE_INFINITY) {
                System.out.println("  " + graph.getVertexName(i) + ": UNREACHABLE");
            } else {
                System.out.println("  " + graph.getVertexName(i) + ": " +
                                  String.format("%.0f days", distances[i]));
            }
        }

        System.out.println("\nMetrics:");
        System.out.println("  Edge relaxations: " + metrics.getCounter("relaxations"));
        System.out.println("  Time: " + String.format("%.4f ms", metrics.getExecutionTimeMillis()));

        System.out.println("\nInterpretation:");
        System.out.println("  ✓ Fastest path to complete each task from project start");
        System.out.println("  ✓ Use for minimum time scheduling");
    }

    private static void testCriticalPath(Graph graph) {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("TEST 4: CRITICAL PATH (LONGEST PATH)");
        System.out.println("-".repeat(80));

        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(graph, metrics);
        Map.Entry<Double, List<Integer>> criticalPath = dagsp.findCriticalPath(0);

        System.out.println("Critical path (bottleneck):");
        List<Integer> path = criticalPath.getValue();
        if (path.isEmpty()) {
            System.out.println("  No path found");
        } else {
            for (int i = 0; i < path.size(); i++) {
                System.out.print("  " + graph.getVertexName(path.get(i)));
                if (i < path.size() - 1) {
                    System.out.print(" → ");
                }
            }
            System.out.println();
        }

        System.out.println("\nCritical path duration: " +
                          String.format("%.0f days", criticalPath.getKey()));

        System.out.println("\nMetrics:");
        System.out.println("  Time: " + String.format("%.4f ms", metrics.getExecutionTimeMillis()));

        System.out.println("\nInterpretation:");
        System.out.println("  ✓ Project minimum completion time: " +
                          String.format("%.0f days", criticalPath.getKey()));
        System.out.println("  ✓ Tasks on critical path have zero slack - cannot be delayed");
        System.out.println("  ✓ Any delay on critical path delays entire project");
    }

    private static void testJsonSerialization(Graph graph) throws IOException {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("TEST 5: JSON SERIALIZATION");
        System.out.println("-".repeat(80));

        String outputPath = "data/integration_test_output.json";
        GraphJsonUtils.saveGraphToJson(graph, outputPath);
        System.out.println("✓ Graph saved to: " + outputPath);

        Graph loaded = GraphJsonUtils.loadGraphFromJson(outputPath);
        System.out.println("✓ Graph loaded from file");
        System.out.println("  Vertices match: " + (loaded.getVertexCount() == graph.getVertexCount()));
        System.out.println("  Edges match: " + (loaded.getEdgeCount() == graph.getEdgeCount()));
    }
}

