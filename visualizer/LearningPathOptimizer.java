package visualizer;

import visualizer.Vertex;
import visualizer.Edge;
import java.util.*;

public class LearningPathOptimizer {

    private final Map<String, Integer> edgeUsage;
    private final double learningRate;

    public LearningPathOptimizer(Map<String, Integer> edgeUsage, double learningRate) {
        this.edgeUsage = edgeUsage;
        this.learningRate = learningRate;
    }

    public int getAdjustedWeight(Vertex from, Vertex to, int originalWeight) {
        String key = from.getId() + "->" + to.getId();
        int frequency = edgeUsage.getOrDefault(key, 0);
        int adjustment = (int) (frequency * learningRate);
        return Math.max(1, originalWeight - adjustment); // Ensure weight stays >= 1
    }
}
