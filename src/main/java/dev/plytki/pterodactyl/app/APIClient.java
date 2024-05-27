package dev.plytki.pterodactyl.app;

import dev.plytki.pterodactyl.app.data.Settings;
import okhttp3.*;

import java.io.IOException;

public class APIClient {

    private final OkHttpClient client;
    private final Settings settings;

    public APIClient(Settings settings) {
        this.settings = settings;
        this.client = new OkHttpClient.Builder().build();
    }

    public Response getUserInfo() throws IOException {
        Request request = new Request.Builder()
                .url(settings.getURLPrefix() + settings.hostname() + "/api/client/account")
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + settings.apiKey())
                .build();

        return client.newCall(request).execute();
    }

    public Response getServerInfo() throws IOException {
        Request request = new Request.Builder()
                .url(settings.getURLPrefix() + settings.hostname() + "/api/client")
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + settings.apiKey())
                .build();

        return client.newCall(request).execute();
    }
}
