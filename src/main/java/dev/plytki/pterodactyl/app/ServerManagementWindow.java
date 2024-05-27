package dev.plytki.pterodactyl.app;

import dev.plytki.pterodactyl.app.data.Server;
import dev.plytki.pterodactyl.app.data.Statistics;
import dev.plytki.pterodactyl.app.font.HackFont;
import dev.plytki.pterodactyl.app.listener.ConsoleLogListener;
import dev.plytki.pterodactyl.app.listener.StatsListener;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.AdjustmentEvent;

/**
 * ServerManagementWindow is a GUI application for managing a server.
 * It displays server statistics, logs, and allows sending commands to the server.
 */
public class ServerManagementWindow extends JFrame {

    private final Server server;
    private DefaultCaret caret;
    private int extent;
    private int lastScrollValue = 0;
    private boolean anchor;

    /**
     * Constructs a ServerManagementWindow for the specified server.
     * @param server The server to manage.
     */
    public ServerManagementWindow(Server server) {
        this.server = server;
        initialize();
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Initializes the GUI components.
     */
    private void initialize() {
        setTitle("Manage Server: " + server.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.WEST);

        JPanel consolePanel = createConsolePanel();
        add(consolePanel, BorderLayout.CENTER);

        fetchConsoleOutput((JTextArea) ((JScrollPane) consolePanel.getComponent(0)).getViewport().getView());

        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Creates the header panel with the server name.
     * @return A JPanel for the header.
     */
    @SneakyThrows
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(45, 45, 45));

        JLabel headerLabel = new JLabel("Server Panel");
        headerLabel.setForeground(new Color(245, 245, 245));
        headerLabel.setFont(HackFont.BOLD.deriveFont(16f));

        headerPanel.add(headerLabel);
        return headerPanel;
    }

