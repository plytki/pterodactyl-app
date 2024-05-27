package dev.plytki.pterodactyl.app.data;

import dev.plytki.pterodactyl.app.ServerConnection;
import lombok.Getter;

/**
 * Represents a server with various attributes and settings.
 * Manages the connection to the server.
 */
@Getter
public class Server {

    private final Settings settings;
    private ServerConnection connection;

    private final boolean serverOwner;
    private final String identifier;
    private final String uuid;
    private final String name;
    private final String node;
    private final ServerResponse.SftpDetails sftpDetails;
    private final String description;
    private final ServerResponse.Limits limits;
    private final ServerResponse.FeatureLimits featureLimits;
    private final boolean isSuspended;
    private final boolean isInstalling;
    private final ServerResponse.Relationships relationships;

    /**
     * Constructs a Server object with the given attributes and settings.
     *
     * @param attributes The server attributes.
     * @param settings   The settings for the server.
     */
    public Server(ServerResponse.ServerAttributes attributes, Settings settings) {
        this.settings = settings;
        this.identifier = attributes.identifier;
        this.uuid = attributes.uuid;
        this.name = attributes.name;
        this.node = attributes.node;
        this.sftpDetails = attributes.sftp_details;
        this.description = attributes.description;
        this.limits = attributes.limits;
        this.featureLimits = attributes.feature_limits;
        this.isSuspended = attributes.is_suspended;
        this.isInstalling = attributes.is_installing;
        this.relationships = attributes.relationships;
        this.serverOwner = attributes.server_owner;
    }

    /**
     * Returns the connection to the server, initializing it if necessary.
     *
     * @return The ServerConnection object.
     */
    public ServerConnection getConnection() {
        if (connection == null) {
            connection = new ServerConnection(this);
        }
        return connection;
    }
}
