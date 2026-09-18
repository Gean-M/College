package com.elasticgui.ui;

import com.elasticgui.service.ClusterService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Painel administrativo com os comandos vistos logo na primeira aula:
 * GET /_cat/health?v, GET /_cat/nodes?v e GET /_cat/indices?v.
 */
public class ClusterPanel extends JPanel {

    private final Supplier<ClusterService> clusterServiceSupplier;

    private final JButton refreshButton = new JButton("Atualizar");
    private final JLabel healthDot = new JLabel("\u25CF");
    private final JLabel healthSummary = new JLabel("Clique em \"Atualizar\" para consultar o cluster.");

    private final MapTableModel nodesModel = new MapTableModel(
            List.of("name", "ip", "node.role", "master", "heap.percent", "ram.percent", "cpu"),
            List.of("Nome", "IP", "Papel", "Master", "Heap %", "RAM %", "CPU %"));

    private final MapTableModel indicesModel = new MapTableModel(
            List.of("health", "status", "index", "pri", "rep", "docs.count", "store.size"),
            List.of("Saúde", "Status", "Índice", "Shards Pri.", "Réplicas", "Documentos", "Tamanho"));

    public ClusterPanel(Supplier<ClusterService> clusterServiceSupplier) {
        this.clusterServiceSupplier = clusterServiceSupplier;
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        healthDot.setForeground(Color.GRAY);
        healthDot.setFont(healthDot.getFont().deriveFont(Font.BOLD, 16f));
        top.add(refreshButton);
        top.add(healthDot);
        top.add(healthSummary);
        add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        JTable nodesTable = new JTable(nodesModel);
        JTable indicesTable = new JTable(indicesModel);
        tabs.addTab("Nós do cluster", new JScrollPane(nodesTable));
        tabs.addTab("Índices", new JScrollPane(indicesTable));
        add(tabs, BorderLayout.CENTER);

        refreshButton.addActionListener(e -> refresh());
    }

    public void refresh() {
        refreshButton.setEnabled(false);
        healthSummary.setText("Consultando...");
        healthDot.setForeground(Color.GRAY);

        UiUtils.runAsync(
                () -> clusterServiceSupplier.get().health(),
                healthList -> {
                    applyHealth(healthList);
                    loadNodes();
                },
                error -> {
                    refreshButton.setEnabled(true);
                    healthSummary.setText("Falha ao consultar o cluster.");
                    UiUtils.showError(this, "Erro ao consultar /_cat/health", error);
                }
        );
    }

    private void loadNodes() {
        UiUtils.runAsync(
                () -> clusterServiceSupplier.get().nodes(),
                nodes -> {
                    nodesModel.setRows(nodes);
                    loadIndices();
                },
                error -> {
                    refreshButton.setEnabled(true);
                    UiUtils.showError(this, "Erro ao consultar /_cat/nodes", error);
                }
        );
    }

    private void loadIndices() {
        UiUtils.runAsync(
                () -> clusterServiceSupplier.get().indices(),
                indices -> {
                    indicesModel.setRows(indices);
                    refreshButton.setEnabled(true);
                },
                error -> {
                    refreshButton.setEnabled(true);
                    UiUtils.showError(this, "Erro ao consultar /_cat/indices", error);
                }
        );
    }

    private void applyHealth(List<Map<String, Object>> healthList) {
        if (healthList == null || healthList.isEmpty()) {
            healthSummary.setText("Sem informação de saúde do cluster.");
            return;
        }
        Map<String, Object> health = healthList.get(0);
        String status = String.valueOf(health.get("status"));
        healthDot.setForeground(colorForStatus(status));
        healthSummary.setText(String.format(
                "Cluster \"%s\" - status: %s | nós: %s | shards ativos: %s | não atribuídos: %s",
                health.get("cluster"), status, health.get("node.total"),
                health.get("active_shards"), health.get("unassign")));
    }

    private static Color colorForStatus(String status) {
        if (status == null) {
            return Color.GRAY;
        }
        switch (status.toLowerCase()) {
            case "green":
                return new Color(0, 140, 0);
            case "yellow":
                return new Color(200, 160, 0);
            case "red":
                return new Color(190, 0, 0);
            default:
                return Color.GRAY;
        }
    }
}
