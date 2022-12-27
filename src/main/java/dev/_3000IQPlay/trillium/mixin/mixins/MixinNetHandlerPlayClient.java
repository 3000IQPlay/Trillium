package dev._3000IQPlay.trillium.mixin.mixins;

import com.google.common.collect.Maps;
import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.DeathEvent;
import dev._3000IQPlay.trillium.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketThreadUtil;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.network.*;
import net.minecraft.network.play.server.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.eventhandler.*;

import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.injection.*;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Mixin({ NetHandlerPlayClient.class })
public class MixinNetHandlerPlayClient
{
    @Inject(method = { "handleEntityMetadata" },  at = { @At("RETURN") },  cancellable = true)
    private void handleEntityMetadataHook(final SPacketEntityMetadata packetIn,  final CallbackInfo info) {
        final Entity entity;
        final EntityPlayer player;
        if (Util.mc.world != null && (entity = Util.mc.world.getEntityByID(packetIn.getEntityId())) instanceof EntityPlayer && (player = (EntityPlayer)entity).getHealth() <= 0.0f) {
            MinecraftForge.EVENT_BUS.post((Event)new DeathEvent(player));
        }
    }
}