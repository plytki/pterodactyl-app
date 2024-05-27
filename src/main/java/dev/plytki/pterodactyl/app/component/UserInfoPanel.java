package dev.plytki.pterodactyl.app.component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.plytki.pterodactyl.app.APIClient;
import dev.plytki.pterodactyl.app.data.Settings;
import dev.plytki.pterodactyl.app.font.HackFont;
import okhttp3.Response;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Panel that displays user information fetched from the API.
 */
public class UserInfoPanel extends JPanel {

    private final JLabel userInfoLabel;
    private final Gson gson = new Gson();

    /**
     * Constructs a UserInfoPanel with the specified settings.
     *
     * @param settings The settings used to configure the API client.
     */
    public UserInfoPanel(Settings settings) {
        setLayout(new BorderLayout());
        userInfoLabel = createUserInfoLabel();
        add(userInfoLabel, BorderLayout.CENTER);
        displayUserInfo(settings);
    }

    /**
     * Creates and configures the user info label.
     *
     * @return The configured user info label.
     */
    private JLabel createUserInfoLabel() {
        JLabel label = new JLabel("User info will be displayed here.");
        label.setFont(HackFont.ITALIC.deriveFont(10f));
        label.setForeground(Color.WHITE);
        label.setBackground(new Color(30, 30, 30));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }

    /**
     * Fetches and displays user information using the provided settings.
     *
     * @param settings The settings used to configure the API client.
     */
    private void displayUserInfo(Settings settings) {
        APIClient apiClient = new APIClient(settings);
        try (Response response = apiClient.getUserInfo()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                JsonObject attributes = jsonObject.getAsJsonObject("attributes");
                String username = attributes.get("username").getAsString();
                String firstName = attributes.get("first_name").getAsString();
                String lastName = attributes.get("last_name").getAsString();
                userInfoLabel.setText(String.format("User: %s (%s %s)", username, firstName, lastName));
            } else {
                userInfoLabel.setText("Failed to fetch user info.");
            }
        } catch (IOException e) {
            userInfoLabel.setText("Error occurred while fetching user info.");
        }
    }
}
