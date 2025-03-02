package accesscontrol;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static accesscontrol.Constants.DATE_FORMAT;

public class AddCard extends JPanel {
    private JTextField txtCardID, txtCardholder;
    private JSpinner startTimeSpinner, expiryTimeSpinner;
    private List<String> selectedRooms = new ArrayList<>();
    private AdminMenu mainFrame;

    public AddCard(AdminMenu mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel header = new JLabel("Add New Card", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(new JLabel("ID Card :"), gbc);
        gbc.gridx = 1;
        txtCardID = new JTextField(20);
        formPanel.add(txtCardID, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Name :"), gbc);
        gbc.gridx = 1;
        txtCardholder = new JTextField(20);
        formPanel.add(txtCardholder, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Room :"), gbc);

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

        gbc.gridx = 1;
        formPanel.add(permissionsPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Start Time :"), gbc);
        gbc.gridx = 1;
        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "dd/MM/yyyy HH:mm"));
        formPanel.add(startTimeSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("End Time :"), gbc);
        gbc.gridx = 1;
        expiryTimeSpinner = new JSpinner(new SpinnerDateModel());
        expiryTimeSpinner.setEditor(new JSpinner.DateEditor(expiryTimeSpinner, "dd/MM/yyyy HH:mm"));
        formPanel.add(expiryTimeSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JButton btnBack = new JButton("⬅ Back");
        btnBack.addActionListener(e -> mainFrame.switchPanel("Dashboard"));
        formPanel.add(btnBack, gbc);

        gbc.gridx = 1;
        JButton btnConfirm = new JButton("Confirm");
        btnConfirm.addActionListener(e -> saveCardData());
        formPanel.add(btnConfirm, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void saveCardData() {
        String cardID = txtCardID.getText().trim();
        String cardholder = txtCardholder.getText().trim();
        String startTimeStr = DATE_FORMAT.format((Date) startTimeSpinner.getValue());
        String expiryTimeStr = DATE_FORMAT.format((Date) expiryTimeSpinner.getValue());
        String permissions = String.join(", ", selectedRooms);

        if (cardID.isEmpty() || cardholder.isEmpty() || permissions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Date newStartTime = DATE_FORMAT.parse(startTimeStr);
            Date newEndTime = DATE_FORMAT.parse(expiryTimeStr);

            if (hasApprovedTimeAndRoomConflict(cardholder, newStartTime, newEndTime, selectedRooms)) {
                JOptionPane.showMessageDialog(this, 
                    "User '" + cardholder + "' already has an approved overlapping time range with the same rooms!", 
                    "Conflict Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String encryptedStartTime = EncryptionUtil.encrypt(startTimeStr);
            String encryptedExpiryTime = EncryptionUtil.encrypt(expiryTimeStr);
            String[] cardData = {cardID, cardholder, encryptedStartTime, encryptedExpiryTime, permissions, "Requesting"};
            saveToFile(cardData);
            LogUtil.logCardEvent(cardID, "Created");

            JOptionPane.showMessageDialog(this, "Card added successfully!");
            txtCardID.setText("");
            txtCardholder.setText("");
            selectedRooms.clear();
            mainFrame.switchPanel("Dashboard");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean hasApprovedTimeAndRoomConflict(String name, Date newStartTime, Date newEndTime, List<String> newRooms) {
        try (BufferedReader reader = new BufferedReader(new FileReader("database.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[1].trim().equals(name)) {
                    Date existingStartTime = DATE_FORMAT.parse(EncryptionUtil.decrypt(parts[2].trim()));
                    Date existingEndTime = DATE_FORMAT.parse(EncryptionUtil.decrypt(parts[3].trim()));
                    String existingRoomsStr = parts[4].trim();
                    String status = parts[5].trim();

                    if (newStartTime.getTime() <= existingEndTime.getTime() && 
                        newEndTime.getTime() >= existingStartTime.getTime() && 
                        status.equals("Approved")) {
                        List<String> existingRooms = new ArrayList<>(List.of(existingRoomsStr.split(", ")));
                        if (newRooms.equals(existingRooms)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error checking database: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void saveToFile(String[] cardData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("database.txt", true))) {
            writer.write(String.join(",", cardData));
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data to file: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRoomSelection(int floor) {
        new RoomSelectionDialog(this, floor);
    }

    public void addSelectedRooms(List<String> rooms) {
        selectedRooms.addAll(rooms);
    }

    public static class RoomSelectionDialog extends JDialog {
        private AddCard addCardPanel;
        private List<JCheckBox> roomCheckBoxes = new ArrayList<>();

        public RoomSelectionDialog(AddCard addCardPanel, int floor) {
            super((JFrame) SwingUtilities.getWindowAncestor(addCardPanel), "Select Rooms - Floor " + floor, true);
            this.addCardPanel = addCardPanel;

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
                addCardPanel.addSelectedRooms(selectedRooms);
                dispose();
            });

            add(roomPanel, BorderLayout.CENTER);
            add(btnConfirm, BorderLayout.SOUTH);

            setSize(300, 300);
            setLocationRelativeTo(addCardPanel);
            setVisible(true);
        }
    }
}