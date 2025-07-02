package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

import algorithms.*;

public class Graph extends JPanel implements MouseListener {

    public static Map<String, Integer> edgeUsage = new HashMap<>();
    protected static List<Vertex> edgeVertices = new ArrayList<>();
    protected static List<List<String>> availableEdges = new ArrayList<>();

    private static boolean aiEnabled = false;

    public static void setAIEnabled(boolean value) {
        aiEnabled = value;
    }

    public static boolean isAIEnabled() {
        return aiEnabled;
    }

    public Graph() {
        setName("Graph");
        setBackground(MainFrame.BACKGROUND_COLOR);
        setLayout(null);
        setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
        setLocation(0, 0);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (MainFrame.mode == Mode.ADD_A_VERTEX) {
            boolean validPosition = validPlacementForVertex(e.getX(), e.getY());
            if (validPosition) {
                while (true) {
                    String input = JOptionPane.showInputDialog(this, "Enter the Vertex ID (Should be 1 char):",
                            "Vertex", JOptionPane.QUESTION_MESSAGE);
                    if (input == null) break;
                    input = input.trim();
                    if (input.length() == 1 && validVertexID(input)) {
                        int xValue = e.getX() - Vertex.SIZE / 2;
                        int yValue = e.getY() - Vertex.SIZE / 2;
                        createVertex(xValue, yValue, input);
                        return;
                    }
                }
            }
        } else if (MainFrame.mode == Mode.ADD_AN_EDGE) {
            Vertex vertex = clickedOnVertex(e.getX(), e.getY());
            if (vertex != null) {
                edgeVertices.add(vertex);
                if (edgeVertices.size() == 2) {
                    for (List<String> edgeList : availableEdges) {
                        String id1 = edgeVertices.get(0).getId();
                        String id2 = edgeVertices.get(1).getId();
                        if (edgeList.contains(id1) && edgeList.contains(id2)) {
                            edgeVertices.clear();
                            return;
                        }
                    }
                    Vertex v1 = edgeVertices.get(0);
                    Vertex v2 = edgeVertices.get(1);
                    edgeVertices.clear();
                    availableEdges.add(Arrays.asList(v1.getId(), v2.getId()));
                    drawEdge(v1, v2);
                }
            }
        } else if (MainFrame.mode == Mode.REMOVE_A_VERTEX) {
            Vertex vertex = clickedOnVertex(e.getX(), e.getY());
            if (vertex != null) {
                Vertex.vertices.remove(vertex.getId());
                List<Edge> edges = new ArrayList<>();
                for (Edge edge : Edge.edges) {
                    Vertex v1 = edge.getVertex1();
                    Vertex v2 = edge.getVertex2();
                    if (vertex.equals(v1) || vertex.equals(v2)) {
                      //  if (edge.getLabel() != null) this.remove(edge.getLabel());
                        this.remove(edge);
                        String id1 = v1.getId();
                        String id2 = v2.getId();
                        availableEdges.removeIf(pair -> pair.contains(id1) && pair.contains(id2));
                    } else {
                        edges.add(edge);
                    }
                }
                Edge.edges = edges;
                this.remove(vertex);
                this.repaint();
            }
        } else if (MainFrame.mode == Mode.REMOVE_AN_EDGE) {
            Edge edge = clickedOnEdge(e.getX(), e.getY());
            if (edge != null) {
                List<Edge> updatedEdges = new ArrayList<>();
                for (Edge e1 : Edge.edges) {
                    if (e1.equals(edge)) {
                        this.remove(e1);
                    //    if (e1.getLabel() != null) this.remove(e1.getLabel());
                        removeEdgeFromStaticList(e1);
                    } else {
                        updatedEdges.add(e1);
                    }
                }
                Edge.edges = updatedEdges;
                this.repaint();
            }
        } else if (MainFrame.mode == Mode.NONE && MainFrame.getAlgorithmDisplayLabel().isVisible()) {
            Vertex vertex = clickedOnVertex(e.getX(), e.getY());
            if (vertex != null) {
                // RESET VERTICES
                for (Vertex v : Vertex.vertices.values()) {
                    v.resetColor();
                }

                // RESET EDGES
                for (Edge edge : Edge.edges) {
                    edge.highlightEdge(false);
                //    edge.toggleLabel(false);
                    edge.repaint();
                }

                AlgorithmSetter setter = new AlgorithmSetter();
                GraphAlgorithm algorithmInstance = MainFrame.algorithm.getAlgorithmInstance();
                setter.setAlgorithm(algorithmInstance);

                Map<Vertex, List<Edge>> graph = createGraphDataStructure();
                String resultPath = setter.execute(graph, vertex);
                final String finalPath = resultPath;

                List<Vertex> visitedPath = null;

                if (algorithmInstance instanceof BFSAlgorithm bfs) {
                    visitedPath = bfs.getTraversalPath();
                } else if (algorithmInstance instanceof DijkstrasAlgorithm d) {
                    visitedPath = d.getTraversalPath();
                } else if (algorithmInstance instanceof DFSAlgorithm dfs) {
                    visitedPath = dfs.getTraversalPath();
                } else if (algorithmInstance instanceof PrimsAlgorithm prims) {
                    visitedPath = prims.getTraversalPath();
                }

                if (visitedPath != null && !visitedPath.isEmpty()) {
                    MainFrame.getAlgorithmDisplayLabel().setText("Running animation...");

                    final List<Vertex> finalVisitedPath = visitedPath;

                    Timer timer = new Timer(500, new ActionListener() {
                        final int[] index = {0};

                        public void actionPerformed(ActionEvent e) {
                            if (index[0] < finalVisitedPath.size()) {
                                Vertex current = finalVisitedPath.get(index[0]);
                                current.setBackground(Color.YELLOW);
                                current.repaint();

                                if (index[0] > 0) {
                                    Vertex previous = finalVisitedPath.get(index[0] - 1);
                                    Edge matchingEdge = findEdgeBetween(previous, current);
                                    if (matchingEdge != null) {
                                        matchingEdge.highlightEdge(true);
                                    //    matchingEdge.toggleLabel(true);
                                        matchingEdge.repaint();
                                    }
                                }

                                index[0]++;
                            } else {
                                ((Timer) e.getSource()).stop();
                                MainFrame.getAlgorithmDisplayLabel().setText(finalPath);
                            }
                        }
                    });

                    timer.start();
                } else {
                    MainFrame.getAlgorithmDisplayLabel().setText(finalPath);
                }
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    private static Vertex clickedOnVertex(int x, int y) {
        for (var entry : Vertex.vertices.entrySet()) {
            Vertex v = entry.getValue();
            if (x >= v.getXLocation() && x <= v.getXLocation() + Vertex.SIZE
                    && y >= v.getYLocation() && y <= v.getYLocation() + Vertex.SIZE) {
                return v;
            }
        }
        return null;
    }

    private static Edge clickedOnEdge(int x, int y) {
        for (Edge edge : Edge.edges) {
            int x1 = edge.getX();
            int y1 = edge.getY() + (edge.getTopEqualsLeft() ? 0 : edge.getHeight());
            int x2 = edge.getX() + edge.getWidth();
            int y2 = edge.getY() + (edge.getTopEqualsLeft() ? edge.getHeight() : 0);
            double dist = java.awt.geom.Line2D.ptLineDistSq(x1, y1, x2, y2, x, y);
            if (dist < 5) return edge;
        }
        return null;
    }

    private static boolean validPlacementForVertex(int x, int y) {
        if (clickedOnEdge(x, y) != null) return false;
        for (Vertex v : Vertex.vertices.values()) {
            if (x >= v.getXLocation() - Vertex.SIZE / 2 && x <= v.getXLocation() + Vertex.SIZE * 1.5 &&
                    y >= v.getYLocation() - Vertex.SIZE / 2 && y <= v.getYLocation() + Vertex.SIZE * 1.5) {
                return false;
            }
        }
        return true;
    }

    private static boolean validVertexID(String id) {
        return !Vertex.vertices.containsKey(id);
    }

    private void createVertex(int x, int y, String id) {
        Vertex vertex = new Vertex(x, y, id);
        this.add(vertex);
        vertex.repaint();
    }

    private void drawEdge(Vertex v1, Vertex v2) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, "Enter Weight:", "Input", JOptionPane.QUESTION_MESSAGE);
            if (input == null) break;
            if (input.matches("-?[0-9]+")) {
                int weight = Integer.parseInt(input);
                Edge e1 = new Edge(v1, v2, weight);
                Edge e2 = new Edge(v2, v1, weight);
                this.add(e1);
                this.add(e2);

                // Ensure edges stay below vertices
                this.setComponentZOrder(e1, 0);
                this.setComponentZOrder(e2, 0);

                this.repaint();
                this.revalidate();
                return;
            }
        }
    }


    private static void removeEdgeFromStaticList(Edge edge) {
        String id1 = edge.getVertex1().getId();
        String id2 = edge.getVertex2().getId();
        availableEdges.removeIf(pair -> pair.contains(id1) && pair.contains(id2));
    }

    private static Map<Vertex, List<Edge>> createGraphDataStructure() {
        Map<Vertex, List<Edge>> graph = new HashMap<>();
        for (Vertex v : Vertex.vertices.values()) {
            graph.put(v, new ArrayList<>());
        }
        for (Edge e : Edge.edges) {
            graph.get(e.getVertex1()).add(e);
        }
        return graph;
    }

    private Edge findEdgeBetween(Vertex v1, Vertex v2) {
        for (Edge e : Edge.edges) {
            if ((e.getVertex1().equals(v1) && e.getVertex2().equals(v2)) ||
                    (e.getVertex1().equals(v2) && e.getVertex2().equals(v1))) {
                return e;
            }
        }
        return null;
    }
}
