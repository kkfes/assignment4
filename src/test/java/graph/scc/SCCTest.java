package graph.scc;

import graph.util.Graph;
import graph.util.MetricsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for SCC algorithms.
 */
public class SCCTest {
    private Graph graphWithCycle;
    private Graph simpleDAG;
    private Graph singleComponent;

    @BeforeEach
    public void setUp() {
        // Graph with one cycle: 0->1->2->0
        graphWithCycle = new Graph(4);
        graphWithCycle.addEdge(0, 1);
        graphWithCycle.addEdge(1, 2);
        graphWithCycle.addEdge(2, 0);
        graphWithCycle.addEdge(2, 3);

        // Simple DAG: 0->1->2->3
        simpleDAG = new Graph(4);
        simpleDAG.addEdge(0, 1);
        simpleDAG.addEdge(1, 2);
        simpleDAG.addEdge(2, 3);

        // Single component: all connected
        singleComponent = new Graph(3);
        singleComponent.addEdge(0, 1);
        singleComponent.addEdge(1, 2);
        singleComponent.addEdge(2, 0);
    }

    @Test
    public void testTarjanWithCycle() {
        MetricsImpl metrics = new MetricsImpl();
        SCCTarjan scc = new SCCTarjan(graphWithCycle, metrics);
        List<List<Integer>> components = scc.findSCCs();

        assertNotNull(components);
        assertEquals(2, components.size()); // One SCC with cycle, one singleton
        assertTrue(components.stream().anyMatch(c -> c.size() == 3)); // SCC of size 3
        assertTrue(components.stream().anyMatch(c -> c.size() == 1)); // Singleton
    }

    @Test
    public void testTarjanDAG() {
        MetricsImpl metrics = new MetricsImpl();
        SCCTarjan scc = new SCCTarjan(simpleDAG, metrics);
        List<List<Integer>> components = scc.findSCCs();

        assertNotNull(components);
        assertEquals(4, components.size()); // Each vertex is its own SCC
        for (List<Integer> component : components) {
            assertEquals(1, component.size());
        }
    }

    @Test
    public void testTarjanSingleComponent() {
        MetricsImpl metrics = new MetricsImpl();
        SCCTarjan scc = new SCCTarjan(singleComponent, metrics);
        List<List<Integer>> components = scc.findSCCs();

        assertNotNull(components);
        assertEquals(1, components.size());
        assertEquals(3, components.get(0).size());
    }

    @Test
    public void testKosarajuWithCycle() {
        MetricsImpl metrics = new MetricsImpl();
        SCCKosaraju scc = new SCCKosaraju(graphWithCycle, metrics);
        List<List<Integer>> components = scc.findSCCs();

        assertNotNull(components);
        assertEquals(2, components.size());
        assertTrue(components.stream().anyMatch(c -> c.size() == 3));
        assertTrue(components.stream().anyMatch(c -> c.size() == 1));
    }

    @Test
    public void testKosarajuDAG() {
        MetricsImpl metrics = new MetricsImpl();
        SCCKosaraju scc = new SCCKosaraju(simpleDAG, metrics);
        List<List<Integer>> components = scc.findSCCs();

        assertNotNull(components);
        assertEquals(4, components.size());
        for (List<Integer> component : components) {
            assertEquals(1, component.size());
        }
    }

    @Test
    public void testCondensationGraph() {
        MetricsImpl metrics = new MetricsImpl();
        SCCTarjan scc = new SCCTarjan(graphWithCycle, metrics);
        List<List<Integer>> components = scc.findSCCs();
        CondensationGraph condensation = new CondensationGraph(components, graphWithCycle);

        assertEquals(2, condensation.getComponentCount());
        assertFalse(condensation.getAdjacentComponents(0).isEmpty());
    }

    @Test
    public void testEmptyGraph() {
        Graph empty = new Graph(0);
        MetricsImpl metrics = new MetricsImpl();
        SCCTarjan scc = new SCCTarjan(empty, metrics);
        List<List<Integer>> components = scc.findSCCs();

        assertEquals(0, components.size());
    }

    @Test
    public void testSingleVertex() {
        Graph single = new Graph(1);
        MetricsImpl metrics = new MetricsImpl();
        SCCTarjan scc = new SCCTarjan(single, metrics);
        List<List<Integer>> components = scc.findSCCs();

        assertEquals(1, components.size());
        assertEquals(1, components.get(0).size());
        assertTrue(components.get(0).contains(0));
    }

    @Test
    public void testComponentSizes() {
        MetricsImpl metrics = new MetricsImpl();
        SCCTarjan scc = new SCCTarjan(graphWithCycle, metrics);
        scc.findSCCs();
        List<Integer> sizes = scc.getComponentSizes();

        assertNotNull(sizes);
        assertEquals(2, sizes.size());
        assertTrue(sizes.contains(3));
        assertTrue(sizes.contains(1));
    }
}