    /**
     * Creates the button panel with server controls and statistics.
     * @return A JPanel for the buttons and statistics.
     */
    @SneakyThrows
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setPreferredSize(new Dimension(200, getHeight()));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(49, 49, 49));

        JLabel state = createStateLabel();
        JLabel name = createNameLabel();

        JLabel cpuUsage = createStatLabel("CPU Usage: ?");
        JLabel memoryUsage = createStatLabel("Memory: ?");
        JLabel diskUsage = createStatLabel("Disk: ?");

        Statistics lastStatistics = server.getConnection().getLastStatistics();
        updateStatLabels(lastStatistics, cpuUsage, memoryUsage, diskUsage);

        server.getConnection().registerListener((StatsListener) event -> {
            Statistics statistics = event.getStatistics();
            updateStatLabels(statistics, cpuUsage, memoryUsage, diskUsage);
        });

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(name);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(state);
        buttonPanel.add(Box.createVerticalStrut(16));
        buttonPanel.add(createActionButton("Start", "Start the server", "start"));
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(createActionButton("Stop", "Stop the server", "stop"));
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(createActionButton("Restart", "Restart the server", "restart"));
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(createActionButton("Kill", "Kill the server process", "kill"));
        buttonPanel.add(Box.createVerticalStrut(40));
        buttonPanel.add(cpuUsage);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(memoryUsage);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(diskUsage);
        buttonPanel.add(Box.createVerticalGlue());

        return buttonPanel;
    }

    /**
     * Updates the statistics labels with new data.
     * @param statistics The new statistics.
     * @param cpuUsage The CPU usage label.
     * @param memoryUsage The memory usage label.
     * @param diskUsage The disk usage label.
     */
    private void updateStatLabels(Statistics statistics, JLabel cpuUsage, JLabel memoryUsage, JLabel diskUsage) {
        cpuUsage.setText("CPU Usage: " + statistics.getCpuAbsolute() + " %");
        memoryUsage.setText(statistics.getFormattedMemory());
        diskUsage.setText(statistics.getFormattedDisk());
    }

    /**
     * Creates a label for displaying statistics.
     * @param text The initial text of the label.
     * @return A JLabel for statistics.
     */
    @SneakyThrows
    private JLabel createStatLabel(String text) {
        JLabel statLabel = new JLabel(text);
        statLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statLabel.setForeground(new Color(245, 245, 245));
        return statLabel;
    }

    /**
     * Creates a label for displaying the server name.
     * @return A JLabel for the server name.
     */
    @SneakyThrows
    private JLabel createNameLabel() {
        JLabel nameLabel = new JLabel(server.getName());
        nameLabel.setFont(HackFont.BOLD.deriveFont(14f));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return nameLabel;
    }

    /**
     * Creates a label for displaying the server state.
     * @return A JLabel for the server state.
     */
    @SneakyThrows
    private JLabel createStateLabel() {
        Statistics lastStatistics = server.getConnection().getLastStatistics();
        JLabel stateLabel = new JLabel("State: " + (lastStatistics == null ? "?" : lastStatistics.getState()));
        stateLabel.setFont(HackFont.REGULAR.deriveFont(12f));
        stateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        server.getConnection().registerListener((StatsListener) event -> {
            stateLabel.setText("State: " + event.getStatistics().getState());
        });
        return stateLabel;
    }

    /**
     * Creates a button for performing server actions.
     * @param text The button text.
     * @param tooltip The button tooltip.
     * @param action The action command.
     * @return A JButton configured for the action.
     */
    @SneakyThrows
    private JButton createActionButton(String text, String tooltip, String action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setToolTipText(tooltip);
        button.setBackground(new Color(70, 70, 70));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(HackFont.BOLD.deriveFont(14f));
        button.setMinimumSize(new Dimension(150, 40));
        button.setPreferredSize(new Dimension(150, 40));
        button.setMaximumSize(new Dimension(150, 40));
        button.setBorder(new LineBorder(new Color(100, 100, 100), 1, true));
        button.addActionListener(e -> performServerAction(action));

        return button;
    }

    /**
     * Creates the console panel for displaying server logs and input commands.
     * @return A JPanel for the console.
     */
    @SneakyThrows
    private JPanel createConsolePanel() {
        JPanel consolePanel = new JPanel(new BorderLayout());
        consolePanel.setPreferredSize(new Dimension(800, 600));
        consolePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        consolePanel.setBackground(new Color(30, 30, 30));

        JTextArea consoleOutput = new JTextArea();
        consoleOutput.setEditable(false);
        consoleOutput.setLineWrap(true);
        consoleOutput.setFont(HackFont.REGULAR.deriveFont(11f));
        consoleOutput.setBackground(new Color(30, 30, 30));
        consoleOutput.setForeground(new Color(210, 210, 210));

        caret = (DefaultCaret) consoleOutput.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(consoleOutput);
        scrollPane.getVerticalScrollBar().setBackground(new Color(120, 120, 120));
        scrollPane.getVerticalScrollBar().addAdjustmentListener(this::handleScrollAdjustment);
        scrollPane.setBorder(null);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        inputPanel.setBackground(new Color(30, 30, 30));

        JTextField commandInput = new JTextField();
        commandInput.setPreferredSize(new Dimension(0, 35));
        commandInput.addActionListener(e -> handleCommandInput(commandInput));
        commandInput.setBackground(new Color(60, 60, 60));
        commandInput.setForeground(new Color(205, 205, 205));
        commandInput.setSelectionColor(new Color(165, 165, 165));
        commandInput.setCaretColor(new Color(165, 165, 165));
        commandInput.setBorder(new RoundedBorder(10, new Color(60, 60, 60)));
        commandInput.setFont(HackFont.REGULAR.deriveFont(12f));
        commandInput.setMargin(new Insets(5, 5, 5, 5));

        inputPanel.add(commandInput, BorderLayout.CENTER);

        consolePanel.add(scrollPane, BorderLayout.CENTER);
        consolePanel.add(inputPanel, BorderLayout.SOUTH);

        return consolePanel;
    }

    /**
     * Custom border class for rounded edges.
     */
    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 1, radius + 1);
        }
    }

    /**
     * Creates the status bar displaying the connection status.
     * @return A JPanel for the status bar.
     */
    @SneakyThrows
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusBar.setBackground(new Color(45, 45, 45));

        JLabel statusLabel = new JLabel("Status: Connected");
        statusLabel.setForeground(new Color(245, 245, 245));
        statusLabel.setFont(HackFont.REGULAR.deriveFont(12f));

        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }

    /**
     * Handles scroll adjustments to maintain anchor behavior.
     * @param e The adjustment event.
     */
    private void handleScrollAdjustment(AdjustmentEvent e) {
        Adjustable adjustable = e.getAdjustable();
        int max = adjustable.getMaximum();
        extent = adjustable.getVisibleAmount();
        int value = adjustable.getValue();
        if (anchor) {
            if (lastScrollValue > value) setAnchor(false);
        } else {
            if (Math.abs(value + extent - max) < 100) setAnchor(true);
        }
        lastScrollValue = value;
    }

    /**
     * Handles command input from the text field.
     * @param commandInput The command input text field.
     */
    private void handleCommandInput(JTextField commandInput) {
        String command = commandInput.getText();
        if (!command.isEmpty()) {
            sendCommandToServer(command);
            commandInput.setText("");
        }
    }

    /**
     * Performs a server action based on the action command.
     * @param action The action command.
     */
    private void performServerAction(String action) {
        server.getConnection().sendPowerAction(action);
    }

    /**
     * Sends a command to the server.
     * @param command The command to send.
     */
    private void sendCommandToServer(String command) {
        server.getConnection().sendCommand(command);
    }

    /**
     * Fetches the console output and updates the JTextArea.
     * @param consoleOutput The JTextArea for console output.
     */
    private void fetchConsoleOutput(JTextArea consoleOutput) {
        for (String log : server.getConnection().getConsoleLog()) {
            if (!consoleOutput.getText().isEmpty()) consoleOutput.append("\n");
            consoleOutput.append(log);
        }
        server.getConnection().registerListener((ConsoleLogListener) event -> {
            SwingUtilities.invokeLater(() -> {
                String log = event.getLog();
                if (!consoleOutput.getText().isEmpty()) consoleOutput.append("\n");
                consoleOutput.append(log);
            });
        });
    }

    /**
     * Sets the anchor state for console auto-scrolling.
     * @param anchor The anchor state.
     */
    private void setAnchor(boolean anchor) {
        this.anchor = anchor;
        if (this.anchor) {
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            caret.setDot(caret.getDot() + extent);
        } else {
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        }
    }
}
