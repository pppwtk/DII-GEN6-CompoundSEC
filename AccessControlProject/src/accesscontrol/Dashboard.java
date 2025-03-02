package accesscontrol;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class Dashboard extends JPanel {
    private CustomersMenu customersFrame;
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JButton approveButton;
    private JButton denyButton;
    private JButton deleteButton;

    public Dashboard(CustomersMenu customersFrame) {
        this.customersFrame = customersFrame;
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Dashboard", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Start Time", "End Time", "Room", "Access"};
        List<String[]> dataList = loadDataFromFile();
        String[][] data = dataList.toArray(new String[0][]);

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dataTable = new JTable(tableModel);
        dataTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(dataTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        approveButton = new JButton("Approve");
        approveButton.setEnabled(false);
        approveButton.addActionListener(e -> updateStatus("Approved"));
        buttonPanel.add(approveButton);

        denyButton = new JButton("Deny");
        denyButton.setEnabled(false);
        denyButton.addActionListener(e -> updateStatus("Denied"));
        buttonPanel.add(denyButton);

        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteCard());
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        dataTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = dataTable.getSelectedRow();
            boolean rowSelected = selectedRow >= 0;
            approveButton.setEnabled(rowSelected);
            denyButton.setEnabled(rowSelected);
            deleteButton.setEnabled(rowSelected);
        });
    }

    private void updateStatus(String newStatus) {
        int row = dataTable.getSelectedRow();
        if (row >= 0) {
            String id = (String) tableModel.getValueAt(row, 0);
            tableModel.setValueAt(newStatus, row, 5);
            updateFile(id, newStatus);
            LogUtil.logCardEvent(id, "Status changed to " + newStatus);
        }
    }

    private void deleteCard() {
        int row = dataTable.getSelectedRow();
        if (row >= 0) {
            String id = (String) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete card " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader("database.txt"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.split(",")[0].equals(id)) {
                            lines.add(line);
                        }
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error reading database: " + e.getMessage());
                    return;
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("database.txt"))) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error writing database: " + e.getMessage());
                    return;
                }
                tableModel.removeRow(row);
                LogUtil.logCardEvent(id, "Deleted");
                JOptionPane.showMessageDialog(this, "Card deleted successfully!");
            }
        }
    }

    private void updateFile(String id, String newStatus) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader("database.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[0].equals(id)) {
                    String[] updatedParts = new String[6];
                    System.arraycopy(parts, 0, updatedParts, 0, parts.length);
                    updatedParts[5] = newStatus;
                    lines.add(String.join(",", updatedParts));
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading database.txt: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "ID not found in file!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("database.txt"))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Status updated to " + newStatus + " successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to database.txt: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String[]> loadDataFromFile() {
        List<String[]> dataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("database.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String[] row = new String[6];
                    row[0] = parts[0]; // ID
                    row[1] = parts[1]; // Name
                    row[2] = EncryptionUtil.decrypt(parts[2]); // Start Time
                    row[3] = EncryptionUtil.decrypt(parts[3]); // End Time
                    row[4] = parts[4]; // Rooms
                    row[5] = parts[5]; // Status
                    dataList.add(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading database.txt: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
        return dataList;
    }
}