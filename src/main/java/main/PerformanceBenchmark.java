package main;

import graph.util.*;
import graph.scc.*;
import graph.topo.*;
import graph.dagsp.*;
import java.io.IOException;
import java.util.*;

/**
 * Performance benchmarking for all algorithms across datasets.
 */
public class PerformanceBenchmark {

    private static class BenchmarkResult {
        String algorithm;
        String dataset;
        int vertices;
        int edges;
        double density;
        long timeNanos;
        int counter1;
        int counter2;

        public BenchmarkResult(String algorithm, String dataset, int vertices, int edges,
                             double density, long timeNanos, int c1, int c2) {
            this.algorithm = algorithm;
            this.dataset = dataset;
            this.vertices = vertices;
            this.edges = edges;
            this.density = density;
            this.timeNanos = timeNanos;
            this.counter1 = c1;
            this.counter2 = c2;
        }

        @Override
        public String toString() {
            return String.format("%s | %s | V:%d E:%d | Time: %.4f ms | Ops: %d,%d",
                    algorithm, dataset, vertices, edges, timeNanos / 1_000_000.0, counter1, counter2);
        }
    }

    private static List<BenchmarkResult> results = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("=".repeat(100));
        System.out.println("PERFORMANCE BENCHMARK - ALL ALGORITHMS");
        System.out.println("=".repeat(100));

        // List of datasets to benchmark
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

        // Run benchmarks
        for (String dataset : datasets) {
            try {
                benchmarkDataset(dataset);
            } catch (IOException e) {
                System.err.println("Could not load dataset: " + dataset);
            }
        }

