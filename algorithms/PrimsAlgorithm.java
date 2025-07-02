package algorithms;

import visualizer.Edge;
import visualizer.Vertex;

import java.util.*;

public class PrimsAlgorithm implements GraphAlgorithm {

    private final List<Vertex> traversalPath = new ArrayList<>();

    public List<Vertex> getTraversalPath() {
        return traversalPath;
    }

    @Override
    public String run(Map<Vertex, List<Edge>> graph, Vertex start) {

        traversalPath.clear(); // Clear previous animation path

        if (graph.get(start).isEmpty()) {
            traversalPath.add(start);
            return start.getId();
        }

        List<Edge> edgesOfMST = new ArrayList<>();
        Set<Vertex> verticesOfMST = new HashSet<>();
        verticesOfMST.add(start);
        traversalPath.add(start); // First node is added to path

        Set<Vertex> connectedVertices = findConnectedVertices(graph, start);

        while (verticesOfMST.size() != connectedVertices.size()) {
            Edge lowestWeightEdge = findLowestEdgeWeight(graph, verticesOfMST);

            if (lowestWeightEdge == null) break; // Fail-safe

            edgesOfMST.add(lowestWeightEdge);

            Vertex newVertex = lowestWeightEdge.getVertex2();
            if (!verticesOfMST.contains(newVertex)) {
                verticesOfMST.add(newVertex);
                traversalPath.add(newVertex); // Track this for animation
            }
        }

        return processEdgesOfMST(edgesOfMST);
    }

    private static Edge findLowestEdgeWeight(Map<Vertex, List<Edge>> graph, Set<Vertex> verticesOfMST) {
        Edge correctEdge = null;
        double smallestWeight = Double.POSITIVE_INFINITY;

        for (Vertex vertex : verticesOfMST) {
            for (Edge edge : graph.get(vertex)) {
                double weight = (double) edge.getWeight();
                Vertex target = edge.getVertex2();
                if (weight < smallestWeight && !verticesOfMST.contains(target)) {
                    smallestWeight = weight;
                    correctEdge = edge;
                }
            }
        }

        return correctEdge;
    }

    private static Set<Vertex> findConnectedVertices(Map<Vertex, List<Edge>> graph, Vertex start) {
        Set<Vertex> reachableVertices = new HashSet<>();
        Set<Vertex> visited = new HashSet<>();
        Queue<Vertex> queue = new LinkedList<>();

        visited.add(start);
        queue.offer(start);

        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            reachableVertices.add(current);

            for (Edge edge : graph.get(current)) {
                Vertex neighbor = edge.getVertex2();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }

        return reachableVertices;
    }

    private static String processEdgesOfMST(List<Edge> edgesOfMST) {
        StringBuilder output = new StringBuilder();

        ChildEdgesInAlphabeticalOrder comparator = new ChildEdgesInAlphabeticalOrder();
        edgesOfMST.sort(comparator);

        for (Edge edge : edgesOfMST) {
            output.append(edge.getVertex2().getId()).append("=").append(edge.getVertex1().getId()).append(", ");
        }

        return output.substring(0, output.length() - 2);
    }

    static class ChildEdgesInAlphabeticalOrder implements Comparator<Edge> {
        @Override
        public int compare(Edge edge1, Edge edge2) {
            Vertex child1 = edge1.getVertex2();
            Vertex child2 = edge2.getVertex2();
            return String.valueOf(child1.getId()).compareTo(child2.getId());
        }
    }
}
