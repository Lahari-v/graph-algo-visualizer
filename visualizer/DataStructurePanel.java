package visualizer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DataStructurePanel extends JPanel {
    private DefaultListModel<String> listModel;
    private JList<String> stateList;
    private JLabel titleLabel;

    public DataStructurePanel(String title) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 0)); // Width = 200px

        // Title label
        titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.BLACK);        // Ensure text is visible
        titleLabel.setBackground(Color.LIGHT_GRAY);   // Better contrast
        titleLabel.setOpaque(true);                   // Important to show background color
        add(titleLabel, BorderLayout.NORTH);

        // List and ScrollPane
        listModel = new DefaultListModel<>();
        stateList = new JList<>(listModel);
        stateList.setForeground(Color.BLACK);         // Text color
        stateList.setBackground(Color.WHITE);         // Background color
        JScrollPane scrollPane = new JScrollPane(stateList);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Optional border
        add(scrollPane, BorderLayout.CENTER);

        // Panel background for visibility/debug
        setBackground(Color.WHITE); // Optional: change or remove after testing
    }

    public void updateContents(List<String> items) {
        listModel.clear();
        for (String item : items) {
            listModel.addElement(item);
        }

        // Ensure GUI updates properly
        this.revalidate();
        this.repaint();

        System.out.println("JList contents: " + listModel);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}
