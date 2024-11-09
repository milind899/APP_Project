import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminPanel extends JFrame {
    private JTextField titleField, locationField, salaryField;
    private JTextArea descriptionArea;
    private JButton addJobButton, viewApplicationsButton, loadJobsButton;
    private JComboBox<String> jobSelector;
    private JPanel applicationsPanel;
    private Connection connection;

    public AdminPanel(Connection connection) {
        this.connection = connection;
        setTitle("Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Font mainFont = new Font("SansSerif", Font.PLAIN, 14);
        Color backgroundColor = new Color(45, 45, 45);
        Color panelColor = new Color(55, 55, 55);
        Color textColor = new Color(220, 220, 220);
        Color buttonColor = new Color(70, 130, 180);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel postJobPanel = createSectionPanel("Post a New Job", panelColor, textColor, mainFont);
        postJobPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        titleField = createTextField();
        locationField = createTextField();
        salaryField = createTextField();
        descriptionArea = createTextArea();

        addComponent(postJobPanel, createLabel("Title:", mainFont, textColor), gbc, 0, 0);
        addComponent(postJobPanel, titleField, gbc, 1, 0);
        addComponent(postJobPanel, createLabel("Description:", mainFont, textColor), gbc, 0, 1);
        addComponent(postJobPanel, new JScrollPane(descriptionArea), gbc, 1, 1);
        addComponent(postJobPanel, createLabel("Location:", mainFont, textColor), gbc, 0, 2);
        addComponent(postJobPanel, locationField, gbc, 1, 2);
        addComponent(postJobPanel, createLabel("Salary:", mainFont, textColor), gbc, 0, 3);
        addComponent(postJobPanel, salaryField, gbc, 1, 3);

        addJobButton = createButton("Add Job", buttonColor);
        addJobButton.addActionListener(e -> addJob());
        gbc.gridwidth = 2;
        addComponent(postJobPanel, addJobButton, gbc, 0, 4);

        JPanel viewApplicationsPanel = createSectionPanel("View Applications", panelColor, textColor, mainFont);
        viewApplicationsPanel.setLayout(new BorderLayout(10, 10));

        JPanel jobSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        jobSelectPanel.setBackground(panelColor);
        jobSelector = new JComboBox<>();
        loadJobsButton = createButton("Load Jobs", buttonColor);
        loadJobsButton.addActionListener(e -> loadJobs());
        viewApplicationsButton = createButton("View Applications", buttonColor);
        viewApplicationsButton.addActionListener(e -> viewApplications());

        jobSelectPanel.add(createLabel("Select Job:", mainFont, textColor));
        jobSelectPanel.add(jobSelector);
        jobSelectPanel.add(loadJobsButton);
        jobSelectPanel.add(viewApplicationsButton);

        viewApplicationsPanel.add(jobSelectPanel, BorderLayout.NORTH);
        applicationsPanel = new JPanel();
        applicationsPanel.setLayout(new BoxLayout(applicationsPanel, BoxLayout.Y_AXIS));
        applicationsPanel.setBackground(panelColor);
        JScrollPane scrollPane = new JScrollPane(applicationsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        viewApplicationsPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(postJobPanel, BorderLayout.WEST);
        mainPanel.add(viewApplicationsPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private void addComponent(JPanel panel, Component comp, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(comp, gbc);
    }

    private JPanel createSectionPanel(String title, Color panelColor, Color textColor, Font font) {
        JPanel panel = new JPanel();
        panel.setBackground(panelColor);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)), title, TitledBorder.LEFT, TitledBorder.TOP, font, textColor));
        return panel;
    }

    private JLabel createLabel(String text, Font font, Color textColor) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(textColor);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(15);
        textField.setBackground(new Color(60, 60, 60));
        textField.setForeground(new Color(220, 220, 220));
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea(4, 15);
        textArea.setBackground(new Color(60, 60, 60));
        textArea.setForeground(new Color(220, 220, 220));
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textArea;
    }

    private JButton createButton(String text, Color buttonColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(buttonColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

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

            titleField.setText("");
            descriptionArea.setText("");
            locationField.setText("");
            salaryField.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add job.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadJobs() {
        jobSelector.removeAllItems();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, title FROM jobs")) {

            while (rs.next()) {
                int jobId = rs.getInt("id");
                String title = rs.getString("title");
                jobSelector.addItem(jobId + " - " + title);
            }

            JOptionPane.showMessageDialog(this, "Jobs loaded successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load jobs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewApplications() {
        applicationsPanel.removeAll();

        String selectedJob = (String) jobSelector.getSelectedItem();
        if (selectedJob == null) {
            JOptionPane.showMessageDialog(this, "No job selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int jobId = Integer.parseInt(selectedJob.split(" - ")[0]);

        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM applications WHERE job_id = ?")) {
            stmt.setInt(1, jobId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                applicationsPanel.add(new JLabel("No applications for this job."));
            } else {
                while (rs.next()) {
                    int applicationId = rs.getInt("id");
                    String userName = rs.getString("user_name");
                    String userEmail = rs.getString("user_email");
                    String resume = rs.getString("resume");
                    String coverLetter = rs.getString("cover_letter");
                    String status = rs.getString("application_status");

                    JPanel applicationPanel = new JPanel(new BorderLayout(10, 10));
                    applicationPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.GRAY),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                    applicationPanel.setBackground(new Color(60, 60, 60));

                    JLabel applicationInfo = new JLabel("<html><b>Applicant:</b> " + userName +
                            "<br><b>Email:</b> " + userEmail +
                            "<br><b>Resume:</b> " + resume +
                            "<br><b>Cover Letter:</b> " + coverLetter + "</html>");

                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    JButton approveButton = createButton("Approve", new Color(46, 204, 113));
                    JButton rejectButton = createButton("Reject", new Color(231, 76, 60));

                    approveButton.addActionListener(e -> updateApplicationStatus(applicationId, "Approved"));
                    rejectButton.addActionListener(e -> updateApplicationStatus(applicationId, "Rejected"));

                    buttonPanel.add(approveButton);
                    buttonPanel.add(rejectButton);

                    applicationPanel.add(applicationInfo, BorderLayout.CENTER);
                    applicationPanel.add(buttonPanel, BorderLayout.SOUTH);

                    if (status.equals("Approved")) {
                        applicationPanel.setBackground(new Color(46, 204, 113, 80));
                    } else if (status.equals("Rejected")) {
                        applicationPanel.setBackground(new Color(231, 76, 60, 80));
                    }

                    applicationsPanel.add(applicationPanel);
                }
            }

            applicationsPanel.revalidate();
            applicationsPanel.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load applications.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateApplicationStatus(int applicationId, String status) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE applications SET application_status = ? WHERE id = ?")) {
            stmt.setString(1, status);
            stmt.setInt(2, applicationId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Application " + status + " successfully!");
            viewApplications();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update application status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
