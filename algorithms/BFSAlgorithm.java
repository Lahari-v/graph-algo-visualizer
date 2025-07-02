package algorithms;

import visualizer.Edge;
import visualizer.Vertex;

import java.util.*;

public class BFSAlgorithm implements GraphAlgorithm {

    private List<Vertex> traversalPath = new ArrayList<>();

    @Override
    public String run(Map<Vertex, List<Edge>> graph, Vertex start) {
        String traversalPathStr = "BFS : ";
        Set<Vertex> visited = new HashSet<>();
        Queue<Vertex> queue = new LinkedList<>();

        visited.add(start);
        queue.offer(start);

        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            traversalPath.add(current); // Track the path for animation

            traversalPathStr += processVertex(current);

            List<Edge> currentVertexEdges = graph.get(current);
            Collections.sort(currentVertexEdges);

            for (Edge edge : currentVertexEdges) {
                Vertex neighbor = edge.getVertex2();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }

        return traversalPathStr.substring(0, traversalPathStr.length() - 4);
    }

    @Override
    public List<Vertex> getTraversalPath() {
        return traversalPath;
    }

    public String processVertex(Vertex vertex) {
        return vertex.getId() + " → ";
    }
}
