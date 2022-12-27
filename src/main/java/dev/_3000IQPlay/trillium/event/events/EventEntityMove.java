package dev._3000IQPlay.trillium.event.events;


import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EventEntityMove extends EventStage {
    private Entity ctx;
    private Vec3d from;

    public EventEntityMove(Entity ctx, Vec3d from) {
        this.ctx = ctx;
        this.from = from;
    }

    public Vec3d from() {
        return this.from;
    }

    public Entity ctx() {
        return this.ctx;
    }
}