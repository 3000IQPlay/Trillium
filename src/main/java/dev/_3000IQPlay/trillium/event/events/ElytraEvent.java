package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import java.awt.*;

@Cancelable
public class ElytraEvent extends EventStage {
    //private static ElytraEvent INSTANCE = new ElytraEvent();

    private Entity entity;

    public ElytraEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
