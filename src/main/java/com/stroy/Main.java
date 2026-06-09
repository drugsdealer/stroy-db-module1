package com.stroy;

import com.stroy.database.DatabaseManager;
import com.stroy.ui.LoginForm;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        // Initialize database
        DatabaseManager.initialize();

        // Create placeholder picture.png if it doesn't exist
        createPlaceholderImage();

        // Create images directory if it doesn't exist
        File imagesDir = new File("images");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }

        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default L&F
        }

        // Launch LoginForm on EDT
        SwingUtilities.invokeLater(LoginForm::new);
    }

    private static void createPlaceholderImage() {
        File placeholder = new File("picture.png");
        if (!placeholder.exists()) {
            BufferedImage img = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();

            // Fill gray background
            g2d.setColor(new Color(180, 180, 180));
            g2d.fillRect(0, 0, 300, 200);

            // Draw text
            g2d.setColor(Color.DARK_GRAY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            String text = "Нет изображения";
            int x = (300 - fm.stringWidth(text)) / 2;
            int y = (200 + fm.getAscent()) / 2 - fm.getDescent();
            g2d.drawString(text, x, y);
            g2d.dispose();

            try {
                ImageIO.write(img, "png", placeholder);
            } catch (IOException e) {
                System.err.println("Не удалось создать placeholder изображение: " + e.getMessage());
            }
        }
    }
}
