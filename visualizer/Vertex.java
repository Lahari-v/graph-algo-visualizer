package visualizer;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Vertex extends JPanel implements Comparable<Vertex> {

    public static final int SIZE = 50;
    public static final Color DEFAULT_COLOR = Color.WHITE;
    public static final Map<String, Vertex> vertices = new HashMap<>();

    private String id;
    private JLabel label;
    private int xLocation, yLocation;

    public Vertex(int x, int y, String id) {
        this.id = id;
        this.xLocation = x;
        this.yLocation = y;
        vertices.put(this.id, this);

        label = new JLabel(this.id, SwingConstants.CENTER);
        label.setBounds(0, 0, SIZE, SIZE);
        label.setForeground(Color.BLACK);
        this.add(label);


        this.setName("Vertex " + this.id);
        this.setBackground(DEFAULT_COLOR);
        this.setOpaque(false);
        this.setLayout(null);
        this.setBounds(x, y, SIZE, SIZE);
    }

    public int getXLocation() { return this.xLocation; }
    public int getYLocation() { return this.yLocation; }
    public String getId() { return this.id; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Transparent background
        g2d.setColor(getBackground());
        g2d.fillOval(0, 0, SIZE, SIZE);

        g2d.setColor(Color.BLACK);
        g2d.drawOval(0, 0, SIZE, SIZE);
    }


    public void resetColor() {
        setBackground(DEFAULT_COLOR);
        repaint();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vertex)) return false;
        Vertex other = (Vertex) obj;
        return id.equals(other.id) && xLocation == other.xLocation && yLocation == other.yLocation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, xLocation, yLocation);
    }

    @Override
    public int compareTo(Vertex other) {
        return this.id.compareTo(other.id);
    }
}
