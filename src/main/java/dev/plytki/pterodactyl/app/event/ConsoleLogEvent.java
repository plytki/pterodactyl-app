package dev.plytki.pterodactyl.app.event;

import dev.plytki.pterodactyl.app.data.Server;
import lombok.Getter;

import java.util.EventObject;

@Getter
public class ConsoleLogEvent extends EventObject {

    private final Server server;
    private final String log;

    public ConsoleLogEvent(Object source, Server server, String log) {
        super(source);
        this.server = server;
        this.log = log;
    }

}
