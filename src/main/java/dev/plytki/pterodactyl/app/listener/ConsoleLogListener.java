package dev.plytki.pterodactyl.app.listener;

import dev.plytki.pterodactyl.app.event.ConsoleLogEvent;

public interface ConsoleLogListener {

    void onEvent(ConsoleLogEvent event);

}
