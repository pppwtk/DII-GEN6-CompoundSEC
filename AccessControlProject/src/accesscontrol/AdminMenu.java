package accesscontrol;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminMenu extends JFrame {
    private JPanel sidebar;
    private JPanel mainPanel;
    private boolean isCollapsed = false;
    private CustomersMenu customersFrame;

    public AdminMenu() {
        setTitle("Admin Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        customersFrame = new CustomersMenu();

        sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(7, 1, 10, 10));
        sidebar.setPreferredSize(new Dimension(150, getHeight()));
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        String[] menuItems = {"Dashboard", "Log", "Add Card", "Modify Card", "View Audit"};
        for (String item : menuItems) {
            JButton btn = createStyledButton(item);
            btn.addActionListener(e -> switchPanel(item));
            sidebar.add(btn);
        }

        JButton logoutButton = createStyledButton("⬅   Back");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutButton.addActionListener(e -> {
            new Main();
            dispose();
        });
        sidebar.add(logoutButton);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        switchPanel("Dashboard");

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setBackground(new Color(220, 220, 220));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(200, 200, 200)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(220, 220, 220)); }
        });
        return btn;
    }

    public void switchPanel(String panelName) {
        System.out.println("Switching to: " + panelName);
        mainPanel.removeAll();

        switch (panelName) {
            case "Dashboard":
                mainPanel.add(new Dashboard(customersFrame));
                break;
            case "Log":
                mainPanel.add(new Log());
                break;
            case "Add Card":
                mainPanel.add(new AddCard(this));
                break;
            case "Modify Card":
                mainPanel.add(new ModifyCard(this));
                break;
            case "View Audit":
                mainPanel.add(new AuditPanel());
                break;
            default:
                mainPanel.add(new JLabel("Unknown Page: " + panelName, SwingConstants.CENTER));
                break;
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminMenu::new);
    }
}