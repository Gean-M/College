package com.elasticgui.ui;

import com.elasticgui.client.ElasticsearchClient;
import com.elasticgui.config.AppConfig;
import com.elasticgui.json.Json;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;

/**
 * Diálogo para configurar host, porta, credenciais e índice do Elasticsearch
 * (os valores padrão já vêm preenchidos com o que é usado em aula:
 * https://localhost:9200, elastic/user123, índice "wikipedia").
 */
public class ConnectionDialog extends JDialog {

    private final JComboBox<String> schemeField = new JComboBox<>(new String[]{"https", "http"});
    private final JTextField hostField = new JTextField(18);
    private final JTextField portField = new JTextField(6);
    private final JTextField userField = new JTextField(14);
    private final JPasswordField passwordField = new JPasswordField(14);
    private final JTextField indexField = new JTextField(16);
    private final JCheckBox trustAllCheckbox = new JCheckBox("Confiar em certificados autoassinados (ambiente de dev)");

    private boolean saved = false;

    public ConnectionDialog(Frame owner, AppConfig config) {
        super(owner, "Configuração de conexão", true);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;

        int row = 0;
        addRow(c, row++, "Protocolo:", schemeField);
        addRow(c, row++, "Host:", hostField);
        addRow(c, row++, "Porta:", portField);
        addRow(c, row++, "Usuário:", userField);
        addRow(c, row++, "Senha:", passwordField);
        addRow(c, row++, "Índice:", indexField);

        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        add(trustAllCheckbox, c);

        JButton testButton = new JButton("Testar conexão");
        JButton saveButton = new JButton("Salvar");
        JButton cancelButton = new JButton("Cancelar");

        JPanel buttons = new JPanel();
        buttons.add(testButton);
        buttons.add(saveButton);
        buttons.add(cancelButton);
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        add(buttons, c);

        loadFrom(config);

        testButton.addActionListener(e -> testConnection());
        saveButton.addActionListener(e -> {
            if (applyTo(config)) {
                saved = true;
                setVisible(false);
            }
        });
        cancelButton.addActionListener(e -> setVisible(false));

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void addRow(GridBagConstraints c, int row, String label, java.awt.Component field) {
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 1;
        add(new JLabel(label), c);
        c.gridx = 1;
        add(field, c);
    }

    private void loadFrom(AppConfig config) {
        schemeField.setSelectedItem(config.getScheme());
        hostField.setText(config.getHost());
        portField.setText(String.valueOf(config.getPort()));
        userField.setText(config.getUsername());
        passwordField.setText(config.getPassword());
        indexField.setText(config.getIndexName());
        trustAllCheckbox.setSelected(config.isTrustAllCerts());
    }

    /** Copia os campos do diálogo para um AppConfig temporário (não altera o original). */
    private AppConfig buildTempConfig() {
        AppConfig temp = new AppConfig();
        temp.setScheme((String) schemeField.getSelectedItem());
        temp.setHost(hostField.getText().trim());
        try {
            temp.setPort(Integer.parseInt(portField.getText().trim()));
        } catch (NumberFormatException ex) {
            temp.setPort(9200);
        }
        temp.setUsername(userField.getText().trim());
        temp.setPassword(new String(passwordField.getPassword()));
        temp.setIndexName(indexField.getText().trim());
        temp.setTrustAllCerts(trustAllCheckbox.isSelected());
        return temp;
    }

    private boolean applyTo(AppConfig config) {
        if (hostField.getText().isBlank() || indexField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Host e índice são obrigatórios.", "Campos obrigatórios",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Porta inválida.", "Campo inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        config.setScheme((String) schemeField.getSelectedItem());
        config.setHost(hostField.getText().trim());
        config.setPort(port);
        config.setUsername(userField.getText().trim());
        config.setPassword(new String(passwordField.getPassword()));
        config.setIndexName(indexField.getText().trim());
        config.setTrustAllCerts(trustAllCheckbox.isSelected());
        config.save();
        return true;
    }

    private void testConnection() {
        AppConfig temp = buildTempConfig();
        try {
            ElasticsearchClient client = new ElasticsearchClient(temp);
            Map<String, Object> response = client.ping();
            Map<String, Object> version = Json.asMap(response.get("version"));
            String versionNumber = version != null ? String.valueOf(version.get("number")) : "desconhecida";
            String clusterName = String.valueOf(response.get("cluster_name"));
            JOptionPane.showMessageDialog(this,
                    "Conexão bem-sucedida!\nCluster: " + clusterName + "\nVersão do Elasticsearch: " + versionNumber,
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Falha na conexão", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
