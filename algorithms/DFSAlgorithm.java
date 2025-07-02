package algorithms;

import visualizer.Edge;
import visualizer.Vertex;

import java.util.*;

public class DFSAlgorithm implements GraphAlgorithm {

    private final List<Vertex> traversalPath = new ArrayList<>();

    public List<Vertex> getTraversalPath() {
        return traversalPath;
    }

    @Override
    public String run(Map<Vertex, List<Edge>> graph, Vertex start) {

        traversalPath.clear(); // Reset previous path

        // If the chosen vertex doesn't have any edges, then just return it
        if (graph.get(start).isEmpty()) {
            traversalPath.add(start);
            return "DFS -> " + start.getId();
        }

        StringBuilder traversal = new StringBuilder("DFS : ");
        Set<Vertex> visited = new HashSet<>();

        traversal.append(helper(graph, start, visited));

        // For disconnected graphs
        for (Vertex vertex : graph.keySet()) {
            if (!visited.contains(vertex) && !graph.get(vertex).isEmpty()) {
                traversal.append(helper(graph, vertex, visited));
            }
        }

        return traversal.substring(0, traversal.length() - 4); // Remove last arrow
    }

    private String helper(Map<Vertex, List<Edge>> graph, Vertex vertex, Set<Vertex> visited) {
        StringBuilder output = new StringBuilder();

        visited.add(vertex);
        traversalPath.add(vertex); // For animation
        output.append(processVertex(vertex));

        List<Edge> edges = graph.get(vertex);
        Collections.sort(edges); // Lowest weight first

        for (Edge edge : edges) {
            Vertex neighbor = edge.getVertex2();
            if (!visited.contains(neighbor)) {
                output.append(helper(graph, neighbor, visited));
            }
        }

        return output.toString();
    }

    public String processVertex(Vertex vertex) {
        return vertex.getId() + " -> ";
    }
}
