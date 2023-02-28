package dev._3000IQPlay.trillium.modules.render;

import dev._3000IQPlay.trillium.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AntiInvisible extends Module {

    public AntiInvisible() {
        super("AntiInvisible", "Renders inivisible ppl like they arent invis", Module.Category.RENDER, true, false, false);
    }

    @Override
	public void onUpdate() {
	    for (final Entity entity : AntiInvisible.mc.world.loadedEntityList) {
			if (entity.isInvisible() && entity instanceof EntityPlayer) {
				entity.setInvisible(false);
			}
		}
	}
}