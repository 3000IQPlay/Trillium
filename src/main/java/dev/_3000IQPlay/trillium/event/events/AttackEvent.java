package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import static dev._3000IQPlay.trillium.util.ItemUtil.mc;

@Cancelable
public class AttackEvent extends EventStage {


        public AttackEvent(Entity attack, int stage) {
            super(stage);
            this.entity = attack;
        }

        Entity entity;


        public Entity getEntity() {
            return entity;
        }


}
