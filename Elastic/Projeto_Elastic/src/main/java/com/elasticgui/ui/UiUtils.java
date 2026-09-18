package com.elasticgui.ui;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.Component;
import java.awt.Desktop;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public final class UiUtils {

    private UiUtils() {
    }

    /**
     * Executa {@code task} em uma thread de segundo plano (para não travar a
     * interface enquanto aguarda a resposta HTTP) e entrega o resultado (ou
     * o erro) de volta na Event Dispatch Thread.
     */
    public static <T> void runAsync(Callable<T> task, Consumer<T> onSuccess, Consumer<Exception> onError) {
        SwingWorker<T, Void> worker = new SwingWorker<>() {
            @Override
            protected T doInBackground() throws Exception {
                return task.call();
            }

            @Override
            protected void done() {
                try {
                    T result = get();
                    onSuccess.accept(result);
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    onError.accept(cause instanceof Exception ? (Exception) cause : e);
                }
            }
        };
        worker.execute();
    }

    public static void showError(Component parent, String title, Throwable e) {
        String message = e.getMessage() != null ? e.getMessage() : e.toString();
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void openInBrowser(Component parent, String url) {
        if (url == null || url.isBlank()) {
            showInfo(parent, "Sem URL", "Este resultado não possui uma URL associada.");
            return;
        }
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
            } else {
                showInfo(parent, "Abrir URL", url);
            }
        } catch (Exception e) {
            showError(parent, "Não foi possível abrir a URL", e);
        }
    }

    public static void invokeLater(Runnable r) {
        SwingUtilities.invokeLater(r);
    }
}
