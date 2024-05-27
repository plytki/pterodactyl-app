package dev.plytki.pterodactyl.app;

import dev.plytki.pterodactyl.app.component.ServerInfoPanel;
import dev.plytki.pterodactyl.app.component.UserInfoPanel;
import dev.plytki.pterodactyl.app.data.Settings;
import dev.plytki.pterodactyl.app.util.ScrollUtils;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class ServerManagementApp extends JFrame {

    private final Settings settings;

    public ServerManagementApp(String apiKey, String hostname, boolean ssl) {
        this.settings = new Settings(apiKey, hostname, ssl);
        initialize();
        pack();
        setLocationRelativeTo(null);
    }

    private void initialize() {
        setTitle("Pterodactyl Management App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        UserInfoPanel userInfoPanel = new UserInfoPanel(settings);
        ServerInfoPanel serverInfoPanel = new ServerInfoPanel(settings);

        add(userInfoPanel, BorderLayout.NORTH);
        JScrollPane comp = new JScrollPane(serverInfoPanel);
        ScrollUtils.fixScrolling(comp);
        add(comp, BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1100, 650);
    }

}
