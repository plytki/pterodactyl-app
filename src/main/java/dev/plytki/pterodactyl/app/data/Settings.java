package dev.plytki.pterodactyl.app.data;

public record Settings(String apiKey, String hostname, boolean ssl) {

    public String getURLPrefix() {
        return ssl ? "https://" : "http://";
    }

}
