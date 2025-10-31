package graph.util;

import java.io.IOException;
import java.util.*;

/**
 * Generator for test datasets with various graph structures and sizes.
 */
public class DatasetGenerator {

    /**
     * Generate small graph with simple cycle (nodes 6-10).
     * Structure: Simple DAG with 1-2 small cycles.
     */
    public static void generateSmallCyclic1() throws IOException {
        Graph graph = new Graph(8);
        String[] names = {"task_A", "task_B", "task_C", "task_D", "task_E", "task_F", "task_G", "task_H"};

        int[][] edges = {
            {0, 1, 2}, {1, 2, 3}, {2, 3, 2}, {3, 0, 1}, // Cycle: 0->1->2->3->0
            {4, 5, 2}, {5, 6, 1}, {6, 4, 1},             // Cycle: 4->5->6->4
            {1, 4, 2}, {3, 7, 3}                           // Cross connections
        };

        for (int i = 0; i < names.length; i++) {
            graph.setVertexName(i, names[i]);
        }
        for (int[] edge : edges) {
            graph.addEdge(edge[0], edge[1], edge[2]);
        }

        GraphJsonUtils.saveGraphToJson(graph, "data/small_cyclic_1.json");
        System.out.println("Generated: small_cyclic_1.json - 8 vertices, " + graph.getEdgeCount() + " edges");
    }

    /**
     * Generate small pure DAG (nodes 6-10).
     */
    public static void generateSmallDAG1() throws IOException {
        Graph graph = new Graph(7);
        String[] names = {"start", "prep", "build", "test", "deploy", "verify", "end"};

        int[][] edges = {
            {0, 1, 1}, {1, 2, 2}, {2, 3, 3}, {2, 4, 2},
            {3, 4, 1}, {4, 5, 2}, {5, 6, 1}
        };

        for (int i = 0; i < names.length; i++) {
            graph.setVertexName(i, names[i]);
        }
        for (int[] edge : edges) {
            graph.addEdge(edge[0], edge[1], edge[2]);
        }

        GraphJsonUtils.saveGraphToJson(graph, "data/small_dag_1.json");
        System.out.println("Generated: small_dag_1.json - 7 vertices, " + graph.getEdgeCount() + " edges");
    }

    /**
     * Generate small sparse DAG with multiple independent chains.
     */
    public static void generateSmallDAG2() throws IOException {
        Graph graph = new Graph(10);
        String[] names = {"t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9"};

        int[][] edges = {
            {0, 1, 2}, {1, 2, 1},
            {3, 4, 3}, {4, 5, 1},
            {6, 7, 2},
            {2, 8, 1}, {5, 8, 2}, {7, 8, 1},
            {8, 9, 2}
        };

        for (int i = 0; i < names.length; i++) {
            graph.setVertexName(i, names[i]);
        }
        for (int[] edge : edges) {
            graph.addEdge(edge[0], edge[1], edge[2]);
        }

        GraphJsonUtils.saveGraphToJson(graph, "data/small_dag_2.json");
        System.out.println("Generated: small_dag_2.json - 10 vertices, " + graph.getEdgeCount() + " edges");
    }

    /**
     * Generate medium graph with multiple SCCs.
     */
    public static void generateMediumMultipleSCC() throws IOException {
        Graph graph = new Graph(15);
        String[] names = new String[15];
        for (int i = 0; i < 15; i++) {
            names[i] = "task_" + i;
        }

        int[][] edges = {
            // SCC 1: 0,1,2
            {0, 1, 2}, {1, 2, 1}, {2, 0, 1},
            // SCC 2: 3,4,5
            {3, 4, 2}, {4, 5, 1}, {5, 3, 2},
            // SCC 3: 6,7
            {6, 7, 1}, {7, 6, 2},
            // Single vertices: 8,9,10,11,12,13,14
            // Connections between SCCs and singles
            {0, 3, 2}, {2, 6, 1},
            {4, 8, 2}, {7, 9, 1},
            {8, 10, 1}, {9, 10, 2},
            {10, 11, 1}, {11, 12, 2},
            {12, 13, 1}, {13, 14, 2},
            // Some additional cross connections
            {1, 9, 1}, {5, 11, 2}
        };

        for (int i = 0; i < names.length; i++) {
            graph.setVertexName(i, names[i]);
        }
        for (int[] edge : edges) {
            graph.addEdge(edge[0], edge[1], edge[2]);
        }

        GraphJsonUtils.saveGraphToJson(graph, "data/medium_multiple_scc.json");
        System.out.println("Generated: medium_multiple_scc.json - 15 vertices, " + graph.getEdgeCount() + " edges");
    }

    /**
     * Generate medium sparse DAG.
     */
    public static void generateMediumSparseDAG() throws IOException {
        Graph graph = new Graph(16);
        String[] names = new String[16];
        for (int i = 0; i < 16; i++) {
            names[i] = "task_" + i;
        }

        // Create sparse DAG using layers
        int[][] edges = {
            // Layer 0->1
            {0, 1, 1}, {0, 2, 2},
            // Layer 1->2
            {1, 3, 2}, {1, 4, 1}, {2, 4, 2}, {2, 5, 1},
            // Layer 2->3
            {3, 6, 1}, {4, 7, 2}, {5, 7, 1}, {5, 8, 2},
            // Layer 3->4
            {6, 9, 2}, {7, 10, 1}, {8, 10, 1}, {8, 11, 2},
            // Layer 4->5
            {9, 12, 1}, {10, 13, 2}, {11, 13, 1}, {11, 14, 2},
            // Final layer
            {12, 15, 2}, {13, 15, 1}, {14, 15, 2}
        };

        for (int i = 0; i < names.length; i++) {
            graph.setVertexName(i, names[i]);
        }
        for (int[] edge : edges) {
            graph.addEdge(edge[0], edge[1], edge[2]);
        }

        GraphJsonUtils.saveGraphToJson(graph, "data/medium_sparse_dag.json");
        System.out.println("Generated: medium_sparse_dag.json - 16 vertices, " + graph.getEdgeCount() + " edges");
    }

