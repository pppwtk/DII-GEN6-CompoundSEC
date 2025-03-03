package accesscontrol;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AuditPanel extends JPanel {
    private JTextArea auditTextArea;

    public AuditPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("Card Audit Log", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        add(header, BorderLayout.NORTH);

        auditTextArea = new JTextArea(20, 50);
        auditTextArea.setEditable(false);
        auditTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(auditTextArea);
        add(scrollPane, BorderLayout.CENTER);

        loadAuditData();
    }

    private void loadAuditData() {
        String filePath = "card_audit.txt";
        StringBuilder auditContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                auditContent.append(line).append("\n");
            }
            auditTextArea.setText(auditContent.toString());
        } catch (IOException e) {
            auditTextArea.setText("Error loading card_audit.txt: " + e.getMessage());
        }

        auditTextArea.setCaretPosition(0);
    }
}
