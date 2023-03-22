package dev._3000IQPlay.trillium.util;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;


public class DamageUtil implements Util {

    public static int ChekTotalarmorDamage(EntityPlayer player) {
        Integer damage_vsey_broni = 0;
        for (ItemStack piece : player.inventory.armorInventory) {
            if (piece == null) {
                damage_vsey_broni = damage_vsey_broni + 0;
            } else {
                damage_vsey_broni = damage_vsey_broni + DamageUtil.getItemDamage(piece);
            }
        }
        return damage_vsey_broni;
    }

    public static int getItemDamage(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }



    public static float calculateDamage(double posX, double posY, double posZ, Entity entity, boolean ignoreTerrain) {
        float finalDamage = 1.0f;
        try {
            float doubleExplosionSize = 12.0F;
            double distancedSize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
            double blockDensity = ignoreTerrain ?
                    ignoreTerrainDecntiy(new Vec3d(posX, posY, posZ), entity.getEntityBoundingBox(), mc.world)
                    : entity.world.getBlockDensity(new Vec3d(posX, posY, posZ), entity.getEntityBoundingBox());
            double v = (1.0D - distancedSize) * blockDensity;
            float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));

            if (entity instanceof EntityLivingBase) {
                finalDamage = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
            }
        } catch (NullPointerException ignored) {
        }

        return finalDamage;
    }
	
	public static float calculateDamage(Vec3d pos, Entity entity) {
        return DamageUtil.calculateDamage(pos.x, pos.y, pos.z, entity);
    }
	
	public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double)doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        }
        catch (Exception exception) {
            // empty catch block
        }
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (int)((v * v + v) / 2.0 * 7.0 * (double)doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = DamageUtil.getBlastReduction((EntityLivingBase)entity, DamageUtil.getDamageMultiplied(damage), new Explosion(DamageUtil.mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float)finald;
    }
	
    public static float ignoreTerrainDecntiy(Vec3d vec, AxisAlignedBB bb, World world) {
        double d0 = 1.0D / ((bb.maxX - bb.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((bb.maxY - bb.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((bb.maxZ - bb.minZ) * 2.0D + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;

        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D)
        {
            int j2 = 0;
            int k2 = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float)((double)f + d0))
            {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float)((double)f1 + d1))
                {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float)((double)f2 + d2))
                    {
                        double d5 = bb.minX + (bb.maxX - bb.minX) * (double)f;
                        double d6 = bb.minY + (bb.maxY - bb.minY) * (double)f1;
                        double d7 = bb.minZ + (bb.maxZ - bb.minZ) * (double)f2;
                        RayTraceResult result;

                        if ( (result = world.rayTraceBlocks(new Vec3d(d5 + d3, d6, d7 + d4), vec)) == null)
                        {
                            ++j2;
                        } else {
                            Block blockHit = BlockUtils.getBlock(result.getBlockPos());
                            if (blockHit.blockResistance < 600)
                                ++j2;
                        }

                        ++k2;
                    }
                }
            }

            return (float)j2 / (float)k2;
        }
        else
        {
            return 0.0F;
        }

    }


    public static float getBlastReduction(final EntityLivingBase entity,  final float damageI,  final Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            final EntityPlayer ep = (EntityPlayer)entity;
            final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage,  (float)ep.getTotalArmorValue(),  (float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(),  ds);
            }
            catch (Exception ex) {}
            final float f = MathHelper.clamp((float)k,  0.0f,  20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage,  0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage,  (float)entity.getTotalArmorValue(),  (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    public static float getDamageMultiplied(final float damage) {
        final int diff = DamageUtil.mc.world.getDifficulty().getId();
        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }
	
	public static void damagePlayer(DamageType type, double plusYVal, boolean groundCheck, boolean hurtTimeCheck) {
        if ((!groundCheck || DamageUtil.mc.player.onGround) && (!hurtTimeCheck || DamageUtil.mc.player.hurtTime == 0)) {
            final double x = DamageUtil.mc.player.posX;
            final double y = DamageUtil.mc.player.posY;
            final double z = DamageUtil.mc.player.posZ;
            double fallDistanceReq = 3.1;
            if (DamageUtil.mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                final int amplifier = mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier();
                fallDistanceReq += (float) (amplifier + 1);
            }
            final int packetCount = (int) Math.ceil(fallDistanceReq / plusYVal); // Don't change this unless you know the change wont break the self damage.
            for (int i = 0; i < packetCount; i++) {
                switch (type) {
                    case POSITION_ROTATION: {
                        DamageUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(x, y + plusYVal, z, DamageUtil.mc.player.rotationYaw, DamageUtil.mc.player.rotationPitch, false));
                        DamageUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(x, y, z, DamageUtil.mc.player.rotationYaw, DamageUtil.mc.player.rotationPitch, false));
						DamageUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(x, y, z, DamageUtil.mc.player.rotationYaw, DamageUtil.mc.player.rotationPitch, true));
                        break;
                    }
                    case POSITION: {
                        DamageUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(x, y + plusYVal, z, false));
                        DamageUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(x, y, z, false));
						DamageUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(x, y, z, true));
                        break;
                    }
                }
            }
            DamageUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer(true));
        }
    }

    public static void damagePlayer(DamageType type, double plusYVal, int packets, boolean groundCheck, boolean hurtTimeCheck) {
        if ((!groundCheck || DamageUtil.mc.player.onGround) && (!hurtTimeCheck || DamageUtil.mc.player.hurtTime == 0)) {
            final double x = DamageUtil.mc.player.posX;
            final double y = DamageUtil.mc.player.posY;
            final double z = DamageUtil.mc.player.posZ;
            for (int i = 0; i < packets; i++) {
                switch (type) {
                    case POSITION_ROTATION: {
                        DamageUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(x, y + plusYVal, z, DamageUtil.mc.player.rotationYaw, DamageUtil.mc.player.rotationPitch, false));
                        DamageUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(x, y, z, DamageUtil.mc.player.rotationYaw, DamageUtil.mc.player.rotationPitch, false));
						DamageUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(x, y, z, DamageUtil.mc.player.rotationYaw, DamageUtil.mc.player.rotationPitch, true));
                        break;
                    }
                    case POSITION: {
                        DamageUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(x, y + plusYVal, z, false));
                        DamageUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(x, y, z, false));
						DamageUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(x, y, z, true));
                        break;
                    }
                }
            }
            DamageUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer(true));
        }
    }

    public enum DamageType {
        POSITION_ROTATION,
        POSITION
    }
}