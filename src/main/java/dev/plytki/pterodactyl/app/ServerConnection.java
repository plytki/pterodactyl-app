package dev.plytki.pterodactyl.app;

import com.google.gson.*;
import dev.plytki.pterodactyl.app.data.Server;
import dev.plytki.pterodactyl.app.data.Statistics;
import dev.plytki.pterodactyl.app.event.ConsoleLogEvent;
import dev.plytki.pterodactyl.app.event.StatsEvent;
import dev.plytki.pterodactyl.app.listener.ConsoleLogListener;
import dev.plytki.pterodactyl.app.listener.StatsListener;
import lombok.Getter;
import okhttp3.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the connection to a Pterodactyl server, including WebSocket communication,
 * event dispatching, and listener registration.
 */
public class ServerConnection {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private WebSocketClient webSocketClient;
    private final List<StatsListener> statsListeners = new ArrayList<>();
    private final List<ConsoleLogListener> consoleLogListeners = new ArrayList<>();
    private final Server server;

    @Getter
    private final List<String> consoleLog = new ArrayList<>();
    @Getter
    private Statistics lastStatistics = new Statistics();

    /**
     * Constructor to initialize the ServerConnection with a given server.
     *
     * @param server The server to connect to.
     */
    public ServerConnection(Server server) {
        this.server = server;
        connectToWebSocket(server.getIdentifier());
    }

    /**
     * Registers a StatsListener to receive statistics events.
     *
     * @param listener The listener to register.
     */
    public void registerListener(StatsListener listener) {
        statsListeners.add(listener);
    }

    /**
     * Registers a ConsoleLogListener to receive console log events.
     *
     * @param listener The listener to register.
     */
    public void registerListener(ConsoleLogListener listener) {
        consoleLogListeners.add(listener);
    }

    /**
     * Closes the WebSocket connection.
     */
    public void closeWebSocket() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    /**
     * Sends a power action command to the server.
     *
     * @param action The power action to send (e.g., "start", "stop", "restart").
     */
    public void sendPowerAction(String action) {
        webSocketClient.send("{\"event\":\"set state\",\"args\":[\"" + action + "\"]}");
    }

    /**
     * Sends a command to the server console.
     *
     * @param command The command to send.
     */
    public void sendCommand(String command) {
        webSocketClient.send("{\"event\":\"send command\",\"args\":[\"" + command + "\"]}");
    }

    /**
     * Initializes the WebSocket connection with the given token, URL, and server ID.
     *
     * @param token    The authentication token.
     * @param url      The WebSocket URL.
     * @param serverId The server identifier.
     * @throws URISyntaxException If the URL is not a valid URI.
     */
    private void initializeWebSocket(String token, String url, String serverId) throws URISyntaxException {
        webSocketClient = new WebSocketClient(new URI(url)) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                authenticateWebSocket(token);
            }

            @Override
            public void onMessage(String message) {
                handleMessage(message, serverId);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                // Handle WebSocket closure if needed
            }

