package dev._3000IQPlay.trillium.modules.movement;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;

import java.util.Objects;

public class LevitationControl  extends Module {
    public LevitationControl () {
        super("LevitCtrl", "Jeez what the fuck", Category.MOVEMENT, true, false, false);
    }



    private final Setting<Integer> upAmplifier = this.register(new Setting<Integer>("UpAmplifier", 1, 1, 3));
    private final Setting<Integer> downAmplifier = this.register(new Setting<Integer>("DownAmplifier", 1, 1, 3));


    @Override
    public void onUpdate() {
        if (mc.player.isPotionActive(MobEffects.LEVITATION)) {

            int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(Objects.requireNonNull(Potion.getPotionById(25)))).getAmplifier();

            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.motionY = ((0.05D * (double)(amplifier + 1) - mc.player.motionY) * 0.2D) * upAmplifier.getValue(); // reverse the levitation effect if not holding space
            } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.motionY = -(((0.05D * (double)(amplifier + 1) - mc.player.motionY) * 0.2D) * downAmplifier.getValue());
            } else {
                mc.player.motionY = 0;
            }

        }

    }

}


