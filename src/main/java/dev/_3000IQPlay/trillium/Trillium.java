package dev._3000IQPlay.trillium;

import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.*;
import dev._3000IQPlay.trillium.manager.*;
import dev._3000IQPlay.trillium.util.IconUtil;
import dev._3000IQPlay.trillium.util.ffp.NetworkHandler;
import dev._3000IQPlay.trillium.util.phobos.*;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;


@Mod(
        modid = "trillium",
        name = "Trillium",
        version = "1.0.0-Beta")


public class Trillium {
    public static final String MODID = "trillium";
	public static final String MODNAME = "Trillium";
    public static final String MODVER = "1.0.0-Beta";
    public static CommandManager commandManager;
    public static FriendManager friendManager;
	public static MovementManager movementManager;
    public static ModuleManager moduleManager;
    public static EnemyManager enemyManager;
    public static NetworkHandler networkHandler;
    public static MacroManager macromanager;
    public static CFontRenderer fontRenderer;
    public static CFontRenderer2 fontRenderer2;
    public static CFontRenderer3 fontRenderer3;
    public static CFontRenderer4 fontRenderer4;
    public static CFontRenderer5 fontRenderer5;
    public static CFontRenderer6 fontRenderer6;
    public static PotionManager potionManager;
    public static SpeedManager speedManager;
	public static PositionManager positionManager;
    public static ReloadManager reloadManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static ServerManager serverManager;
    public static EventManager eventManager;
    public static EntityProvider entityProvider;
    public static PacketManager packetManager;
    public static HoleManager holeManager;
    public static RotationManager rotationManager;
    public static SetDeadManager setDeadManager;
    public static ThreadManager threadManager;
    public static ServerTickManager servtickManager;
    public static SwitchManager switchManager;
    public static CombatManager combatManager;
    public static Scheduler yahz;
    public static NoMotionUpdateService nobitches;


    public static String ServerIp;
    public static int ServerPort;

    @Mod.Instance
    public static Trillium INSTANCE;
    private static boolean unloaded;
    static {
        unloaded = false;
    }
    public static void load() {
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }
        try {
            Font verdanapro = Font.createFont( Font.TRUETYPE_FONT, Objects.requireNonNull(Trillium.class.getResourceAsStream("/fonts/TrilliumFont2.ttf")));
            verdanapro = verdanapro.deriveFont( 24.f );
            fontRenderer = new CFontRenderer( verdanapro, true, true );

            Font verdanapro2 = Font.createFont( Font.TRUETYPE_FONT, Objects.requireNonNull(Trillium.class.getResourceAsStream("/fonts/TrilliumFont3.ttf")));
            verdanapro2 = verdanapro2.deriveFont( 36.f );
            fontRenderer2 = new CFontRenderer2( verdanapro2, true, true );

            Font verdanapro3 = Font.createFont( Font.TRUETYPE_FONT, Objects.requireNonNull(Trillium.class.getResourceAsStream("/fonts/TrilliumFont2.ttf")));
            verdanapro3 = verdanapro3.deriveFont( 18.f );
            fontRenderer3 = new CFontRenderer3( verdanapro3, true, true );

            Font verdanapro4 = Font.createFont( Font.TRUETYPE_FONT, Objects.requireNonNull(Trillium.class.getResourceAsStream("/fonts/TrilliumFont2.ttf")));
            verdanapro4 = verdanapro4.deriveFont( 50.f );
            fontRenderer4 = new CFontRenderer4( verdanapro4, true, true );

            Font verdanapro5 = Font.createFont( Font.TRUETYPE_FONT, Objects.requireNonNull(Trillium.class.getResourceAsStream("/fonts/Monsterrat.ttf")));
            verdanapro5 = verdanapro5.deriveFont( 12.f );
            fontRenderer5 = new CFontRenderer5( verdanapro5, true, true );

            Font verdanapro6 = Font.createFont( Font.TRUETYPE_FONT, Objects.requireNonNull(Trillium.class.getResourceAsStream("/fonts/Monsterrat.ttf")));
            verdanapro6 = verdanapro6.deriveFont( 14.f );
            fontRenderer6 = new CFontRenderer6( verdanapro6, true, true );
        } catch ( Exception e ) {
            e.printStackTrace( );
            return;
        }
        entityProvider = new EntityProvider();
		movementManager = new MovementManager();
        rotationManager = new RotationManager();
        threadManager = new ThreadManager();
        servtickManager = new ServerTickManager();
        switchManager = new SwitchManager();
		packetManager = new PacketManager();
        combatManager = new CombatManager();
    	positionManager = new PositionManager();
        yahz = new Scheduler();
		holeManager = new HoleManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        enemyManager = new EnemyManager();
        eventManager = new EventManager();
        macromanager = new MacroManager();
        networkHandler = new NetworkHandler();
        setDeadManager = new SetDeadManager();
        speedManager = new SpeedManager();
        potionManager = new PotionManager();
        serverManager = new ServerManager();
        fileManager = new FileManager();
        configManager = new ConfigManager();
        nobitches = new NoMotionUpdateService();
		
