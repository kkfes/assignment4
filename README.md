# Assignment 4: Smart City/Campus Scheduling - Graph Algorithms

## Project Overview

This project implements a comprehensive solution for analyzing task dependency graphs in smart city/campus scheduling scenarios using three fundamental graph algorithms:

1. **Strongly Connected Components (SCC)** - Detect and compress cyclic dependencies
2. **Topological Sorting** - Order tasks respecting all dependencies
3. **Shortest Paths in DAGs** - Find optimal schedules and identify critical paths

## Architecture

```
assignment4/
├── src/main/java/
│   ├── graph/
│   │   ├── util/              # Graph representation, Metrics, JSON utilities
│   │   ├── scc/               # Tarjan & Kosaraju SCC algorithms
│   │   ├── topo/              # Kahn & DFS topological sorting
│   │   └── dagsp/             # DAG shortest and longest paths
│   └── main/                  # Analysis tools and demonstrations
├── src/test/java/
│   ├── graph/scc/             # SCC algorithm tests
│   ├── graph/topo/            # Topological sort tests
│   └── graph/dagsp/           # DAG shortest paths tests
├── data/                       # 9 test datasets (JSON format)
├── pom.xml                     # Maven configuration
└── Documentation files
```

## Core Components

### 1. Strongly Connected Components (SCC)

**Two Implementations:**
- **Tarjan's Algorithm** - Single-pass DFS with stack
  - Time: O(V + E) | Space: O(V)
  - More efficient in practice
  
- **Kosaraju's Algorithm** - Two-pass DFS approach
  - Time: O(V + E) | Space: O(V)
  - Easier to understand

**Output:**
- List of all strongly connected components
- Component sizes and distribution
- Condensation graph (DAG of components)

**Use Case:** Detect circular task dependencies that must be resolved before scheduling

### 2. Topological Sorting

**Two Approaches:**
- **Kahn's Algorithm** - BFS-based with in-degree tracking
  - Better for dense graphs
  
- **DFS-based** - Recursive with vertex coloring
  - Better for sparse graphs

**Output:**
- Valid ordering of tasks
- Cycle detection
- Task execution order for DAGs

**Use Case:** Determine valid execution order respecting all dependencies

### 3. Shortest Paths in DAGs

**Features:**
- Single-source shortest paths via DP over topological order
- Longest paths (critical path analysis)
- Path reconstruction and visualization

**Time Complexity:** O(V + E)

**Output:**
- Distance matrix from source to all vertices
- Critical path (longest path) and its length
- Reconstructed optimal paths

**Use Case:** Find project bottlenecks and minimum completion time

## Weight Model

**Selected: Node Duration Model (edge weights)**

Each edge weight represents the duration/cost of completing the destination task:
- Edge (u, v) with weight w = time units to complete task v after u finishes
- Weights range: 1-5 units (representing days, hours, or abstract units)
- Used for both shortest path and critical path computation

## Datasets Summary

### Generated Datasets (9 total)

#### Small Category (6-10 vertices, 3 datasets)

1. **small_cyclic_1.json** (8v, 9e)
   - Two independent cyclic components
   - Type: Cyclic
   - Density: 0.143
   - Purpose: Test SCC detection

2. **small_dag_1.json** (7v, 6e)
   - Linear pipeline structure
   - Type: Pure DAG
   - Density: 0.143
   - Purpose: Test basic topological sort

3. **small_dag_2.json** (10v, 9e)
   - Multiple independent chains converging
   - Type: Sparse DAG
   - Density: 0.100
   - Purpose: Test multi-path scenarios

#### Medium Category (10-20 vertices, 3 datasets)

4. **medium_multiple_scc.json** (15v, 20e) ⭐ **REQUIRED**
   - Multiple SCCs with cross-connections
   - Type: Mixed cyclic/acyclic
   - Density: 0.095
   - Purpose: Complex SCC structures

5. **medium_sparse_dag.json** (16v, 21e)
   - Layered DAG with sparse connections
   - Type: Sparse DAG
   - Density: 0.088
   - Purpose: Sparse graph performance

6. **medium_dense_cyclic.json** (18v, 25e)
   - Dense graph with multiple cycles
   - Type: Dense cyclic
   - Density: 0.082
   - Purpose: Complex cyclic structures

#### Large Category (20-50 vertices, 3 datasets)

7. **large_sparse_dag.json** (30v, 45e)
   - Layered sparse DAG
   - Type: Sparse DAG
   - Density: 0.052
   - Purpose: Performance testing on sparse graphs

8. **large_dense_dag.json** (35v, 90e)
   - Dense DAG with 60% edge probability
   - Type: Dense DAG
   - Density: 0.076
   - Purpose: Performance testing on dense graphs

9. **large_complex_scc.json** (40v, 50e)
   - Multiple large SCCs with inter-component edges
   - Type: Complex cyclic
   - Density: 0.032
   - Purpose: Complex SCC distribution

## Installation & Quick Start

### Requirements
- Java 21+
- Maven 3.6+

### Build
```bash
cd assignment4
mvn clean compile
```

### Run Analysis
```bash
# Generate datasets and analyze all
mvn exec:java -Dexec.mainClass="main.AlgorithmAnalysis"

# Run integration test
mvn exec:java -Dexec.mainClass="main.IntegrationTest"

# Interactive mode
mvn exec:java -Dexec.mainClass="main.InteractiveAnalyzer"

# Performance benchmarks
mvn exec:java -Dexec.mainClass="main.PerformanceBenchmark"
```

