package dev.plytki.pterodactyl.app.data;

import java.util.List;

/**
 * Represents the response from the server API, including server data and metadata.
 */
public class ServerResponse {

    public String object;
    public List<ServerData> data;
    public Meta meta;

    /**
     * Represents individual server data in the server response.
     */
    public static class ServerData {
        public String object;
        public ServerAttributes attributes;
    }

    /**
     * Represents the attributes of a server.
     */
    public static class ServerAttributes {
        public boolean server_owner;
        public String identifier;
        public String uuid;
        public String name;
        public String node;
        public SftpDetails sftp_details;
        public String description;
        public Limits limits;
        public FeatureLimits feature_limits;
        public boolean is_suspended;
        public boolean is_installing;
        public Relationships relationships;
    }

    /**
     * Represents the SFTP details of a server.
     */
    public static class SftpDetails {
        public String ip;
        public int port;
    }

    /**
     * Represents the resource limits of a server.
     */
    public static class Limits {
        public int memory;
        public int swap;
        public int disk;
        public int io;
        public int cpu;
    }

    /**
     * Represents the feature limits of a server.
     */
    public static class FeatureLimits {
        public int databases;
        public int allocations;
        public int backups;
    }

    /**
     * Represents the relationships of a server, including allocations.
     */
    public static class Relationships {
        public Allocations allocations;
    }

    /**
     * Represents the allocations of a server.
     */
    public static class Allocations {
        public String object;
        public List<AllocationData> data;

        /**
         * Returns the primary allocation data, which is marked as default.
         *
         * @return The primary allocation data.
         */
        public AllocationData getPrimaryAllocation() {
            return data.stream().filter(allocationData -> allocationData.attributes.is_default).findFirst().orElse(null);
        }
    }

    /**
     * Represents individual allocation data in the server response.
     */
    public static class AllocationData {
        public String object;
        public AllocationAttributes attributes;
    }

    /**
     * Represents the attributes of an allocation.
     */
    public static class AllocationAttributes {
        public int id;
        public String ip;
        public String ip_alias;
        public int port;
        public String notes;
        public boolean is_default;
    }

    /**
     * Represents the metadata of the server response.
     */
    public static class Meta {
        public Pagination pagination;
    }

    /**
     * Represents the pagination details in the metadata.
     */
    public static class Pagination {
        public int total;
        public int count;
        public int per_page;
        public int current_page;
        public int total_pages;
        public Links links;
    }

    /**
     * Represents the links in the pagination details.
     */
    public static class Links {
    }
}
