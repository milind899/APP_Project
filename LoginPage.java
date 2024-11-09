import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private Connection connection;

    public LoginPage() {
        setTitle("Job Portal - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set custom gradient background
        getContentPane().setBackground(new Color(45, 45, 45)); // Set a default background color
        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(70, 130, 180), 0, getHeight(), new Color(30, 30, 30));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        });

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(45, 45, 45));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(100, 300, 100, 300));

        // Title Label
        JLabel titleLabel = new JLabel("Job Portal Login", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(new Color(220, 220, 220));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(new Color(55, 55, 55));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Username Label and Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(220, 220, 220));

        usernameField = new JTextField();
        usernameField.setBackground(new Color(60, 60, 60));
        usernameField.setForeground(new Color(220, 220, 220));
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);

        // Password Label and Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(220, 220, 220));

        passwordField = new JPasswordField();
        passwordField.setBackground(new Color(60, 60, 60));
        passwordField.setForeground(new Color(220, 220, 220));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> authenticateUser());

        // Add Hover effect on login button
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(100, 150, 200)); // Darker blue on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(70, 130, 180)); // Original color
            }
        });

        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adding Components
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacer
        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        mainPanel.add(loginButton);

        add(mainPanel);
        initializeDatabase();
        setVisible(true);
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/job_portal", "root", "mysqlmaihumilind");
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Database connection is not established.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT role FROM users WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            System.out.println("Executing query with username: " + username + " and password: " + password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                System.out.println("Login successful! User role: " + role);

                dispose(); // Close the login page
                
                if ("admin".equals(role)) {
                    new AdminPanel(connection);
                } else {
                    new UserPanel(connection);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while querying the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}
