package dev.plytki.pterodactyl.app.component;

import dev.plytki.pterodactyl.app.ServerManagementWindow;
import dev.plytki.pterodactyl.app.data.Server;
import dev.plytki.pterodactyl.app.data.ServerResponse;
import dev.plytki.pterodactyl.app.data.Statistics;
import dev.plytki.pterodactyl.app.event.StatsEvent;
import dev.plytki.pterodactyl.app.font.HackFont;
import dev.plytki.pterodactyl.app.listener.StatsListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.RoundRectangle2D;

/**
 * Panel that displays information about a server, including its name, IP,
 * port, CPU usage, memory usage, disk usage, and state. It also allows the user
 * to copy the server IP and port and open a server management window.
 */
public class ServerPanel extends JPanel {

    /**
     * Constructs a ServerPanel for the given server.
     *
     * @param server The server to display information for.
     */
    public ServerPanel(Server server) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setOpaque(false);

        JLabel nameLabel = createNameLabel(server.getName());
        JLabel ipLabel = createIpLabel(server);
        JButton copyButton = createCopyButton(ipLabel.getText());
        JLabel cpuLabel = createStatusLabel("CPU: ?");
        JLabel memoryLabel = createStatusLabel("Memory: ?");
        JLabel diskLabel = createStatusLabel("Disk: ?");
        JLabel stateLabel = createStatusLabel("State: ?");
        JButton manageButton = createManageButton();

        manageButton.addActionListener(e -> openServerManagementWindow(server));
        server.getConnection().registerListener((StatsListener) event ->
                SwingUtilities.invokeLater(() -> updateStats(event, cpuLabel, memoryLabel, diskLabel, stateLabel))
        );

        JPanel leftPanel = createLeftPanel(nameLabel, ipLabel, copyButton);
        JPanel rightPanel = createRightPanel(cpuLabel, memoryLabel, diskLabel);
        JPanel buttonPanel = createButtonPanel(manageButton);
        JPanel stateIndicator = createStateIndicator();

        server.getConnection().registerListener((StatsListener) event ->
                updateStateIndicator(event, stateIndicator)
        );

