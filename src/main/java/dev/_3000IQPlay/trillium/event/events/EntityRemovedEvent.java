package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraft.entity.Entity;

public class EntityRemovedEvent extends EventStage {
    public Entity entity;

    public EntityRemovedEvent(Entity entity) {
        this.entity = entity;
    }
}