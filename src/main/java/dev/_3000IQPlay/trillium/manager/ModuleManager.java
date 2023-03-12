package dev._3000IQPlay.trillium.manager;

import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.event.events.Render3DEvent;
import dev._3000IQPlay.trillium.gui.hud.*;
import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.modules.client.*;
import dev._3000IQPlay.trillium.modules.client.Particles;
import dev._3000IQPlay.trillium.modules.combat.*;
import dev._3000IQPlay.trillium.modules.exploit.*;
import dev._3000IQPlay.trillium.modules.movement.Speed;
import dev._3000IQPlay.trillium.modules.misc.*;
import dev._3000IQPlay.trillium.modules.movement.*;
import dev._3000IQPlay.trillium.modules.movement.NoSlowDown;
import dev._3000IQPlay.trillium.modules.player.*;
import dev._3000IQPlay.trillium.modules.render.*;
import dev._3000IQPlay.trillium.notification.NotificationManager;
import dev._3000IQPlay.trillium.modules.Feature;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.util.PlayerUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager
        extends Feature {
    public ArrayList<Module> modules = new ArrayList();
    public List<Module> sortedModules = new ArrayList<Module>();

    public void init() {
        this.modules.add(new ClickGui());
        this.modules.add(new AimAssist());
        this.modules.add(new AntiBadEffects());
        this.modules.add(new Optimization());
        this.modules.add(new AutoCrystal());
        this.modules.add(new AutoMine());
        this.modules.add(new PvPResources());
        this.modules.add(new ElytraFly2b2tNew());
        this.modules.add(new CivBreaker());
        this.modules.add(new FastSwim());
        this.modules.add(new AntiTPhere());
        this.modules.add(new PearlBait());
        this.modules.add(new AutoSheep());
        this.modules.add(new Aura());
        this.modules.add(new NoSlowDown());
        this.modules.add(new BlockHighlight());
        this.modules.add(new StorageEsp());
        this.modules.add(new Ambience());
        this.modules.add(new Ghost());
        this.modules.add(new BowAim());
        this.modules.add(new SolidBlock());
        this.modules.add(new Sprint());
        this.modules.add(new TargetHud());
        this.modules.add(new FreeLook());
        this.modules.add(new Quiver());
        this.modules.add(new NoFall());
        this.modules.add(new AutoReconnect());
        this.modules.add(new LevitationControl());
        this.modules.add(new CustomEnchants());
        this.modules.add(new HoleESP());
        this.modules.add(new Trajectories());
        this.modules.add(new FakePlayer());
        this.modules.add(new TpsSync());
        this.modules.add(new ItemShaders());
        this.modules.add(new StashFinder());
        this.modules.add(new PearlESP());
        this.modules.add(new EntityESP());
        this.modules.add(new AutoFish());
        this.modules.add(new CrystalChams());
		this.modules.add(new MountBypass());
        this.modules.add(new PearlBypass());
        this.modules.add(new FreeCam());
        this.modules.add(new PacketFly());
        this.modules.add(new AutoTrap());
        this.modules.add(new NoEntityTrace());
        this.modules.add(new PistonAura());
        this.modules.add(new Models());
        this.modules.add(new Jesus());
        this.modules.add(new EChestFarmer());
        this.modules.add(new SeedOverlay());
        this.modules.add(new MiddleClick());
        this.modules.add(new Anchor());
        this.modules.add(new NotificationManager());
        this.modules.add(new Speedmine());
        this.modules.add(new AntiVoid());
        this.modules.add(new FakeVanilla());
        this.modules.add(new AutoRegear());
        this.modules.add(new Particles());
        this.modules.add(new ElytraFlight());
        this.modules.add(new RusherScaffold());
        this.modules.add(new FpsCounter());
        this.modules.add(new Blink());
        this.modules.add(new MainSettings());
        this.modules.add(new TPSCounter());
        this.modules.add(new WaterMark());
        this.modules.add(new Player());
        this.modules.add(new dev._3000IQPlay.trillium.gui.hud.SpeedMeter());
        this.modules.add(new ArmorHud());
        this.modules.add(new Surround());
        this.modules.add(new LogoutSpots());
        this.modules.add(new RPC());
        this.modules.add(new ViewModel());
        this.modules.add(new NoRender());
        this.modules.add(new VoidESP());
        this.modules.add(new TunnelESP());
        this.modules.add(new Criticals());
        this.modules.add(new Shaders());
        this.modules.add(new Indicators());
        this.modules.add(new ChestStealer());
        this.modules.add(new FastPlace2());
        this.modules.add(new AutoArmor());
        this.modules.add(new PacketCounter());
        this.modules.add(new OffHand());
        this.modules.add(new Speed());
        this.modules.add(new Burrow());
        this.modules.add(new AntiHunger());
        this.modules.add(new FullBright());
        this.modules.add(new Velocity());
        this.modules.add(new NameTags());
        this.modules.add(new AutoTPaccept());
		this.modules.add(new AntiDisconnect());
        this.modules.add(new MultiConnect());
        this.modules.add(new RadarRewrite());
        this.modules.add(new dev._3000IQPlay.trillium.gui.hud.ArrayList());
        this.modules.add(new Coords());
        this.modules.add(new KillEffect());
        this.modules.add(new BowKiller());
        this.modules.add(new BowSpam());
        this.modules.add(new ItemESP());
        this.modules.add(new AutoRespawn());
        this.modules.add(new TrueDurability());
        this.modules.add(new Potions());
        this.modules.add(new CevBreaker());
        this.modules.add(new AntiSpam());
        this.modules.add(new CoolCrosshair());
        this.modules.add(new ToolTips());
        this.modules.add(new Macros());
        this.modules.add(new HudEditor());
        this.modules.add(new Animations());
        this.modules.add(new AutoEZ());
        this.modules.add(new JumpCircle());
        this.modules.add(new KeyBinds());
		this.modules.add(new EntityDesync());
        this.modules.add(new AutoCraftDupe());
        this.modules.add(new ChorusLag());
        this.modules.add(new FarThrow());
        this.modules.add(new AutoFrameDupe());
        this.modules.add(new CowDupeExploit());
        this.modules.add(new ChorusPostpone());
        this.modules.add(new CornerClip());
        this.modules.add(new FastEat());
        this.modules.add(new Step());
		this.modules.add(new AntiUnicode());
		this.modules.add(new HoleSnap());
		this.modules.add(new FastFall());
		this.modules.add(new UnicodeLag());
        this.modules.add(new Strafe());
        this.modules.add(new Flight());
        this.modules.add(new AntiContainer());
		this.modules.add(new ChatModifier());
		this.modules.add(new Replenish());
		this.modules.add(new AntiPlantStomp());
		this.modules.add(new AutoTrader());
		this.modules.add(new AutoChunkDupe());
		this.modules.add(new PacketCanceller());
		this.modules.add(new PasswordHider());
		this.modules.add(new Aspect());
		this.modules.add(new KeyPearl());
		this.modules.add(new CrystalOptimizer());
		this.modules.add(new DamageParticles());
		this.modules.add(new SkyColor());
		this.modules.add(new EntityControl());
		this.modules.add(new AntiAFK());
		this.modules.add(new AntiInvisible());
		this.modules.add(new BackTrack());
		this.modules.add(new EntitySpeed());
		this.modules.add(new BoatFly());
		this.modules.add(new FastSleep());
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.modules) {
            if (!clazz.isInstance(module)) continue;
            return (T) module;
        }
        return null;
    }

    public Module getModuleByDisplayName(String displayName) {
        for (Module module : this.modules) {
            if (!module.getDisplayName().equalsIgnoreCase(displayName)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<Module>();
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<Module>();
        this.modules.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        this.modules.sort(Comparator.comparing(Module::getName));
        this.modules.stream().filter(Module::listening).forEach(((EventBus) MinecraftForge.EVENT_BUS)::register);
        this.modules.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
        this.modules.forEach(module -> {
            if (!PlayerUtils.isKeyDown(module.getBind().getKey()) && module.isEnabled() && module.getBind().isHold()) {
                module.disable();
            }
        });
    }

    public void onRender2D(Render2DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> FontRender.getStringWidth6(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }


    public void onLogout() {
        this.modules.forEach(Module::onLogout);
    }

    public void onLogin() {
        this.modules.forEach(Module::onLogin);
    }

    public void onUnload() {
        this.modules.forEach(MinecraftForge.EVENT_BUS::unregister);
        this.modules.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        for (Module module : this.modules) {
            module.enabled.setValue(false);
        }
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState()) {
            return;
        }
        this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }
}
