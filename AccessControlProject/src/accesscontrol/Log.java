package accesscontrol;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class Log extends JPanel {
    private JTable logTable;
    private DefaultTableModel tableModel;

    public Log() {
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Log", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Time Stamp", "Room", "Start Date", "End Date", "Outcome"};
        List<String[]> dataList = loadLogDataFromFile();
        String[][] data = dataList.toArray(new String[0][]);

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        logTable = new JTable(tableModel);
        logTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(logTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private List<String[]> loadLogDataFromFile() {
        List<String[]> dataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("log.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    dataList.add(parts);
                } else {
                    System.err.println("Invalid log line format: " + line);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading log.txt: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return dataList;
    }
}