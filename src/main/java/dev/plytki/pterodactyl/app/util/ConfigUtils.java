package dev.plytki.pterodactyl.app.util;

import okhttp3.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

/**
 * Utility class for handling configuration-related tasks, such as loading and saving properties,
 * prompting for API details, and validating API keys.
 */
public class ConfigUtils {

    /**
     * Loads configuration properties from the specified file.
     *
     * @param configFilePath The path to the configuration file.
     * @return A Properties object containing the loaded configuration.
     */
    public static Properties loadConfig(String configFilePath) {
        Properties config = new Properties();
        File configFile = new File(configFilePath);
        if (!configFile.exists()) {
            try {
                Files.createFile(configFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (FileInputStream input = new FileInputStream(configFilePath)) {
            config.load(input);
        } catch (IOException e) {
            // Handle the exception (e.g., log it)
            e.printStackTrace();
        }
        return config;
    }

    /**
     * Saves configuration properties to the specified file.
     *
     * @param config The Properties object containing the configuration to save.
     * @param configFilePath The path to the configuration file.
     */
    public static void saveConfig(Properties config, String configFilePath) {
        try (FileOutputStream output = new FileOutputStream(configFilePath)) {
            config.store(output, null);
        } catch (IOException e) {
            // Handle the exception (e.g., log it)
            e.printStackTrace();
        }
    }

    /**
     * Prompts the user for API details (API key and hostname) through a dialog box.
     *
     * @return An array containing the API key, hostname, and SSL usage flag,
     *         or null if the user cancels the dialog.
     */
    public static String[] promptForApiDetails() {
        JPanel panel = createApiDetailsPanel();
        JTextField apiKeyField = (JTextField) panel.getComponent(1);
        JTextField hostnameField = (JTextField) panel.getComponent(3);
        JCheckBox sslCheckbox = (JCheckBox) panel.getComponent(4);

        // Loop until valid input is provided or user cancels
        while (true) {
            int result = JOptionPane.showConfirmDialog(null, panel, "API Details Required", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String apiKey = apiKeyField.getText();
                String hostname = hostnameField.getText();
                boolean useSsl = sslCheckbox.isSelected();

                if (isApiKeyValid(apiKey, hostname, useSsl)) {
                    return new String[]{apiKey, hostname, String.valueOf(useSsl)};
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid API Key or Hostname. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                return null;  // Return null if the user cancels
            }
        }
    }

    /**
     * Validates the provided API key by making a test request to the specified hostname.
     *
     * @param apiKey The API key to validate.
     * @param hostname The hostname of the server.
     * @param useSsl Whether to use SSL for the request.
     * @return true if the API key is valid, false otherwise.
     */
    public static boolean isApiKeyValid(String apiKey, String hostname, boolean useSsl) {
        if (apiKey == null || apiKey.isEmpty() || hostname == null || hostname.isEmpty()) {
            return false;
        }

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String protocol = useSsl ? "https://" : "http://";
        Request request = new Request.Builder()
                .url(protocol + hostname + "/api/client/account")
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.code() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Creates the panel for prompting API details from the user.
     *
     * @return The created JPanel.
     */
    private static JPanel createApiDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding

        // API Key Input
        JLabel apiKeyLabel = new JLabel("Enter Pterodactyl API Key:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(apiKeyLabel, gbc);

        JTextField apiKeyField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(apiKeyField, gbc);

        // Hostname Input
        JLabel hostnameLabel = new JLabel("Enter Hostname:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(hostnameLabel, gbc);

        JTextField hostnameField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(hostnameField, gbc);

        // SSL Checkbox
        JCheckBox sslCheckbox = new JCheckBox("Use SSL");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(sslCheckbox, gbc);

        return panel;
    }
}
