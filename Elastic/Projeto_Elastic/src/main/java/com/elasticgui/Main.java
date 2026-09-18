package com.elasticgui;

import com.elasticgui.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // segue com o look and feel padrão do Swing
            }
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
