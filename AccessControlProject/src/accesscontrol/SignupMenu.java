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

public class SignupMenu extends JFrame {
    private JTextField txtCardID, txtCardholder;
    private JSpinner startTimeSpinner, expiryTimeSpinner;
    private List<String> selectedRooms = new ArrayList<>();
    private CustomersMenu adminFrame;
    private List<String[]> signupDataList;

    public SignupMenu(CustomersMenu adminFrame, List<String[]> signupDataList) {
        this.adminFrame = adminFrame;
        this.signupDataList = signupDataList;

        setTitle("Application");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel header = new JLabel("Create Your Account", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        mainPanel.add(header, BorderLayout.NORTH);

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
        btnBack.addActionListener(e -> {
            this.setVisible(false);
            adminFrame.setVisible(true);
        });
        formPanel.add(btnBack, gbc);

        gbc.gridx = 1;
        JButton btnSignup = new JButton("Sign Up");
        btnSignup.addActionListener(e -> saveSignupData());
        formPanel.add(btnSignup, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private void saveSignupData() {
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

            if (hasApprovedTimeAndRoomConflict(newStartTime, newEndTime, selectedRooms)) {
                JOptionPane.showMessageDialog(this, 
                    "This rooms are already taked for an overlapping time range\n", 
                    "Booking Alert", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String encryptedStartTime = EncryptionUtil.encrypt(startTimeStr);
            String encryptedExpiryTime = EncryptionUtil.encrypt(expiryTimeStr);
            String[] signupData = {cardID, cardholder, encryptedStartTime, encryptedExpiryTime, permissions, "Requesting"};
            signupDataList.add(signupData);

            saveToFile(signupData);
            LogUtil.logCardEvent(cardID, "Created");

            adminFrame.updateAdminData();
            JOptionPane.showMessageDialog(this, "Sign Up successfully!");
            this.setVisible(false);
            adminFrame.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean hasApprovedTimeAndRoomConflict(Date newStartTime, Date newEndTime, List<String> newRooms) {
        try (BufferedReader reader = new BufferedReader(new FileReader("database.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    Date existingStartTime = DATE_FORMAT.parse(EncryptionUtil.decrypt(parts[2].trim()));
                    Date existingEndTime = DATE_FORMAT.parse(EncryptionUtil.decrypt(parts[3].trim()));
                    String existingRoomsStr = parts[4].trim();
                    String status = parts[5].trim();

                    // Check for time overlap AND same rooms
                    if (status.equals("Approved") &&
                        newStartTime.getTime() <= existingEndTime.getTime() && 
                        newEndTime.getTime() >= existingStartTime.getTime()) {
                        List<String> existingRooms = new ArrayList<>(List.of(existingRoomsStr.split(", ")));
                        // Check if requested rooms match existing approved
                        if (newRooms.stream().anyMatch(existingRooms::contains)) {
                            return true; // if found: overlapping time
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

    private void saveToFile(String[] signupData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("database.txt", true))) {
            writer.write(String.join(",", signupData));
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data to file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRoomSelection(int floor) {
        new RoomSelectionDialog(this, floor);
    }

    public void addSelectedRooms(List<String> rooms) {
        selectedRooms.addAll(rooms);
    }

    public class RoomSelectionDialog extends JDialog {
        private SignupMenu signup;
        private List<JCheckBox> roomCheckBoxes = new ArrayList<>();

        public RoomSelectionDialog(SignupMenu signup, int floor) {
            super(signup, "Select Rooms - Floor " + floor, true);
            this.signup = signup;

            setLayout(new BorderLayout());
            JPanel roomPanel = new JPanel(new GridLayout(0, 3, 3, 3));

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
                signup.addSelectedRooms(selectedRooms);
                dispose();
            });

            add(roomPanel, BorderLayout.CENTER);
            add(btnConfirm, BorderLayout.SOUTH);

            setSize(300, 500);
            setLocationRelativeTo(signup);
            setVisible(true);
        }
    }
}