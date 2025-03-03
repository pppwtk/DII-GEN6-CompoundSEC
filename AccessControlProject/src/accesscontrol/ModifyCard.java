package accesscontrol;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static accesscontrol.Constants.DATE_FORMAT;

public class ModifyCard extends JPanel {
    private JTextField txtCardID, txtCardholder;
    private JSpinner startTimeSpinner, expiryTimeSpinner;
    private List<String> selectedRooms = new ArrayList<>();
    private AdminMenu mainFrame;

    public ModifyCard(AdminMenu mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel header = new JLabel("Modify Card", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Card ID:"), gbc);
        gbc.gridx = 1;
        txtCardID = new JTextField(20);
        txtCardID.addActionListener(e -> loadCardData());
        formPanel.add(txtCardID, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        txtCardholder = new JTextField(20);
        formPanel.add(txtCardholder, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Room:"), gbc);
        gbc.gridx = 1;
        JPanel permissionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton floor1Btn = new JButton("Floor 1");
        JButton floor2Btn = new JButton("Floor 2");
        JButton floor3Btn = new JButton("Floor 3");
        floor1Btn.addActionListener(e -> openRoomSelection(1));
        floor2Btn.addActionListener(e -> openRoomSelection(2));
        floor3Btn.addActionListener(e -> openRoomSelection(3));
        permissionsPanel.add(floor1Btn);
        permissionsPanel.add(floor2Btn);
        permissionsPanel.add(floor3Btn);
        formPanel.add(permissionsPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "dd/MM/yyyy HH:mm"));
        formPanel.add(startTimeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 1;
        expiryTimeSpinner = new JSpinner(new SpinnerDateModel());
        expiryTimeSpinner.setEditor(new JSpinner.DateEditor(expiryTimeSpinner, "dd/MM/yyyy HH:mm"));
        formPanel.add(expiryTimeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        JButton btnBack = new JButton("⬅ Back");
        btnBack.addActionListener(e -> mainFrame.switchPanel("Dashboard"));
        formPanel.add(btnBack, gbc);

        gbc.gridx = 1;
        JButton btnSave = new JButton("Save Changes");
        btnSave.addActionListener(e -> saveModifiedCard());
        formPanel.add(btnSave, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void loadCardData() {
        String cardID = txtCardID.getText().trim();
        try (BufferedReader reader = new BufferedReader(new FileReader("database.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(cardID)) {
                    txtCardholder.setText(parts[1]);
                    startTimeSpinner.setValue(DATE_FORMAT.parse(EncryptionUtil.decrypt(parts[2])));
                    expiryTimeSpinner.setValue(DATE_FORMAT.parse(EncryptionUtil.decrypt(parts[3])));
                    selectedRooms.clear();
                    selectedRooms.addAll(Arrays.asList(parts[4].split(", ")));
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Card ID not found!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading card: " + e.getMessage());
        }
    }

    private void saveModifiedCard() {
        String cardID = txtCardID.getText().trim();
        String cardholder = txtCardholder.getText().trim();
        String startTimeStr = DATE_FORMAT.format((Date) startTimeSpinner.getValue());
        String expiryTimeStr = DATE_FORMAT.format((Date) expiryTimeSpinner.getValue());
        String permissions = String.join(", ", selectedRooms);

        try {
            String encryptedStartTime = EncryptionUtil.encrypt(startTimeStr);
            String encryptedExpiryTime = EncryptionUtil.encrypt(expiryTimeStr);
            List<String> lines = new ArrayList<>();
            boolean found = false;

            try (BufferedReader reader = new BufferedReader(new FileReader("database.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(cardID)) {
                        lines.add(String.join(",", cardID, cardholder, encryptedStartTime, encryptedExpiryTime, permissions, parts[5]));
                        found = true;
                    } else {
                        lines.add(line);
                    }
                }
            }

            if (found) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("database.txt"))) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
                LogUtil.logCardEvent(cardID, "Modified");
                JOptionPane.showMessageDialog(this, "Card updated successfully!");
                mainFrame.switchPanel("Dashboard");
            } else {
                JOptionPane.showMessageDialog(this, "Card ID not found!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving card: " + e.getMessage());
        }
    }

    private void openRoomSelection(int floor) {
        new RoomSelectionDialog(this, floor);
    }

    public void addSelectedRooms(List<String> rooms) {
        selectedRooms.addAll(rooms);
    }

    public static class RoomSelectionDialog extends JDialog {
        private ModifyCard modifyCardPanel;
        private List<JCheckBox> roomCheckBoxes = new ArrayList<>();

        public RoomSelectionDialog(ModifyCard modifyCardPanel, int floor) {
            super((JFrame) SwingUtilities.getWindowAncestor(modifyCardPanel), "Select Rooms - Floor " + floor, true);
            this.modifyCardPanel = modifyCardPanel;

            setLayout(new BorderLayout());
            JPanel roomPanel = new JPanel(new GridLayout(0, 3, 5, 5));

            for (int i = 1; i <= 9; i++) {
                String roomNumber = "R" + floor + "0" + i;
                JCheckBox checkBox = new JCheckBox(roomNumber);
                roomCheckBoxes.add(checkBox);
                roomPanel.add(checkBox);
            }

            JButton btnConfirm = new JButton("Confirm");
            btnConfirm.addActionListener(e -> {
                List<String> selectedRooms = new ArrayList<>();
                for (JCheckBox checkBox : roomCheckBoxes) {
                    if (checkBox.isSelected()) {
                        selectedRooms.add(checkBox.getText());
                    }
                }
                modifyCardPanel.addSelectedRooms(selectedRooms);
                dispose();
            });

            add(roomPanel, BorderLayout.CENTER);
            add(btnConfirm, BorderLayout.SOUTH);

            setSize(300, 300);
            setLocationRelativeTo(modifyCardPanel);
            setVisible(true);
        }
    }
}