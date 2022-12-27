package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.modules.Feature;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ClientEvent
        extends EventStage {
    private Feature feature;
    private Setting setting;

    public ClientEvent(int stage, Feature feature) {
        super(stage);
        this.feature = feature;
    }

    public ClientEvent(Setting setting) {
        super(2);
        this.setting = setting;
    }

    public Feature getFeature() {
        return this.feature;
    }

    public Setting getSetting() {
        return this.setting;
    }
}