        moduleManager.init();
        configManager.init();
        eventManager.init();
        positionManager.init();
        rotationManager.init();
        servtickManager.init();
        switchManager.init();
        combatManager.init();
        yahz.init();
        setDeadManager.init();
        nobitches.init();
        entityProvider.init();
        moduleManager.onLoad();
    }

    public static void unload(boolean unload) {
        Display.setTitle("Minecraft 1.12.2");

        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        Trillium.onUnload();

        holeManager = null;
        eventManager = null;
        friendManager = null;
        speedManager = null;
		movementManager = null;
        fontRenderer = null;
        enemyManager = null;
        macromanager = null;
		positionManager = null;
        networkHandler = null;
        configManager = null;
        commandManager = null;
        serverManager = null;
        fileManager = null;
        potionManager = null;
    }

    public static void reload() {
        Trillium.unload(false);
        Trillium.load();
    }
	
	public static void setWindowIcon() {
        if (Util.getOSType() != Util.EnumOS.OSX) {
            try (InputStream inputStream16x = Minecraft.class.getResourceAsStream("/assets/minecraft/textures/icons/icon-16x.png");
                InputStream inputStream32x = Minecraft.class.getResourceAsStream("/assets/minecraft/textures/icons/icon-32x.png");
                InputStream inputStream64x = Minecraft.class.getResourceAsStream("/assets/minecraft/textures/icons/icon-64x.png"); 
				InputStream inputStream128x = Minecraft.class.getResourceAsStream("/assets/minecraft/textures/icons/icon-128x.png");
				InputStream inputStream256x = Minecraft.class.getResourceAsStream("/assets/minecraft/textures/icons/icon-256x.png")) {
                ByteBuffer[] icons = new ByteBuffer[]{IconUtil.INSTANCE.readImageToBuffer(inputStream16x), IconUtil.INSTANCE.readImageToBuffer(inputStream32x), IconUtil.INSTANCE.readImageToBuffer(inputStream64x), IconUtil.INSTANCE.readImageToBuffer(inputStream128x), IconUtil.INSTANCE.readImageToBuffer(inputStream256x)};
                Display.setIcon(icons);
            } catch (Exception e) {
                Trillium.LOGGER.error("Couldn't set Windows Icon", e);
            }
        }
    }

    private void setWindowsIcon() {
        Trillium.setWindowIcon();
    }


    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(Trillium.configManager.config.replaceFirst("Trillium/", ""));
            moduleManager.onUnloadPost();
            unloaded = true;
        }
    }

    private static final Logger LOGGER = LogManager.getLogger("trillium");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GlobalExecutor.EXECUTOR.submit(() -> Sphere.cacheSphere(LOGGER));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
        Display.setTitle(MODNAME + " v"+ MODVER + " || User: " + mc.getSession().getUsername());
        Trillium.load();
		setWindowsIcon();
        MinecraftForge.EVENT_BUS.register(networkHandler);
    }
}