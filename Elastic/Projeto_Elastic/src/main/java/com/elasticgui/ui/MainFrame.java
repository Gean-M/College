package com.elasticgui.ui;

import com.elasticgui.client.ElasticsearchClient;
import com.elasticgui.config.AppConfig;
import com.elasticgui.service.ClusterService;
import com.elasticgui.service.SearchService;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * Janela principal: menu, abas ("Busca" e "Cluster") e barra de status com a
 * conexão atual.
 */
public class MainFrame extends JFrame {

    private AppConfig config;
    private ElasticsearchClient client;
    private SearchService searchService;
    private ClusterService clusterService;

    private final JLabel connectionStatusLabel = new JLabel();
    private ClusterPanel clusterPanel;

    public MainFrame() {
        super("Elasticsearch GUI - baseado em elasticsearch_example / curso de Elasticsearch");
        this.config = AppConfig.loadOrDefault();
        rebuildServices();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(buildMenuBar());

        JTabbedPane tabs = new JTabbedPane();
        SearchPanel searchPanel = new SearchPanel(this::getSearchService);
        clusterPanel = new ClusterPanel(this::getClusterService);
        tabs.addTab("Busca", searchPanel);
        tabs.addTab("Cluster", clusterPanel);

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        setMinimumSize(new Dimension(980, 640));
        setSize(1100, 720);
        setLocationRelativeTo(null);

        updateStatusBar();
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Arquivo");
        JMenuItem connectionItem = new JMenuItem("Configuração de conexão...");
        connectionItem.addActionListener(e -> openConnectionDialog());
        JMenuItem exitItem = new JMenuItem("Sair");
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(connectionItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Ajuda");
        JMenuItem aboutItem = new JMenuItem("Sobre");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private JPanel buildStatusBar() {
        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        status.add(connectionStatusLabel);
        return status;
    }

    private void openConnectionDialog() {
        ConnectionDialog dialog = new ConnectionDialog(this, config);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            rebuildServices();
            updateStatusBar();
            clusterPanel.refresh();
        }
    }

    private void rebuildServices() {
        this.client = new ElasticsearchClient(config);
        this.searchService = new SearchService(client, config);
        this.clusterService = new ClusterService(client);
    }

    private void updateStatusBar() {
        connectionStatusLabel.setText(String.format("Conectado a %s | índice: %s | usuário: %s",
                config.baseUrl(), config.getIndexName(), config.getUsername()));
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Interface gráfica (Swing) para o Elasticsearch.\n" +
                        "Baseada nos requisitos do repositório fbgonzaga/elasticsearch_example\n" +
                        "e no material de aula de Elasticsearch.\n\n" +
                        "Comunica-se diretamente com a API REST do Elasticsearch\n" +
                        "(sem dependências externas nem necessidade de Docker).",
                "Sobre", JOptionPane.INFORMATION_MESSAGE);
    }

    public SearchService getSearchService() {
        return searchService;
    }

    public ClusterService getClusterService() {
        return clusterService;
    }
}
