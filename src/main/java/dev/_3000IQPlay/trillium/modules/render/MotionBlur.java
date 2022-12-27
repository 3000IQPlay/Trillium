package dev._3000IQPlay.trillium.modules.render;

import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.manager.MotionBlurResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class MotionBlur extends Module {


    public MotionBlur() {
        super("MotionBlur", "MotionBlur", Category.RENDER, true, false, false);
    }
    public final Setting<Float> amount = this.register(new Setting<Float>("Amount", Float.valueOf(1f), Float.valueOf(0f), Float.valueOf(10f)));


    float lastValue;
    private Map domainResourceManagers;


    @Override
    public void onDisable() {
        mc.entityRenderer.stopUseShader();
    }

    @Override
    public void onTick() {
        try {
            float curValue = amount.getValue();

            if (!mc.entityRenderer.isShaderActive() && mc.world != null) {
                mc.entityRenderer.loadShader(new ResourceLocation("motionblur", "motionblur"));
            }

            if (domainResourceManagers == null) {
                domainResourceManagers =  ((SimpleReloadableResourceManager) mc.resourceManager).domainResourceManagers;
            }

            if (!domainResourceManagers.containsKey("motionblur")) {
                domainResourceManagers.put("motionblur", new MotionBlurResourceManager());
            }

            if (curValue != lastValue) {
                Command.sendMessage("Motion Blur перезапущен!");
                domainResourceManagers.remove("motionblur");
                domainResourceManagers.put("motionblur", new MotionBlurResourceManager());
                mc.entityRenderer.loadShader(new ResourceLocation("motionblur", "motionblur"));
            }

            lastValue = curValue;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
