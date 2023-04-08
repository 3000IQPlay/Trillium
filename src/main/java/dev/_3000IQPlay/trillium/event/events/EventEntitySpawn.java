package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraft.entity.Entity;

public class EventEntitySpawn extends EventStage {
    private final Entity entity;
    public EventEntitySpawn(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}