    /**
     * Generate medium dense graph with cycles.
     */
    public static void generateMediumDenseCyclic() throws IOException {
        Random rand = new Random(42);
        int n = 18;
        Graph graph = new Graph(n);
        String[] names = new String[n];
        for (int i = 0; i < n; i++) {
            names[i] = "task_" + i;
            graph.setVertexName(i, names[i]);
        }

        // Create base cycles
        for (int i = 0; i < 3; i++) {
            int base = i * 6;
            for (int j = 0; j < 5; j++) {
                graph.addEdge(base + j, base + (j + 1), rand.nextInt(3) + 1);
            }
            graph.addEdge(base + 5, base, rand.nextInt(3) + 1);
        }

        // Connect cycles
        graph.addEdge(0, 6, 2);
        graph.addEdge(6, 12, 1);
        graph.addEdge(8, 14, 2);
        graph.addEdge(12, 2, 1);

        GraphJsonUtils.saveGraphToJson(graph, "data/medium_dense_cyclic.json");
        System.out.println("Generated: medium_dense_cyclic.json - " + n + " vertices, " + graph.getEdgeCount() + " edges");
    }

    /**
     * Generate large sparse DAG for performance testing.
     */
    public static void generateLargeSparseDAG() throws IOException {
        Random rand = new Random(123);
        int n = 30;
        Graph graph = new Graph(n);
        String[] names = new String[n];
        for (int i = 0; i < n; i++) {
            names[i] = "task_" + i;
            graph.setVertexName(i, names[i]);
        }

        // Create layered DAG structure for sparsity
        int layerSize = 5;
        for (int layer = 0; layer < 5; layer++) {
            for (int i = 0; i < layerSize; i++) {
                int from = layer * layerSize + i;
                if (layer < 5 - 1) {
                    int nextLayer = (layer + 1) * layerSize;
                    // Connect to 1-3 nodes in next layer
                    for (int j = 0; j < 1 + rand.nextInt(3); j++) {
                        int to = nextLayer + rand.nextInt(layerSize);
                        graph.addEdge(from, to, rand.nextInt(5) + 1);
                    }
                }
            }
        }

        GraphJsonUtils.saveGraphToJson(graph, "data/large_sparse_dag.json");
        System.out.println("Generated: large_sparse_dag.json - " + n + " vertices, " + graph.getEdgeCount() + " edges");
    }

    /**
     * Generate large dense DAG.
     */
    public static void generateLargeDenseDAG() throws IOException {
        Random rand = new Random(456);
        int n = 35;
        Graph graph = new Graph(n);
        String[] names = new String[n];
        for (int i = 0; i < n; i++) {
            names[i] = "task_" + i;
            graph.setVertexName(i, names[i]);
        }

        // Create dense connections in topological order
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < Math.min(i + 8, n); j++) {
                if (rand.nextDouble() < 0.6) { // 60% probability
                    graph.addEdge(i, j, rand.nextInt(5) + 1);
                }
            }
        }

        GraphJsonUtils.saveGraphToJson(graph, "data/large_dense_dag.json");
        System.out.println("Generated: large_dense_dag.json - " + n + " vertices, " + graph.getEdgeCount() + " edges");
    }

    /**
     * Generate large graph with complex SCC structure.
     */
    public static void generateLargeComplexSCC() throws IOException {
        Random rand = new Random(789);
        int n = 40;
        Graph graph = new Graph(n);
        String[] names = new String[n];
        for (int i = 0; i < n; i++) {
            names[i] = "task_" + i;
            graph.setVertexName(i, names[i]);
        }

        // Create several SCCs
        int[][] sccBoundaries = {{0, 8}, {8, 16}, {16, 24}, {24, 32}, {32, 40}};
        for (int[] boundary : sccBoundaries) {
            // Create cycle within SCC
            for (int i = boundary[0]; i < boundary[1] - 1; i++) {
                graph.addEdge(i, i + 1, rand.nextInt(3) + 1);
            }
            graph.addEdge(boundary[1] - 1, boundary[0], rand.nextInt(3) + 1);
        }

        // Add inter-SCC edges
        graph.addEdge(3, 10, 2);
        graph.addEdge(13, 20, 1);
        graph.addEdge(19, 26, 2);
        graph.addEdge(29, 34, 1);

        GraphJsonUtils.saveGraphToJson(graph, "data/large_complex_scc.json");
        System.out.println("Generated: large_complex_scc.json - " + n + " vertices, " + graph.getEdgeCount() + " edges");
    }

    /**
     * Generate all test datasets.
     */
    public static void generateAllDatasets() throws IOException {
        System.out.println("Generating test datasets...\n");

        // Small datasets
        generateSmallCyclic1();
        generateSmallDAG1();
        generateSmallDAG2();

        // Medium datasets
        generateMediumMultipleSCC();
        generateMediumSparseDAG();
        generateMediumDenseCyclic();

        // Large datasets
        generateLargeSparseDAG();
        generateLargeDenseDAG();
        generateLargeComplexSCC();

        System.out.println("\nAll datasets generated successfully!");
    }

    public static void main(String[] args) {
        try {
            generateAllDatasets();
        } catch (IOException e) {
            System.err.println("Error generating datasets: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

