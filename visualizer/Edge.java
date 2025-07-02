package visualizer;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Edge extends JComponent implements Comparable<Edge> {

    private Vertex vertex1, vertex2;
    private int weight;
    private boolean topEqualsLeft;
    private Color color = Vertex.DEFAULT_COLOR;
    private boolean highlight = false;

    public static java.util.List<Edge> edges = new ArrayList<>();

    public Edge(Vertex v1, Vertex v2, int weight) {
        this.vertex1 = v1;
        this.vertex2 = v2;
        this.weight = weight;
        this.setName("Edge <" + v1.getId() + " -> " + v2.getId() + ">");
        this.setLayout(null);
        this.topEqualsLeft = setEdgeBounds();
        edges.add(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(highlight ? Color.ORANGE : color);
        int x1 = 0;
        int y1 = topEqualsLeft ? 0 : getHeight();
        int x2 = getWidth();
        int y2 = topEqualsLeft ? getHeight() : 0;
        g2.drawLine(x1, y1, x2, y2);

        // Draw the weight in the middle of the edge
        String weightStr = String.valueOf(weight);
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(weightStr);
        int textHeight = fm.getHeight();

        int midX = (x1 + x2) / 2 - textWidth / 2;
        int midY = (y1 + y2) / 2 + textHeight / 4;
        g2.drawString(weightStr, midX, midY);
    }

    private boolean setEdgeBounds() {
        Vertex top = vertex1.getYLocation() < vertex2.getYLocation() ? vertex1 : vertex2;
        Vertex left = vertex1.getXLocation() < vertex2.getXLocation() ? vertex1 : vertex2;
        int extra = Vertex.SIZE / 2;

        int width = Math.abs(vertex1.getXLocation() - vertex2.getXLocation());
        int height = Math.abs(vertex1.getYLocation() - vertex2.getYLocation());

        setBounds(left.getXLocation() + extra - 2, top.getYLocation() + extra - 2, width + 4, height + 4);
        return top == left;
    }

    public void highlightEdge(boolean highlight) {
        this.highlight = highlight;
        repaint();
    }

    public Vertex getVertex1() { return vertex1; }
    public Vertex getVertex2() { return vertex2; }
    public int getWeight() { return weight; }
    public boolean getTopEqualsLeft() { return topEqualsLeft; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge other = (Edge) o;
        return (vertex1.equals(other.vertex1) && vertex2.equals(other.vertex2)) ||
                (vertex1.equals(other.vertex2) && vertex2.equals(other.vertex1));
    }

    @Override
    public int hashCode() {
        return vertex1.hashCode() + vertex2.hashCode();
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }
}