### Run Tests
```bash
mvn test                                          # All tests
mvn test -Dtest=SCCTest                          # SCC tests
mvn test -Dtest=TopoTest                         # Topology tests
mvn test -Dtest=DAGShortestPathsTest             # DAG tests
```

## Code Quality

### Metrics & Instrumentation

**Common Metrics Interface:**
- Counter tracking for operations
- Timing via System.nanoTime()
- Summary generation

**Tracked Metrics:**
- **SCC:** DFS visits, edges examined, execution time
- **Topo Sort:** Pushes, pops, edges examined, execution time
- **DAG-SP:** Relaxations, execution time

### Testing

**Coverage: 25+ unit tests**
- SCCTest: 10 test methods
- TopoTest: 10 test methods
- DAGShortestPathsTest: 10 test methods

**Test Categories:**
- Basic functionality
- Edge cases (empty graphs, single vertices)
- Cycle detection
- Path reconstruction
- Integration tests

### Documentation

**Code Quality:**
- All classes documented with Javadoc
- Key algorithm steps commented
- Clean package structure
- Design patterns applied
- Readable and maintainable

**Documentation:**
- README.md - This guide
- REPORT.md - Detailed analysis
- API_REFERENCE.md - Class documentation
- QUICKSTART.md - Quick start guide
- INSTALLATION.md - Setup instructions
- PROJECT_SUMMARY.md - Requirements checklist
- FILE_MANIFEST.md - File organization
- DOCUMENTATION_INDEX.md - Navigation guide

## Algorithm Complexity Analysis

| Algorithm | Time | Space | Notes |
|-----------|------|-------|-------|
| Tarjan SCC | O(V+E) | O(V) | Single-pass, stack-based |
| Kosaraju SCC | O(V+E) | O(V) | Two-pass, requires transpose |
| Kahn Topo Sort | O(V+E) | O(V) | BFS-based in-degree tracking |
| DFS Topo Sort | O(V+E) | O(V) | Recursive with coloring |
| DAG Shortest Paths | O(V+E) | O(V) | DP over topological order |
| DAG Longest Paths | O(V+E) | O(V) | Same as shortest, max instead of min |

## Results Summary

### Performance on Test Datasets

**Small Datasets (≤10 vertices):**
- Execution: <0.001 ms
- Purpose: Correctness verification

**Medium Datasets (10-20 vertices):**
- Execution: 0.001-0.002 ms
- Effect: Differences emerge with complexity

**Large Datasets (20-50 vertices):**
- Execution: 0.002-0.005 ms
- Effect: Edge density impacts performance

### Key Findings

1. **SCC Detection**
   - All cyclic dependencies correctly identified
   - Tarjan outperforms Kosaraju on sparse graphs
   - Component sizes vary significantly

2. **Topological Sorting**
   - Valid orderings produced for all DAGs
   - Cycles correctly detected
   - Kahn's algorithm faster for dense graphs

3. **Critical Path Analysis**
   - Project bottlenecks identified
   - Critical paths range 5-12 time units on test data
   - Path reconstruction successful

### Practical Recommendations

**Choose Tarjan's SCC when:**
- Production system required
- Sparse to medium graphs
- Performance critical

**Choose Kosaraju's SCC when:**
- Educational context
- Code clarity important
- Team unfamiliar with Tarjan

**Choose Kahn's Topo Sort when:**
- Need cycle detection during execution
- Dense graphs
- Queue-based processing preferred

**Choose DFS Topo Sort when:**
- Sparse graphs
- Minimal extra space needed
- Recursive approach preferred

**Use DAG Shortest Paths when:**
- Project scheduling needed
- Critical path analysis required
- Must guarantee acyclic graph

## Real-World Application: Smart Campus Scheduling

**Scenario:** Schedule semester coursework and deadlines optimally

**Step 1: SCC Analysis**
- Detect circular dependencies (e.g., Project A depends on B, B depends on A)
- If found: Redesign workflow or merge conflicting tasks
- If not: Proceed to Step 2

**Step 2: Topological Sort**
- Generate valid execution order
- Use as baseline schedule
- Identify dependency chains

**Step 3: Critical Path Analysis**
- Find longest dependency chain
- Identify bottleneck tasks
- Allocate resources to critical path

**Step 4: Minimum Duration Calculation**
- Compute earliest start times
- Detect parallelization opportunities
- Optimize resource allocation

## Example Usage

### Load Dataset
```java
Graph graph = GraphJsonUtils.loadGraphFromJson("data/small_dag_1.json");
```

### Find SCCs
```java
Metrics metrics = new MetricsImpl();
SCCTarjan scc = new SCCTarjan(graph, metrics);
List<List<Integer>> components = scc.findSCCs();
System.out.println("Found " + components.size() + " components");
```

### Topological Sort
```java
TopoKahn topo = new TopoKahn(graph, metrics);
List<Integer> order = topo.computeTopologicalOrder();
if (!topo.hasCycle()) {
    System.out.println("Valid order: " + order);
}
```

### Critical Path Analysis
```java
DAGShortestPaths dagsp = new DAGShortestPaths(graph, metrics);
var critPath = dagsp.findCriticalPath(0);
System.out.println("Critical path: " + critPath.getValue());
System.out.println("Length: " + critPath.getKey() + " time units");
```


