package ui;

import java.awt.EventQueue;

import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Swing can still run with the default look and feel.
            }

            OthelloWindow window = new OthelloWindow();
            window.setVisible(true);
        });
    }
}
