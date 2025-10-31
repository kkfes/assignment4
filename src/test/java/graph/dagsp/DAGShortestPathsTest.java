package graph.dagsp;

import graph.util.Graph;
import graph.util.MetricsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for DAG shortest/longest paths.
 */
public class DAGShortestPathsTest {
    private Graph simpleDAG;
    private Graph complexDAG;
    private Graph linearDAG;

    @BeforeEach
    public void setUp() {
        // Simple linear DAG: 0->1(2)->2(3)->3(1)
        simpleDAG = new Graph(4);
        simpleDAG.addEdge(0, 1, 2);
        simpleDAG.addEdge(1, 2, 3);
        simpleDAG.addEdge(2, 3, 1);

        // Complex DAG with multiple paths
        complexDAG = new Graph(6);
        complexDAG.addEdge(0, 1, 4);
        complexDAG.addEdge(0, 2, 2);
        complexDAG.addEdge(1, 3, 1);
        complexDAG.addEdge(2, 3, 5);
        complexDAG.addEdge(3, 4, 3);
        complexDAG.addEdge(3, 5, 2);
        complexDAG.addEdge(4, 5, 2);

        // Linear DAG
        linearDAG = new Graph(5);
        linearDAG.addEdge(0, 1, 1);
        linearDAG.addEdge(1, 2, 2);
        linearDAG.addEdge(2, 3, 3);
        linearDAG.addEdge(3, 4, 4);
    }

    @Test
    public void testShortestPathsSimpleDAG() {
        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(simpleDAG, metrics);
        double[] distances = dagsp.computeShortestPaths(0);

        assertEquals(0, distances[0]);
        assertEquals(2, distances[1]);
        assertEquals(5, distances[2]);
        assertEquals(6, distances[3]);
    }

    @Test
    public void testShortestPathsComplexDAG() {
        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(complexDAG, metrics);
        double[] distances = dagsp.computeShortestPaths(0);

        assertEquals(0, distances[0]);
        assertEquals(4, distances[1]);
        assertEquals(2, distances[2]);
        assertEquals(5, distances[3]); // min(4+1, 2+5) = 5
        assertEquals(8, distances[4]); // 5+3
        assertEquals(7, distances[5]); // min(5+2, 8+2) = 7
    }

    @Test
    public void testShortestPathsUnreachable() {
        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(complexDAG, metrics);
        double[] distances = dagsp.computeShortestPaths(5);

        assertEquals(0, distances[5]);
        assertEquals(Double.POSITIVE_INFINITY, distances[0]);
        assertEquals(Double.POSITIVE_INFINITY, distances[1]);
    }

    @Test
    public void testLongestPathsSimpleDAG() {
        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(simpleDAG, metrics);
        double[] distances = dagsp.computeLongestPaths(0);

        assertEquals(0, distances[0]);
        assertEquals(2, distances[1]);
        assertEquals(5, distances[2]);
        assertEquals(6, distances[3]);
    }

    @Test
    public void testLongestPathsLinearDAG() {
        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(linearDAG, metrics);
        double[] distances = dagsp.computeLongestPaths(0);

        assertEquals(0, distances[0]);
        assertEquals(1, distances[1]);
        assertEquals(3, distances[2]);
        assertEquals(6, distances[3]);
        assertEquals(10, distances[4]);
    }

    @Test
    public void testReconstructPath() {
        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(simpleDAG, metrics);
        dagsp.computeShortestPaths(0);

        List<Integer> path = dagsp.reconstructPath(3);
        assertNotNull(path);
        assertEquals(4, path.size());
        assertEquals(0, path.get(0));
        assertEquals(3, path.get(3));
    }

    @Test
    public void testReconstructPathUnreachable() {
        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(complexDAG, metrics);
        dagsp.computeShortestPaths(5);

        List<Integer> path = dagsp.reconstructPath(0);
        assertTrue(path.isEmpty());
    }

    @Test
    public void testCriticalPath() {
        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(linearDAG, metrics);

        Map.Entry<Double, List<Integer>> criticalPath = dagsp.findCriticalPath(0);
        assertNotNull(criticalPath);
        assertEquals(10, criticalPath.getKey());
        assertEquals(5, criticalPath.getValue().size());
    }

    @Test
    public void testSingleVertex() {
        Graph single = new Graph(1);
        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(single, metrics);
        double[] distances = dagsp.computeShortestPaths(0);

        assertEquals(0, distances[0]);
    }

    @Test
    public void testDisconnectedDAG() {
        Graph disconnected = new Graph(4);
        disconnected.addEdge(0, 1, 5);
        disconnected.addEdge(2, 3, 3);

        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(disconnected, metrics);
        double[] distances = dagsp.computeShortestPaths(0);

        assertEquals(0, distances[0]);
        assertEquals(5, distances[1]);
        assertEquals(Double.POSITIVE_INFINITY, distances[2]);
        assertEquals(Double.POSITIVE_INFINITY, distances[3]);
    }

    @Test
    public void testExecutionTimeRecorded() {
        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(simpleDAG, metrics);
        dagsp.computeShortestPaths(0);

        assertTrue(metrics.getExecutionTimeNanos() > 0);
    }

    @Test
    public void testMetricsCounter() {
        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPaths dagsp = new DAGShortestPaths(simpleDAG, metrics);
        dagsp.computeShortestPaths(0);

        assertTrue(metrics.getCounter("relaxations") > 0);
    }
}

