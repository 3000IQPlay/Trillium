package dev._3000IQPlay.trillium.modules.client;

import dev._3000IQPlay.trillium.Discord;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

public class RPC
        extends Module {
    public static RPC INSTANCE;
    public Setting < Boolean > showIP = this.register ( new Setting <> ( "ShowIP" , true  ) );
    public Setting < Boolean > queue = this.register ( new Setting <> ( "Queue" , true  ) );
    public Setting < String > state = this.register ( new Setting <> ( "State" , "Trillium" ));
    public Setting < Boolean > nickname = this.register ( new Setting <> ( "Nickname" , true  ) );

    public static boolean inQ = false;
    public static String position = "";

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(fullNullCheck())return;
        if(e.getPacket() instanceof SPacketChat){
            SPacketChat packchat = e.getPacket();
            String wtf = packchat.getChatComponent().getUnformattedText();
            position= StringUtils.substringBetween(wtf, "Position in queue: ", "\nYou can purchase");
            if(wtf.contains("Position in queue")){
                inQ = true;
            }
        }
        if( mc.player.posY < 63f || mc.player.posY > 64f ){
            inQ = false;
        }
    }

    @Override
    public void onLogout(){
        inQ = false;
        position = "";
    }

    public RPC( ) {
        super ( "DiscordRPC" , "Cool RPC" , Category.CLIENT , true , false , false );
        INSTANCE = this;
    }

    @Override
    public void onEnable ( ) {
        Discord.start ( );
    }


    @Override
    public void onUpdate(){
        if(!Discord.started) {
            Discord.start();
        }
    } //§7§6Position in queue: §r§6§l45§r


    @Override
    public
    void onDisable ( ) {
        Discord.stop ( );
    }
}