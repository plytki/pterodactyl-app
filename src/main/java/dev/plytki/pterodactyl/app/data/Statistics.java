package dev.plytki.pterodactyl.app.data;

import lombok.Getter;

@Getter
public class Statistics {

    private final long memoryBytes;
    private final long memoryLimitBytes;
    private final double cpuAbsolute;
    private final Network network;
    private final String state;
    private final long diskBytes;

    public Statistics() {
        this.memoryBytes = 0;
        this.memoryLimitBytes = 0;
        this.cpuAbsolute = 0;
        this.network = new Network(0, 0);
        this.state = "";
        this.diskBytes = 0;
    }

    public Statistics(long memoryBytes, long memoryLimitBytes, double cpuAbsolute, Network network, String state, long diskBytes) {
        this.memoryBytes = memoryBytes;
        this.memoryLimitBytes = memoryLimitBytes;
        this.cpuAbsolute = cpuAbsolute;
        this.network = network;
        this.state = state;
        this.diskBytes = diskBytes;
    }

    // New method to format the stats
    public String getFormattedDisk() {
        return formatBytes(diskBytes, "Disk");
    }

    public String getFormattedMemory() {
        return formatBytes(memoryBytes, "Memory");
    }

    public String getFormattedMemoryLimit() {
        return formatBytes(memoryLimitBytes, "Memory Limit");
    }

    public String getFormattedNetwork() {
        return String.format("Network: Rx %s, Tx %s", formatBytes(network.rxBytes(), ""), formatBytes(network.txBytes(), ""));
    }

    private String formatBytes(long bytes, String label) {
        double value = bytes;
        String unit = "B";

        if (bytes >= 1024) {
            value = bytes / 1024.0;
            unit = "KiB";
        }
        if (bytes >= 1024 * 1024) {
            value = bytes / (1024.0 * 1024.0);
            unit = "MiB";
        }
        if (bytes >= 1024 * 1024 * 1024) {
            value = bytes / (1024.0 * 1024.0 * 1024.0);
            unit = "GiB";
        }
        if (bytes >= 1024L * 1024L * 1024L * 1024L) {
            value = bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0);
            unit = "TiB";
        }

        if (label.isEmpty()) {
            return String.format("%.2f %s", value, unit);
        } else {
            return String.format("%s: %.2f %s", label, value, unit);
        }
    }

    public record Network(long rxBytes, long txBytes) {

    }

}