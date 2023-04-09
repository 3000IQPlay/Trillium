package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.network.NetworkManager;

public class AntiDesync
        extends Module {
    public static AntiDesync INSTANCE = new AntiDesync();
    private final Setting<Boolean> syncItem = this.register(new Setting<>("SyncItem", false));
    private final Setting<Boolean> processPackets = this.register(new Setting<>("ProcessPackets", true));

    public AntiDesync() {
        super("AntiDesync", "Prevents you from desyncing", Module.Category.PLAYER, true, false, false);
		INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (AntiDesync.mc.player == null) return;
        if (AntiDesync.mc.world == null) {
            return;
        }
        if (this.syncItem.getValue()) {
            AntiDesync.mc.playerController.syncCurrentPlayItem();
        }
        if (!this.processPackets.getValue()) return;
        this.handleNetwork();
    }

    private void handleNetwork() {
        NetworkManager networkManager = AntiDesync.mc.playerController.connection.getNetworkManager();
        if (networkManager.isChannelOpen()) {
            networkManager.processReceivedPackets();
            return;
        }
        networkManager.handleDisconnection();
    }
}