package dev.plytki.pterodactyl.app.component;

import com.google.gson.Gson;
import dev.plytki.pterodactyl.app.APIClient;
import dev.plytki.pterodactyl.app.data.Server;
import dev.plytki.pterodactyl.app.data.ServerResponse;
import dev.plytki.pterodactyl.app.data.Settings;
import okhttp3.Response;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Panel that displays information about servers fetched from the API.
 */
public class ServerInfoPanel extends JPanel {

    private final Gson gson = new Gson();
    private final JPanel serverInfoPanel;

    /**
     * Constructs a ServerInfoPanel with the specified settings.
     *
     * @param settings The settings used to configure the API client.
     */
    public ServerInfoPanel(Settings settings) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(30, 30, 30));

        serverInfoPanel = createServerInfoPanel();
        add(serverInfoPanel);

        displayServerInfo(settings);
    }

    /**
     * Creates and configures the server info panel.
     *
     * @return The configured server info panel.
     */
    private JPanel createServerInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 30));
        return panel;
    }

    /**
     * Fetches and displays server information using the provided settings.
     *
     * @param settings The settings used to configure the API client.
     */
    private void displayServerInfo(Settings settings) {
        APIClient apiClient = new APIClient(settings);

        try (Response response = apiClient.getServerInfo()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                ServerResponse serverResponse = gson.fromJson(responseBody, ServerResponse.class);

                serverInfoPanel.removeAll();
                for (ServerResponse.ServerData serverData : serverResponse.data) {
                    Server server = new Server(serverData.attributes, settings);
                    ServerPanel serverPanel = new ServerPanel(server);
                    serverInfoPanel.add(serverPanel);
                }
                serverInfoPanel.revalidate();
                serverInfoPanel.repaint();
            } else {
                displayErrorMessage("Failed to fetch server info.");
            }
        } catch (IOException e) {
            displayErrorMessage("Error occurred while fetching server info.");
        }
    }

    /**
     * Displays an error message in the server info panel.
     *
     * @param message The error message to display.
     */
    private void displayErrorMessage(String message) {
        serverInfoPanel.removeAll();
        JLabel errorLabel = new JLabel(message);
        errorLabel.setForeground(Color.RED);
        serverInfoPanel.add(errorLabel);
        serverInfoPanel.revalidate();
        serverInfoPanel.repaint();
    }
}
