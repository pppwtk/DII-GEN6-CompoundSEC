package accesscontrol;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static accesscontrol.Constants.DATE_FORMAT;

public class CustomersMenu extends JFrame {
    private List<String[]> signupDataList = new ArrayList<>();
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel currentPanel;
    private JPanel bottomPanel;

    public CustomersMenu() {
        setTitle("Application");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));

        JButton signUpButton = new JButton("Sign up");
        JButton loginButton = new JButton("Log in");
        
        signUpButton.setFont(new Font("Arial", Font.PLAIN, 12));
        loginButton.setFont(new Font("Arial", Font.PLAIN, 12));

        signUpButton.addActionListener(e -> {
            new SignupMenu(this, signupDataList);
            setVisible(false);
        });

        loginButton.addActionListener(e -> showLoginPanel());

        buttonPanel.add(signUpButton);
        buttonPanel.add(loginButton);
        gbc.gridy++;
        mainPanel.add(buttonPanel, gbc);

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton backButton = new JButton("\u2B05 Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));
        backButton.addActionListener(e -> {
            new Main();
            dispose();
        });
        bottomPanel.add(backButton);

        loginPanel = createLoginPanel();
        currentPanel = mainPanel;
        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lockIcon = new JLabel("Log in");
        lockIcon.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(lockIcon, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel cardIDLabel = new JLabel("Card ID:");
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(cardIDLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField cardIDField = new JTextField(20);
        panel.add(cardIDField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel nameLabel = new JLabel("Cardholder:");
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField nameField = new JTextField(20);
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton accessButton = new JButton("Log in");
        accessButton.addActionListener(e -> {
            String cardID = cardIDField.getText().trim();
            String cardholderName = nameField.getText().trim();
            boolean loginSuccess = false;

            try {
                Scanner scanner = new Scanner(new File("database.txt"));
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] data = line.split(",");

                    String storedCardID = data[0].trim();
                    String storedName = data[1].trim();
                    String encryptedStartTime = data[2].trim();
                    String encryptedEndTime = data[3].trim();
                    String roomNumber = data[4].trim();
                    String accessStatus = data[5].trim();

                    if (storedCardID.equals(cardID) && storedName.equalsIgnoreCase(cardholderName)) {
                        String startTimeStr = EncryptionUtil.decrypt(encryptedStartTime);
                        String endTimeStr = EncryptionUtil.decrypt(encryptedEndTime);
                        Date startTime = DATE_FORMAT.parse(startTimeStr);
                        Date endTime = DATE_FORMAT.parse(endTimeStr);
                        Date currentTime = new Date();

                        if (accessStatus.equals("Approved") && currentTime.after(startTime) && currentTime.before(endTime)) {
                            String userDetails = String.format(
                                "Card ID: %s\nCardholder: %s\nCheck-in Date: %s\nCheck-out Date: %s\nRoom: %s",
                                storedCardID, storedName, startTimeStr, endTimeStr, roomNumber
                            );
                            JOptionPane.showMessageDialog(this, userDetails);
                            logLoginDetails(storedCardID, storedName, roomNumber, startTimeStr, endTimeStr, "Success");
                        } else {
                            String reason = accessStatus.equals("Approved") ? "Time expired" : "Not approved";
                            JOptionPane.showMessageDialog(this, "Access Denied: " + reason);
                            logLoginDetails(storedCardID, storedName, roomNumber, startTimeStr, endTimeStr, "Denied");
                        }
                        loginSuccess = true;
                        break;
                    }
                }
                if (!loginSuccess) {
                    JOptionPane.showMessageDialog(this, "Incorrect Card ID or Name");
                    logLoginDetails(cardID, cardholderName, "N/A", "N/A", "N/A", "Invalid");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        panel.add(accessButton, gbc);

        return panel;
    }

    private void showLoginPanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        currentPanel = loginPanel;
        add(loginPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showMainPanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        currentPanel = mainPanel;
        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void logLoginDetails(String cardID, String name, String room, String startTime, String endTime, String outcome) {
        try {
            String timestamp = DATE_FORMAT.format(new Date());
            String logEntry = String.format("%s,%s,%s,%s,%s,%s,%s\n",
                    cardID, name, timestamp, room, startTime, endTime, outcome);
            FileWriter writer = new FileWriter("log.txt", true);
            writer.write(logEntry);
            writer.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error writing to log file: " + ex.getMessage());
        }
    }

    public void addSignupData(String[] data) {
        signupDataList.add(data);
    }

    public List<String[]> getSignupData() {
        return signupDataList;
    }

    public void updateAdminData() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomersMenu::new);
    }
}