        addPanels(leftPanel, rightPanel, buttonPanel, stateIndicator);
        createBorder();
    }

    private JLabel createNameLabel(String name) {
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(HackFont.REGULAR.deriveFont(13f));
        nameLabel.setForeground(Color.WHITE);
        return nameLabel;
    }

    private JLabel createIpLabel(Server server) {
        ServerResponse.Allocations allocations = server.getRelationships().allocations;
        ServerResponse.AllocationData primaryAllocation = allocations.getPrimaryAllocation();
        String ipPort = primaryAllocation.attributes.ip + ":" + primaryAllocation.attributes.port;
        JLabel ipLabel = new JLabel(ipPort);
        ipLabel.setFont(HackFont.REGULAR.deriveFont(11f));
        ipLabel.setForeground(Color.LIGHT_GRAY);
        return ipLabel;
    }

    private JButton createCopyButton(String ipPort) {
        JButton copyButton = new JButton("Copy");
        copyButton.setFocusPainted(false);
        copyButton.setBackground(new Color(70, 70, 70));
        copyButton.setForeground(Color.WHITE);
        copyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        copyButton.setFont(HackFont.REGULAR.deriveFont(11f));
        copyButton.addActionListener(e -> copyToClipboard(ipPort));
        return copyButton;
    }

    private void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    private JLabel createStatusLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }

    private JButton createManageButton() {
        JButton button = new JButton("Manage");
        button.setFont(HackFont.BOLD.deriveFont(13f));
        button.setPreferredSize(new Dimension(180, 70));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 70, 70));
        button.setForeground(Color.WHITE);
        button.setBorder(new LineBorder(new Color(100, 100, 100), 1, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createLeftPanel(JLabel nameLabel, JLabel ipLabel, JButton copyButton) {
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(new Color(45, 45, 45));
        GridBagConstraints gbc = createGbc(0, 0);
        leftPanel.add(nameLabel, gbc);
        gbc.gridy = 1;
        leftPanel.add(ipLabel, gbc);
        gbc.gridx = 1;
        leftPanel.add(copyButton, gbc);
        return leftPanel;
    }

    private JPanel createRightPanel(JLabel cpuLabel, JLabel memoryLabel, JLabel diskLabel) {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(45, 45, 45));
        GridBagConstraints gbc = createGbc(0, 0);
        JPanel resourcePanel = createResourcePanel(cpuLabel, memoryLabel, diskLabel, gbc);
        rightPanel.add(resourcePanel, gbc);
        return rightPanel;
    }

    private JPanel createResourcePanel(JLabel cpuLabel, JLabel memoryLabel, JLabel diskLabel, GridBagConstraints gbc) {
        JPanel resourcePanel = new JPanel(new GridBagLayout());
        resourcePanel.setBackground(new Color(45, 45, 45));
        gbc.gridy = 0;
        resourcePanel.add(cpuLabel, gbc);
        gbc.gridx = 1;
        resourcePanel.add(memoryLabel, gbc);
        gbc.gridx = 2;
        resourcePanel.add(diskLabel, gbc);
        return resourcePanel;
    }

    private JPanel createButtonPanel(JButton manageButton) {
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(45, 45, 45));
        GridBagConstraints gbc = createGbc(0, 0);
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.PAGE_END;
        buttonPanel.add(manageButton, gbc);
        return buttonPanel;
    }

    private JPanel createStateIndicator() {
        RoundPanel stateIndicator = new RoundPanel(15, 15, 15, 15);
        stateIndicator.setBackground(Color.GRAY);
        stateIndicator.setPreferredSize(new Dimension(12, 0));
        return stateIndicator;
    }

    private void addPanels(JPanel leftPanel, JPanel rightPanel, JPanel buttonPanel, JPanel stateIndicator) {
        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.setBackground(new Color(45, 45, 45));
        JPanel spacerPanel = createSpacerPanel();
        westPanel.add(stateIndicator, BorderLayout.WEST);
        westPanel.add(spacerPanel, BorderLayout.CENTER);
        westPanel.add(leftPanel, BorderLayout.EAST);

        add(westPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
    }

    private JPanel createSpacerPanel() {
        JPanel spacerPanel = new JPanel();
        spacerPanel.setOpaque(false);
        spacerPanel.setPreferredSize(new Dimension(10, 0));
        return spacerPanel;
    }

    private GridBagConstraints createGbc(int gridx, int gridy) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void openServerManagementWindow(Server server) {
        SwingUtilities.invokeLater(() -> {
            ServerManagementWindow serverManagementWindow = new ServerManagementWindow(server);
            serverManagementWindow.setVisible(true);
        });
    }

    private void updateStats(StatsEvent event, JLabel cpuLabel, JLabel memoryLabel, JLabel diskLabel, JLabel stateLabel) {
        Statistics statistics = event.getStatistics();
        cpuLabel.setText(String.format("CPU: %.2f%%", statistics.getCpuAbsolute()));
        memoryLabel.setText(statistics.getFormattedMemory());
        diskLabel.setText(statistics.getFormattedDisk());
        stateLabel.setText("State: " + statistics.getState());
    }

    private void updateStateIndicator(StatsEvent event, JPanel stateIndicator) {
        String state = event.getStatistics().getState();
        Color stateColor = switch (state) {
            case "running" -> new Color(48, 131, 48);
            case "offline" -> new Color(131, 48, 48);
            case "starting", "stopping" -> new Color(131, 105, 48);
            default -> Color.GRAY;
        };
        stateIndicator.setBackground(stateColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(45, 45, 45));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2d.setColor(new Color(70, 70, 70));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1.5, getHeight() - 1.5, 15, 15));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1000, 130);
    }

    private void createBorder() {
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(70, 70, 70)),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
        ));
    }
}
