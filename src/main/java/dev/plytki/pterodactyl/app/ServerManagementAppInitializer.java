package dev.plytki.pterodactyl.app;

import com.formdev.flatlaf.FlatDarkLaf;
import dev.plytki.pterodactyl.app.util.ConfigUtils;

import javax.swing.*;
import java.util.Properties;

public class ServerManagementAppInitializer {

    public static final String CONFIG_FILE = "config.properties";
    public static final String API_KEY_PROPERTY = "pterodactyl.api.key";
    public static final String HOSTNAME_PROPERTY = "hostname";
    public static final String SSL_PROPERTY = "ssl";

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            Properties config = ConfigUtils.loadConfig(CONFIG_FILE);
            String apiKey = config.getProperty(API_KEY_PROPERTY);
            String hostname = config.getProperty(HOSTNAME_PROPERTY);
            boolean ssl = Boolean.parseBoolean(config.getProperty(SSL_PROPERTY));

            if (apiKey == null || hostname == null || !ConfigUtils.isApiKeyValid(apiKey, hostname, ssl)) {
                String[] apiDetails = ConfigUtils.promptForApiDetails();
                if (apiDetails != null) {
                    apiKey = apiDetails[0];
                    hostname = apiDetails[1];
                    ssl = Boolean.parseBoolean(apiDetails[2]);

                    config.setProperty(API_KEY_PROPERTY, apiKey);
                    config.setProperty(HOSTNAME_PROPERTY, hostname);
                    config.setProperty(SSL_PROPERTY, String.valueOf(ssl));

                    ConfigUtils.saveConfig(config, CONFIG_FILE);
                } else {
                    System.exit(0);  // Exit the application if the user cancels
                }
            }

            ServerManagementApp app = new ServerManagementApp(apiKey, hostname, ssl);
            app.setVisible(true);
        });
    }
}
