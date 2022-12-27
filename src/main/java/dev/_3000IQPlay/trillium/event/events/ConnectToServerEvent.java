package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraft.entity.Entity;


public class ConnectToServerEvent extends EventStage {
    public ConnectToServerEvent(String ip) {
        super(1);
        this.ip = ip;
    }

    String ip;


    public String getIp() {
        return ip;
    }
}
