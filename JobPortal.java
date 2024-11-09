// File: JobPortal.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class JobPortal extends JFrame {
    private JTextField titleField, locationField, salaryField, searchField;
    private JTextArea descriptionArea, resultArea;
    private JButton addJobButton, searchButton;
    private Connection connection;

    public JobPortal() {
        // Set up the JFrame
        setTitle("Job Portal");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize database connection
        initializeDatabase();

        // Create UI Components
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        inputPanel.add(new JLabel("Job Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 20);
        inputPanel.add(new JScrollPane(descriptionArea));

        inputPanel.add(new JLabel("Location:"));
        locationField = new JTextField();
        inputPanel.add(locationField);

        inputPanel.add(new JLabel("Salary:"));
        salaryField = new JTextField();
        inputPanel.add(salaryField);

        addJobButton = new JButton("Add Job");
        addJobButton.addActionListener(e -> addJob());
        inputPanel.add(addJobButton);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        searchButton = new JButton("Search Jobs");
        searchButton.addActionListener(e -> searchJobs());
        searchPanel.add(new JLabel("Search Jobs:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Result Display
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);

        // Add components to JFrame
        add(inputPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(resultScrollPane, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Database connection setup
    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/job_portal", "root", "mysqlmaihumilind");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Add job to database
    private void addJob() {
        String title = titleField.getText();
        String description = descriptionArea.getText();
        String location = locationField.getText();
        String salary = salaryField.getText();

        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO jobs (title, description, location, salary) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, location);
            stmt.setDouble(4, Double.parseDouble(salary));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Job added successfully!");

            // Clear fields
            titleField.setText("");
            descriptionArea.setText("");
            locationField.setText("");
            salaryField.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add job.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Search for jobs by title
    private void searchJobs() {
        String keyword = searchField.getText();
        resultArea.setText("");

        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM jobs WHERE title LIKE ? OR description LIKE ? OR location LIKE ?")) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            stmt.setString(3, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String result = "ID: " + rs.getInt("id") + "\n" +
                                "Title: " + rs.getString("title") + "\n" +
                                "Description: " + rs.getString("description") + "\n" +
                                "Location: " + rs.getString("location") + "\n" +
                                "Salary: $" + rs.getDouble("salary") + "\n\n";
                resultArea.append(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to search jobs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(JobPortal::new);
    }
}
