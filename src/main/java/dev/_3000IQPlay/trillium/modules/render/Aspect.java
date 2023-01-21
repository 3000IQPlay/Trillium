package dev._3000IQPlay.trillium.modules.render;

import dev._3000IQPlay.trillium.event.events.PerspectiveEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Aspect
        extends Module {
    public Setting<Float> aspect = this.register(new Setting<Float>("Aspect", 1.0f, 0.1f, 5.0f));

    public Aspect() {
        super("Aspect", "Change ur screen aspect like fortnite.", Module.Category.RENDER, true, false, false);
    }

    @SubscribeEvent
    public void onPerspectiveEvent(PerspectiveEvent perspectiveEvent) {
        perspectiveEvent.setAspect(this.aspect.getValue().floatValue());
    }
}