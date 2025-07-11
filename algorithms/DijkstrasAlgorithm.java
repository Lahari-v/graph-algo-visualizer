package algorithms;

import visualizer.Edge;
import visualizer.Vertex;
import java.util.*;

public class DijkstrasAlgorithm implements GraphAlgorithm {

    private List<Vertex> traversalPath = new ArrayList<>();

    @Override
    public String run(Map<Vertex, List<Edge>> graph, Vertex start) {

        boolean useAI = visualizer.Graph.isAIEnabled();
        visualizer.LearningPathOptimizer aiAgent = new visualizer.LearningPathOptimizer(visualizer.Graph.edgeUsage, 0.5);

        Map<Vertex, Double> outputMap = new TreeMap<>();
        Map<Vertex, Double> distances = new HashMap<>();
        Map<Vertex, Vertex> previous = new HashMap<>();

        for (Vertex vertex : graph.keySet()) {
            if (!vertex.equals(start)) distances.put(vertex, Double.POSITIVE_INFINITY);
        }

        Set<Vertex> unprocessedVertices = new HashSet<>(distances.keySet());
        List<Edge> startVertexEdges = graph.get(start);

        for (Edge edge : startVertexEdges) {
            Vertex neighbor = edge.getVertex2();
            int weight = useAI
                    ? aiAgent.getAdjustedWeight(start, neighbor, edge.getWeight())
                    : edge.getWeight();

            if (unprocessedVertices.contains(neighbor)) {
                double newDistance = (double) weight;
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, start);
                }
            }
        }

        while (!unprocessedVertices.isEmpty()) {
            Vertex current = findSmallestDistanceVertex(unprocessedVertices, distances);

            traversalPath.add(current); // ✅ Track visited vertex for animation

            outputMap.put(current, distances.get(current));
            unprocessedVertices.remove(current);

            List<Edge> currentVertexEdges = graph.get(current);
            for (Edge edge : currentVertexEdges) {
                Vertex neighbor = edge.getVertex2();
                int weight = useAI
                        ? aiAgent.getAdjustedWeight(current, neighbor, edge.getWeight())
                        : edge.getWeight();

                if (unprocessedVertices.contains(neighbor)) {
                    double newDistance = distances.get(current) + weight;
                    if (newDistance < distances.get(neighbor)) {
                        distances.put(neighbor, newDistance);
                        previous.put(neighbor, current);
                    }
                }
            }
        }

        // AI edge usage tracking
        for (Vertex target : previous.keySet()) {
            Vertex current = target;
            while (previous.containsKey(current)) {
                Vertex source = previous.get(current);
                String edgeKey = source.getId() + "->" + current.getId();
                visualizer.Graph.edgeUsage.put(edgeKey, visualizer.Graph.edgeUsage.getOrDefault(edgeKey, 0) + 1);
                current = source;
            }
        }

        String shortestPaths = processDistances(outputMap);
        return shortestPaths.substring(0, shortestPaths.length() - 2);
    }

    private static Vertex findSmallestDistanceVertex(Set<Vertex> unprocessedVertices, Map<Vertex, Double> distances) {
        Vertex smallestVertex = null;
        double smallestDistance = Double.POSITIVE_INFINITY;

        for (Vertex vertex : unprocessedVertices) {
            if (distances.get(vertex) <= smallestDistance) {
                smallestVertex = vertex;
                smallestDistance = distances.get(vertex);
            }
        }

        return smallestVertex;
    }

    private String processDistances(Map<Vertex, Double> map) {
        String shortestPaths = "";
        for (Vertex vertex : map.keySet()) {
            Double weight = map.get(vertex);
            if (weight == Double.POSITIVE_INFINITY) {
                shortestPaths += vertex.getId() + "=" + weight + ", ";
            } else {
                shortestPaths += vertex.getId() + "=" + weight.intValue() + ", ";
            }
        }
        return shortestPaths;
    }

    @Override
    public List<Vertex> getTraversalPath() {
        return traversalPath;
    }
}
