package com.colisiones;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // fallback to default
            }
            AppWindow window = new AppWindow();
            window.setVisible(true);
        });
    }
}
