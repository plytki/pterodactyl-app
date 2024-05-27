package dev.plytki.pterodactyl.app.event;

import dev.plytki.pterodactyl.app.data.Server;
import dev.plytki.pterodactyl.app.data.Statistics;
import lombok.Getter;

import java.util.EventObject;

@Getter
public class StatsEvent extends EventObject {

    private final Server server;
    private final Statistics statistics;

    public StatsEvent(Object source, Server server, Statistics statistics) {
        super(source);
        this.server = server;
        this.statistics = statistics;
    }

}