        // Print summary
        printBenchmarkSummary();
    }

    private static void benchmarkDataset(String filePath) throws IOException {
        Graph graph = GraphJsonUtils.loadGraphFromJson(filePath);
        String datasetName = filePath.substring(filePath.lastIndexOf("/") + 1)
                                     .replace(".json", "");

        int vertices = graph.getVertexCount();
        int edges = graph.getEdgeCount();
        double density = (double) edges / (vertices * (vertices - 1));

        System.out.println("\n" + "-".repeat(100));
        System.out.println("Dataset: " + datasetName + " (V=" + vertices + ", E=" + edges +
                          ", Density=" + String.format("%.3f", density) + ")");
        System.out.println("-".repeat(100));

        // Benchmark SCC Tarjan
        benchmarkSCCTarjan(graph, datasetName, vertices, edges, density);

        // Benchmark SCC Kosaraju
        benchmarkSCCKosaraju(graph, datasetName, vertices, edges, density);

        // Benchmark Topo Kahn
        benchmarkTopoKahn(graph, datasetName, vertices, edges, density);

        // Benchmark Topo DFS
        benchmarkTopoDFS(graph, datasetName, vertices, edges, density);

        // Benchmark DAG Shortest Paths (if DAG)
        benchmarkDAGShortestPaths(graph, datasetName, vertices, edges, density);
    }

    private static void benchmarkSCCTarjan(Graph graph, String dataset, int v, int e, double dens) {
        MetricsImpl metrics = new MetricsImpl();
        SCCTarjan scc = new SCCTarjan(graph, metrics);

        long startTime = System.nanoTime();
        List<List<Integer>> components = scc.findSCCs();
        long endTime = System.nanoTime();

        long timeNanos = endTime - startTime;
        int visits = metrics.getCounter("dfs_visits");
        int edges = metrics.getCounter("edges_examined");

        BenchmarkResult result = new BenchmarkResult("SCC-Tarjan", dataset, v, e, dens, timeNanos, visits, edges);
        results.add(result);
        System.out.println("  " + result);
    }

    private static void benchmarkSCCKosaraju(Graph graph, String dataset, int v, int e, double dens) {
        MetricsImpl metrics = new MetricsImpl();
        SCCKosaraju scc = new SCCKosaraju(graph, metrics);

        long startTime = System.nanoTime();
        List<List<Integer>> components = scc.findSCCs();
        long endTime = System.nanoTime();

        long timeNanos = endTime - startTime;
        int visits = metrics.getCounter("dfs_visits");
        int edges = metrics.getCounter("edges_examined");

        BenchmarkResult result = new BenchmarkResult("SCC-Kosaraju", dataset, v, e, dens, timeNanos, visits, edges);
        results.add(result);
        System.out.println("  " + result);
    }

    private static void benchmarkTopoKahn(Graph graph, String dataset, int v, int e, double dens) {
        MetricsImpl metrics = new MetricsImpl();
        TopoKahn topo = new TopoKahn(graph, metrics);

        long startTime = System.nanoTime();
        List<Integer> order = topo.computeTopologicalOrder();
        long endTime = System.nanoTime();

        long timeNanos = endTime - startTime;
        int pushes = metrics.getCounter("pushes");
        int pops = metrics.getCounter("pops");

        String status = topo.hasCycle() ? "[CYCLE]" : "[DAG]";
        BenchmarkResult result = new BenchmarkResult("Topo-Kahn" + status, dataset, v, e, dens,
                                                    timeNanos, pushes, pops);
        results.add(result);
        System.out.println("  " + result);
    }

    private static void benchmarkTopoDFS(Graph graph, String dataset, int v, int e, double dens) {
        MetricsImpl metrics = new MetricsImpl();
        TopoDFS topo = new TopoDFS(graph, metrics);

        long startTime = System.nanoTime();
        List<Integer> order = topo.computeTopologicalOrder();
        long endTime = System.nanoTime();

        long timeNanos = endTime - startTime;
        int visits = metrics.getCounter("dfs_visits");
        int edges = metrics.getCounter("edges_examined");

        String status = topo.hasCycle() ? "[CYCLE]" : "[DAG]";
        BenchmarkResult result = new BenchmarkResult("Topo-DFS" + status, dataset, v, e, dens,
                                                    timeNanos, visits, edges);
        results.add(result);
        System.out.println("  " + result);
    }

    private static void benchmarkDAGShortestPaths(Graph graph, String dataset, int v, int e, double dens) {
        // First check if DAG
        MetricsImpl checkMetrics = new MetricsImpl();
        TopoKahn check = new TopoKahn(graph, checkMetrics);
        check.computeTopologicalOrder();

        if (check.hasCycle()) {
            System.out.println("  DAGSP: [SKIPPED - CYCLIC GRAPH]");
            return;
        }

        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(graph, metrics);

        long startTime = System.nanoTime();
        double[] distances = dagsp.computeShortestPaths(0);
        long endTime = System.nanoTime();

        long timeNanos = endTime - startTime;
        int relaxations = metrics.getCounter("relaxations");

        BenchmarkResult result = new BenchmarkResult("DAG-SP", dataset, v, e, dens,
                                                    timeNanos, relaxations, 0);
        results.add(result);
        System.out.println("  " + result);
    }

    private static void printBenchmarkSummary() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("BENCHMARK SUMMARY");
        System.out.println("=".repeat(100));

        // Sort by algorithm and time
        Map<String, List<BenchmarkResult>> byAlgorithm = new HashMap<>();
        for (BenchmarkResult result : results) {
            byAlgorithm.computeIfAbsent(result.algorithm, k -> new ArrayList<>()).add(result);
        }

        for (String alg : byAlgorithm.keySet()) {
            List<BenchmarkResult> algResults = byAlgorithm.get(alg);
            System.out.println("\n" + alg + ":");
            System.out.println("  Count: " + algResults.size());

            double avgTime = algResults.stream()
                    .mapToLong(r -> r.timeNanos)
                    .average()
                    .orElse(0);

            long minTime = algResults.stream()
                    .mapToLong(r -> r.timeNanos)
                    .min()
                    .orElse(0);

            long maxTime = algResults.stream()
                    .mapToLong(r -> r.timeNanos)
                    .max()
                    .orElse(0);

            System.out.println("  Avg time: " + String.format("%.4f ms", avgTime / 1_000_000.0));
            System.out.println("  Min time: " + String.format("%.4f ms", minTime / 1_000_000.0));
            System.out.println("  Max time: " + String.format("%.4f ms", maxTime / 1_000_000.0));
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.println("Benchmark complete. Check console output for detailed results.");
        System.out.println("=".repeat(100));
    }
}

