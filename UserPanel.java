import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserPanel extends JFrame {
    private JTextField searchField;
    private JButton searchButton, profileButton;
    private JPanel jobListPanel;
    private Connection connection;
    private static final Color BACKGROUND_COLOR = new Color(28, 27, 36);
    private static final Color PANEL_COLOR = new Color(35, 34, 43);
    private static final Color ACCENT_COLOR = new Color(86, 90, 233);
    private static final Color HOVER_COLOR = new Color(107, 111, 245);
    private static final Color SUCCESS_COLOR = new Color(46, 213, 115);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public UserPanel(Connection connection) {
        this.connection = connection;
        setTitle("Job Portal - User Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Panel for Search
        JPanel searchPanel = new JPanel(new BorderLayout(15, 15));
        searchPanel.setBackground(PANEL_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel searchLabel = new JLabel("Search Jobs");
        searchLabel.setFont(TITLE_FONT);
        searchLabel.setForeground(Color.WHITE);
        
        searchField = new JTextField(20);
        searchField.setFont(MAIN_FONT);
        searchField.setBackground(new Color(45, 44, 53));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        searchButton = createStyledButton("Search", ACCENT_COLOR, HOVER_COLOR);
        searchButton.addActionListener(e -> loadJobs());

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        profileButton = createStyledButton("My Profile", ACCENT_COLOR, HOVER_COLOR);
        profileButton.addActionListener(e -> openUserProfile());

        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBackground(PANEL_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(profileButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Job Listings Panel
        jobListPanel = new JPanel();
        jobListPanel.setLayout(new BoxLayout(jobListPanel, BoxLayout.Y_AXIS));
        jobListPanel.setBackground(BACKGROUND_COLOR);
        jobListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(jobListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        loadJobs();  // Loading jobs initially
        setVisible(true);
    }

    private JButton createStyledButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
        return button;
    }

    private void loadJobs() {
        jobListPanel.removeAll();
        String searchKeyword = searchField.getText().trim();

        try {
            String query = "SELECT * FROM jobs";
            if (!searchKeyword.isEmpty()) {
                query += " WHERE title LIKE ? OR category LIKE ? OR description LIKE ?";
            }
            query += " ORDER BY category";

            PreparedStatement stmt = connection.prepareStatement(query);
            if (!searchKeyword.isEmpty()) {
                String searchPattern = "%" + searchKeyword + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String jobTitle = rs.getString("title");
                String location = rs.getString("location");
                double salary = rs.getDouble("salary");
                int jobId = rs.getInt("id");

                JPanel jobPanel = new JPanel(new BorderLayout(15, 15));
                jobPanel.setBackground(PANEL_COLOR);
                jobPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(86, 90, 233, 40), 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
                jobPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

                JLabel titleLabel = new JLabel(jobTitle);
                titleLabel.setFont(TITLE_FONT);
                titleLabel.setForeground(Color.WHITE);

                JLabel detailsLabel = new JLabel(String.format("<html>📍 %s<br>💰 $%.2f/year</html>", 
                    location, salary));
                detailsLabel.setFont(MAIN_FONT);
                detailsLabel.setForeground(new Color(200, 200, 200));

                JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
                infoPanel.setBackground(PANEL_COLOR);
                infoPanel.add(titleLabel, BorderLayout.NORTH);
                infoPanel.add(detailsLabel, BorderLayout.CENTER);

                JButton applyButton = createStyledButton("Apply Now", SUCCESS_COLOR, 
                    new Color(56, 224, 133));
                applyButton.addActionListener(e -> openApplicationForm(jobId, jobTitle));

                jobPanel.add(infoPanel, BorderLayout.CENTER);
                jobPanel.add(applyButton, BorderLayout.EAST);

                jobListPanel.add(Box.createVerticalStrut(10));
                jobListPanel.add(jobPanel);
            }

            jobListPanel.revalidate();
            jobListPanel.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load jobs.", "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openApplicationForm(int jobId, String jobTitle) {
        JFrame applicationFrame = new JFrame("Apply for " + jobTitle);
        applicationFrame.setSize(400, 400);
        applicationFrame.setLocationRelativeTo(this);
        applicationFrame.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(MAIN_FONT);
        
        JTextField nameField = new JTextField(20);
        styleTextField(nameField);

        JLabel coverLetterLabel = new JLabel("Cover Letter");
        coverLetterLabel.setForeground(Color.WHITE);
        coverLetterLabel.setFont(MAIN_FONT);
        
        JTextArea coverLetterArea = new JTextArea(8, 20);
        coverLetterArea.setFont(MAIN_FONT);
        coverLetterArea.setBackground(new Color(45, 44, 53));
        coverLetterArea.setForeground(Color.WHITE);
        coverLetterArea.setCaretColor(Color.WHITE);
        coverLetterArea.setLineWrap(true);
        coverLetterArea.setWrapStyleWord(true);
        coverLetterArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JButton submitButton = createStyledButton("Submit Application", SUCCESS_COLOR, 
            new Color(56, 224, 133));
        submitButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(applicationFrame, 
                "Application submitted successfully!", "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            applicationFrame.dispose();
        });

        formPanel.add(nameLabel, gbc);
        formPanel.add(Box.createVerticalStrut(5), gbc);
        formPanel.add(nameField, gbc);
        formPanel.add(Box.createVerticalStrut(15), gbc);
        formPanel.add(coverLetterLabel, gbc);
        formPanel.add(Box.createVerticalStrut(5), gbc);
        formPanel.add(new JScrollPane(coverLetterArea), gbc);
        formPanel.add(Box.createVerticalStrut(15), gbc);
        formPanel.add(submitButton, gbc);

        applicationFrame.add(formPanel);
        applicationFrame.setVisible(true);
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(new Color(45, 44, 53));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setFont(MAIN_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private void openUserProfile() {
        JOptionPane.showMessageDialog(this, "User Profile clicked!", "Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            // Establishing database connection
            String url = "jdbc:mysql://localhost:3306/jobportal";
            String user = "root";
            String password = "mysqlmaihumilind";
            Connection connection = DriverManager.getConnection(url, user, password);

            // Create and show the User Panel directly (no welcome screen)
            new UserPanel(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