            @Override
            public void onError(Exception ex) {
                // Handle WebSocket error if needed
            }
        };
        webSocketClient.connect();
    }

    /**
     * Dispatches a StatsEvent to all registered StatsListeners.
     *
     * @param event The StatsEvent to dispatch.
     */
    private void dispatchEvent(StatsEvent event) {
        statsListeners.forEach(listener -> listener.onEvent(event));
    }

    /**
     * Dispatches a ConsoleLogEvent to all registered ConsoleLogListeners.
     *
     * @param event The ConsoleLogEvent to dispatch.
     */
    private void dispatchEvent(ConsoleLogEvent event) {
        consoleLogListeners.forEach(listener -> listener.onEvent(event));
    }

    /**
     * Removes unwanted characters from the log string.
     *
     * @param log The log string to clean.
     * @return The cleaned log string.
     */
    private String cleanLog(String log) {
        return log
                .replaceAll("\\u001B\\[[;\\d]*[A-Za-z]", "")
                .replaceAll("\\u001B\\[\\?\\d+[lh]", "");
    }

    /**
     * Returns a JSON string to request logs from the server.
     *
     * @return The JSON string to request logs.
     */
    private static String getLogsRequest() {
        return "{\"event\":\"send logs\",\"args\":[null]}";
    }

    /**
     * Returns a JSON string to request statistics from the server.
     *
     * @return The JSON string to request statistics.
     */
    private static String getStatsRequest() {
        return "{\"event\":\"send stats\",\"args\":[null]}";
    }

    /**
     * Fetches a new authentication token for the WebSocket connection.
     *
     * @param serverId The server identifier.
     * @return The new authentication token.
     */
    private String getNewToken(String serverId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(server.getSettings().getURLPrefix() + server.getSettings().hostname() + "/api/client/servers/" + serverId + "/websocket")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + server.getSettings().apiKey())
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String responseBody = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");
                return data.get("token").getAsString();
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Connects to the WebSocket using the given server ID.
     *
     * @param serverId The server identifier.
     */
    private void connectToWebSocket(String serverId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(server.getSettings().getURLPrefix() + server.getSettings().hostname() + "/api/client/servers/" + serverId + "/websocket")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + server.getSettings().apiKey())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.err.println("Failed to fetch WebSocket details: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonObject data = jsonObject.getAsJsonObject("data");
                    String token = data.get("token").getAsString();
                    String webSocketUrl = data.get("socket").getAsString();
                    try {
                        initializeWebSocket(token, webSocketUrl, serverId);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Failed to fetch WebSocket details: " + response.message());
                }
            }
        });
    }

    /**
     * Handles incoming WebSocket messages.
     *
     * @param message  The message received.
     * @param serverId The server identifier.
     */
    private void handleMessage(String message, String serverId) {
        JsonElement element = JsonParser.parseString(message);
        JsonObject object = element.getAsJsonObject();
        String event = object.get("event").getAsString();
        JsonArray args = object.get("args").getAsJsonArray();

        switch (event) {
            case "stats" -> handleStatsEvent(args);
            case "console output" -> handleConsoleOutputEvent(args);
            case "token expiring" -> handleTokenExpiringEvent(serverId);
        }
    }

    /**
     * Handles the "stats" WebSocket event.
     *
     * @param args The event arguments.
     */
    private void handleStatsEvent(JsonArray args) {
        String statsJson = args.get(0).getAsString();
        JsonObject statsObject = JsonParser.parseString(statsJson).getAsJsonObject();

        long memoryBytes = statsObject.get("memory_bytes").getAsLong();
        long memoryLimitBytes = statsObject.get("memory_limit_bytes").getAsLong();
        double cpuAbsolute = statsObject.get("cpu_absolute").getAsDouble();
        JsonObject networkObject = statsObject.get("network").getAsJsonObject();
        long rxBytes = networkObject.get("rx_bytes").getAsLong();
        long txBytes = networkObject.get("tx_bytes").getAsLong();
        String state = statsObject.get("state").getAsString();
        long diskBytes = statsObject.get("disk_bytes").getAsLong();

        Statistics.Network network = new Statistics.Network(rxBytes, txBytes);
        Statistics statistics = new Statistics(memoryBytes, memoryLimitBytes, cpuAbsolute, network, state, diskBytes);
        lastStatistics = statistics;

        StatsEvent statsEvent = new StatsEvent(this, server, statistics);
        dispatchEvent(statsEvent);
    }

    /**
     * Handles the "console output" WebSocket event.
     *
     * @param args The event arguments.
     */
    private void handleConsoleOutputEvent(JsonArray args) {
        String consoleLog = args.get(0).getAsString();
        consoleLog = cleanLog(consoleLog);
        this.consoleLog.add(consoleLog);
        ConsoleLogEvent consoleLogEvent = new ConsoleLogEvent(this, server, consoleLog);
        dispatchEvent(consoleLogEvent);
    }

    /**
     * Handles the "token expiring" WebSocket event.
     *
     * @param serverId The server identifier.
     */
    private void handleTokenExpiringEvent(String serverId) {
        String newToken = getNewToken(serverId);
        authenticateWebSocket(newToken);
    }

    /**
     * Authenticates the WebSocket connection with the given token.
     *
     * @param token The authentication token.
     */
    private void authenticateWebSocket(String token) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event", "auth");
        JsonArray jsonElements = new JsonArray();
        jsonElements.add(token);
        jsonObject.add("args", jsonElements);
        String json = gson.toJson(jsonObject);
        webSocketClient.send(json);
        webSocketClient.send(getLogsRequest());
        webSocketClient.send(getStatsRequest());
    }
}
