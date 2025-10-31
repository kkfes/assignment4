package graph.topo;

import graph.util.Graph;
import graph.util.MetricsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for topological sorting algorithms.
 */
public class TopoTest {
    private Graph simpleDAG;
    private Graph complexDAG;
    private Graph graphWithCycle;

    @BeforeEach
    public void setUp() {
        // Simple DAG: 0->1->2->3
        simpleDAG = new Graph(4);
        simpleDAG.addEdge(0, 1);
        simpleDAG.addEdge(1, 2);
        simpleDAG.addEdge(2, 3);

        // Complex DAG with multiple paths
        complexDAG = new Graph(6);
        complexDAG.addEdge(0, 1);
        complexDAG.addEdge(0, 2);
        complexDAG.addEdge(1, 3);
        complexDAG.addEdge(2, 3);
        complexDAG.addEdge(3, 4);
        complexDAG.addEdge(4, 5);

        // Graph with cycle
        graphWithCycle = new Graph(3);
        graphWithCycle.addEdge(0, 1);
        graphWithCycle.addEdge(1, 2);
        graphWithCycle.addEdge(2, 0);
    }

    @Test
    public void testKahnSimpleDAG() {
        MetricsImpl metrics = new MetricsImpl();
        TopoKahn topo = new TopoKahn(simpleDAG, metrics);
        List<Integer> order = topo.computeTopologicalOrder();

        assertEquals(4, order.size());
        assertEquals(0, order.get(0));
        assertEquals(1, order.get(1));
        assertEquals(2, order.get(2));
        assertEquals(3, order.get(3));
        assertFalse(topo.hasCycle());
    }

    @Test
    public void testKahnComplexDAG() {
        MetricsImpl metrics = new MetricsImpl();
        TopoKahn topo = new TopoKahn(complexDAG, metrics);
        List<Integer> order = topo.computeTopologicalOrder();

        assertEquals(6, order.size());
        // Verify topological ordering
        for (int u = 0; u < complexDAG.getVertexCount(); u++) {
            for (Graph.Edge edge : complexDAG.getAdjacentEdges(u)) {
                int uIdx = order.indexOf(u);
                int vIdx = order.indexOf(edge.to);
                assertTrue(uIdx < vIdx);
            }
        }
    }

    @Test
    public void testKahnWithCycle() {
        MetricsImpl metrics = new MetricsImpl();
        TopoKahn topo = new TopoKahn(graphWithCycle, metrics);
        List<Integer> order = topo.computeTopologicalOrder();

        assertTrue(order.isEmpty());
        assertTrue(topo.hasCycle());
    }

    @Test
    public void testDFSSimpleDAG() {
        MetricsImpl metrics = new MetricsImpl();
        TopoDFS topo = new TopoDFS(simpleDAG, metrics);
        List<Integer> order = topo.computeTopologicalOrder();

        assertEquals(4, order.size());
        // Verify topological ordering
        for (int u = 0; u < simpleDAG.getVertexCount(); u++) {
            for (Graph.Edge edge : simpleDAG.getAdjacentEdges(u)) {
                int uIdx = order.indexOf(u);
                int vIdx = order.indexOf(edge.to);
                assertTrue(uIdx < vIdx);
            }
        }
        assertFalse(topo.hasCycle());
    }

    @Test
    public void testDFSComplexDAG() {
        MetricsImpl metrics = new MetricsImpl();
        TopoDFS topo = new TopoDFS(complexDAG, metrics);
        List<Integer> order = topo.computeTopologicalOrder();

        assertEquals(6, order.size());
        assertFalse(topo.hasCycle());
    }

    @Test
    public void testDFSWithCycle() {
        MetricsImpl metrics = new MetricsImpl();
        TopoDFS topo = new TopoDFS(graphWithCycle, metrics);
        List<Integer> order = topo.computeTopologicalOrder();

        assertTrue(order.isEmpty());
        assertTrue(topo.hasCycle());
    }

    @Test
    public void testSingleVertex() {
        Graph single = new Graph(1);
        MetricsImpl metrics = new MetricsImpl();
        TopoKahn topo = new TopoKahn(single, metrics);
        List<Integer> order = topo.computeTopologicalOrder();

        assertEquals(1, order.size());
        assertEquals(0, order.get(0));
    }

    @Test
    public void testDisconnectedDAG() {
        Graph disconnected = new Graph(4);
        disconnected.addEdge(0, 1);
        disconnected.addEdge(2, 3);

        MetricsImpl metrics = new MetricsImpl();
        TopoKahn topo = new TopoKahn(disconnected, metrics);
        List<Integer> order = topo.computeTopologicalOrder();

        assertEquals(4, order.size());
        int idx0 = order.indexOf(0);
        int idx1 = order.indexOf(1);
        int idx2 = order.indexOf(2);
        int idx3 = order.indexOf(3);
        assertTrue(idx0 < idx1);
        assertTrue(idx2 < idx3);
    }

    @Test
    public void testEmptyGraph() {
        Graph empty = new Graph(0);
        MetricsImpl metrics = new MetricsImpl();
        TopoKahn topo = new TopoKahn(empty, metrics);
        List<Integer> order = topo.computeTopologicalOrder();

        assertEquals(0, order.size());
    }
}

