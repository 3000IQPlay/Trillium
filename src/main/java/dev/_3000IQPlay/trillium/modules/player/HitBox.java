package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class HitBox extends Module {
	public Setting<Float> heights = this.register(new Setting<>("Height", 2.0f, 2.0f, 5.0f));
    public Setting<Float> widths = this.register(new Setting<>("Width", 1.0f, 1.0f, 5.0f));
	
    public HitBox() {
        super("HitBox", "Increases Hitbox size", Module.Category.PLAYER, true, false, false);
    }
    
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        for (final EntityPlayer player : HitBox.getPlayersList()) {
            if (!this.check((EntityLivingBase)player)) {
                continue;
            }
            if (player == null) {
                continue;
            }
            final float width = this.widths.getValue();
            final float height = this.heights.getValue();
            HitBox.setEntityBoundingBoxSize((Entity)player, width, height);
        }
    }
    
    @Override
    public void onDisable() {
        for (final EntityPlayer player : HitBox.getPlayersList()) {
            HitBox.setEntityBoundingBoxSize((Entity)player);
        }
        super.onDisable();
    }
	
	public static void setEntityBoundingBoxSize(final Entity entity, final float width, final float height) {
        final EntitySize size = getEntitySize(entity);
        entity.width = size.width;
        entity.height = size.height;
        final double d0 = width / 2.0;
        entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, entity.posY, entity.posZ - d0, entity.posX + d0, entity.posY + height, entity.posZ + d0));
    }
	
	public static void setEntityBoundingBoxSize(final Entity entity) {
        final EntitySize size = getEntitySize(entity);
        entity.width = size.width;
        entity.height = size.height;
        final double d0 = entity.width / 2.0;
        entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, entity.posY, entity.posZ - d0, entity.posX + d0, entity.posY + entity.height, entity.posZ + d0));
    }
	
	public static EntitySize getEntitySize(final Entity entity) {
        final EntitySize entitySize = new EntitySize(0.6f, 1.8f);
        return entitySize;
    }
	
	public static List<EntityPlayer> getPlayersList() {
        return (List<EntityPlayer>)mc.world.playerEntities;
    }
    
    public boolean check(final EntityLivingBase entity) {
        return !(entity instanceof EntityPlayerSP) && entity != mc.player && !entity.isDead;
    }
	
	static class EntitySize {
        public float height;
        public float width;

        public EntitySize(float var1, float var2) {
            this.width = var1;
            this.height = var2;
       }
    }
}