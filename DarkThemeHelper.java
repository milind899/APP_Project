// File: DarkThemeHelper.java
import javax.swing.*;
import java.awt.*;

public class DarkThemeHelper {
    public static void applyDarkTheme(Component component) {
        Color darkBackground = new Color(34, 34, 34); // Dark background color
        Color textColor = new Color(220, 220, 220);   // Light text color
        Color buttonColor = new Color(60, 63, 65);    // Button color
        Color borderColor = new Color(80, 80, 80);    // Border color
        
        component.setBackground(darkBackground);
        component.setForeground(textColor);
        component.setFont(new Font("SansSerif", Font.PLAIN, 14));

        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            for (Component child : panel.getComponents()) {
                applyDarkTheme(child);
            }
        } else if (component instanceof JButton) {
            JButton button = (JButton) component;
            button.setBackground(buttonColor);
            button.setForeground(textColor);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));
        } else if (component instanceof JTextField || component instanceof JPasswordField) {
            component.setBackground(new Color(45, 45, 45));
            component.setForeground(textColor);
            component.setFont(new Font("SansSerif", Font.PLAIN, 14));
            ((JComponent) component).setBorder(BorderFactory.createLineBorder(borderColor));
        }
    }
}
