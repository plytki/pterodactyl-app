package dev.plytki.pterodactyl.app.listener;

import dev.plytki.pterodactyl.app.event.StatsEvent;

public interface StatsListener {

    void onEvent(StatsEvent event);

}
