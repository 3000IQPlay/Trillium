package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.event.events.Render3DEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.notification.NotificationManager;
import dev._3000IQPlay.trillium.notification.NotificationType;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.RenderUtil;
import dev._3000IQPlay.trillium.util.Timer;
import dev._3000IQPlay.trillium.util.seedoverlay.WorldLoader;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SeedOverlay
        extends Module{
    public Setting <Integer> BlockLimit = this.register ( new Setting <> ( "Chance", 200, 0, 5000 ) );
    public Setting <Integer> Distance = this.register ( new Setting <> ( "Distance", 6, 0, 15 ) );
    public Setting<Boolean> GrassSpread = register(new Setting("GrassSpread", false));
    public Setting<Boolean> FalsePositive = register(new Setting("FalsePositive", false));
    public Setting<Boolean> LavaMix = register(new Setting("LavaMix", false));
    public Setting<Boolean> Bush = register(new Setting("Bush", false));
    public Setting<Boolean> Tree = register(new Setting("Tree", false));
    public Setting<Boolean> Liquid = register(new Setting("Liquid", false));
    public Setting<Boolean> Fallingblock = register(new Setting("Fallingblock", false));
    public Setting<String> sd = this.register(new Setting<String>("seed", "-4172144997902289642"));
    private static ExecutorService executor;
    private static ExecutorService executor2;
    public int currentdis = 0;
    private ArrayList<ChunkData> chunks = new ArrayList<>();
    private final ArrayList<int[]> tobesearch = new ArrayList<>();
    private final Timer timer = new Timer();
	
	public SeedOverlay() {
        super("SeedOverlay", "Renders a fake world to look for inconsistencies", Module.Category.MISC, false, false, false);
    }

    @Override
    public void onUpdate() {

        if (timer.passedMs(500)) {
            if (mc.player.dimension != currentdis) {
                Command.sendMessage("Toggles the module");
                this.toggle();
            }
            searchViewDistance();
            runviewdistance();
            timer.reset();
        }
        int[] remove = null;
        try {
            for (int[] vec2d : tobesearch) {
                remove = vec2d;
                executor.execute(() -> WorldLoader.CreateChunk(vec2d[0], vec2d[1], mc.player.dimension));
            }
        } catch (Exception e){}

        tobesearch.remove(remove);
    }


    @Override
    public void onEnable() {
        WorldLoader.seed = Long.parseLong(sd.getValue());
        try {
            NotificationManager.publicity("SeedOverlay", "Set to " + WorldLoader.seed, 3, NotificationType.INFO);
        } catch (Exception e){}
        //  executor = Executors.newSingleThreadExecutor();
       // executor2 = Executors.newSingleThreadExecutor();
        if (mc.isSingleplayer()) {
            Command.sendMessage("You need to be in multiplayer dumbo");
            this.toggle();
        }
        if (WorldLoader.seed == 44776655) {
            Command.sendMessage("I hope u are just jokin around");
            this.toggle();
            return;
        }
        currentdis = mc.player.dimension;
        executor = Executors.newSingleThreadExecutor();
        executor2 = Executors.newSingleThreadExecutor();
        WorldLoader.setup();
        //Command.sendMessage("Still Working on this.");
        chunks = new ArrayList<>();
        searchViewDistance();
    }

    private void searchViewDistance() {
        executor.execute(() -> {
            for (int x = mc.player.chunkCoordX - (int) Distance.getValue(); x <= mc.player.chunkCoordX + (int) Distance.getValue(); x++) {
                for (int z = mc.player.chunkCoordZ - (int) Distance.getValue(); z <= mc.player.chunkCoordZ + (int) Distance.getValue(); z++) {
                    if (havenotsearched(x, z))
                        if (mc.world.isChunkGeneratedAt(x, z)) {
                            boolean found = false;
                            for (int[] vec2d : tobesearch) {
                                if (vec2d[0] == x && vec2d[1] == z) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found)
                                tobesearch.add(new int[]{x, z});
                        }
                }
            }
        });
    }

    private void runviewdistance() {
        for (int x = mc.player.chunkCoordX - (int) Distance.getValue(); x <= mc.player.chunkCoordX + (int) Distance.getValue(); x++) {
            for (int z = mc.player.chunkCoordZ - (int) Distance.getValue(); z <= mc.player.chunkCoordZ + (int) Distance.getValue(); z++) {
                if (mc.world.isChunkGeneratedAt(x, z)) {
                    if (WorldLoader.fakeworld.isChunkGeneratedAt(x, z) && WorldLoader.fakeworld.isChunkGeneratedAt(x + 1, z) && WorldLoader.fakeworld.isChunkGeneratedAt(x, z + 1)
                            && WorldLoader.fakeworld.isChunkGeneratedAt(x + 1, z + 1)) {
                        if (havenotsearched(x, z)) {
                            ChunkData data = new ChunkData(new ChunkPos(x, z), false);
                            searchChunk(mc.world.getChunk(x, z), data);
                            chunks.add(data);
                        }
                    }
                }
            }
        }
    }

    private boolean havenotsearched(int x, int z) {
        for (ChunkData chunk : chunks) {
            if (chunk.chunkPos.x == x && chunk.chunkPos.z == z) {
                return false;
            }
        }
        return true;
    }


    private void searchChunk(Chunk chunk, ChunkData data) {
        executor2.execute(() -> {
            try {
                for (int x = chunk.getPos().getXStart(); x <= chunk.getPos().getXEnd(); x++) {
                    for (int z = chunk.getPos().getZStart(); z <= chunk.getPos().getZEnd(); z++) {
                        for (int y = 0; y < 255; y++) {
                            if (BlockFast(new BlockPos(x, y, z), WorldLoader.fakeworld.getBlockState(new BlockPos(x, y, z)).getBlock(), chunk.getBlockState(x, y, z).getBlock()))
                                data.blocks.add(new BlockPos(x, y, z));
                        }
                    }
                }
                data.Searched = true;
            } catch (Exception ignored) {
            }
        });
    }


    private boolean BlockFast(BlockPos blockPos, Block FakeChunk, Block RealChunk) {
        if (RealChunk instanceof BlockSnow)
            return false;
        if (FakeChunk instanceof BlockSnow)
            return false;
        if (RealChunk instanceof BlockVine)
            return false;
        if (FakeChunk instanceof BlockVine)
            return false;
        if (!Fallingblock.getValue()) {
            if (RealChunk instanceof BlockFalling)
                return false;
            if (FakeChunk instanceof BlockFalling)
                return false;
        }
        if (!Liquid.getValue()) {
            if (RealChunk instanceof BlockLiquid)
                return false;
            if (FakeChunk instanceof BlockLiquid)
                return false;
            if (mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockLiquid)
                return false;
            if (mc.world.getBlockState(blockPos.down(2)).getBlock() instanceof BlockLiquid)
                return false;
        }
        if (!Tree.getValue()) {
            if (FakeChunk instanceof BlockGrass)
                if (Treeroots(blockPos))
                    return false;
            if (RealChunk instanceof BlockLog || RealChunk instanceof BlockLeaves)
                return false;
            if (FakeChunk instanceof BlockLog || FakeChunk instanceof BlockLeaves)
                return false;
        }
        if (!GrassSpread.getValue()) {
            if (RealChunk instanceof BlockGrass && FakeChunk instanceof BlockDirt)
                return false;
            if (RealChunk instanceof BlockDirt && FakeChunk instanceof BlockGrass)
                return false;
        }
        if (!Bush.getValue()) {
            if (RealChunk instanceof BlockBush)
                return false;
            if (RealChunk instanceof BlockReed)
                return false;
            if (FakeChunk instanceof BlockBush)
                return false;
        }
        if (!LavaMix.getValue()) {
            if (RealChunk instanceof BlockObsidian || RealChunk.equals(Blocks.COBBLESTONE))
                if (Lavamix(blockPos))
                    return false;
        }
        if (!FalsePositive.getValue()) {
            if (FakeChunk instanceof BlockOre && (RealChunk instanceof BlockStone || RealChunk instanceof BlockMagma || RealChunk instanceof BlockNetherrack || RealChunk instanceof BlockDirt))
                return false;
            if (RealChunk instanceof BlockOre && (FakeChunk instanceof BlockStone || FakeChunk instanceof BlockMagma || FakeChunk instanceof BlockNetherrack || FakeChunk instanceof BlockDirt))
                return false;

            // Redstone ore is not in ore list???????
            if (FakeChunk instanceof BlockRedstoneOre && (RealChunk instanceof BlockStone || RealChunk instanceof BlockDirt))
                return false;
            if (RealChunk instanceof BlockRedstoneOre && (FakeChunk instanceof BlockStone || FakeChunk instanceof BlockDirt))
                return false;

            if (FakeChunk instanceof BlockGlowstone && (RealChunk instanceof BlockAir))
                return false;
            if (RealChunk instanceof BlockGlowstone && (FakeChunk instanceof BlockAir))
                return false;

            if (FakeChunk instanceof BlockMagma && RealChunk instanceof BlockNetherrack)
                return false;
            if (RealChunk instanceof BlockMagma && FakeChunk instanceof BlockNetherrack)
                return false;
            if (RealChunk instanceof BlockFire || FakeChunk instanceof BlockFire)
                return false;
            if (RealChunk instanceof BlockOre && FakeChunk instanceof BlockOre)
                return false;
            if (RealChunk.getLocalizedName().equals(Blocks.MONSTER_EGG.getLocalizedName()) && FakeChunk instanceof BlockStone)
                return false;
            if ((FakeChunk instanceof BlockStone && RealChunk instanceof BlockDirt) || (FakeChunk instanceof BlockDirt && RealChunk instanceof BlockStone))
                return false;
            if (!(FakeChunk instanceof BlockAir) && RealChunk instanceof BlockAir)
                if (!mc.world.getBlockState(blockPos).getBlock().getLocalizedName().equals(RealChunk.getLocalizedName()))
                    return false;
        }

        return !FakeChunk.getLocalizedName().equals(RealChunk.getLocalizedName());
    }

    public boolean Treeroots(BlockPos b) {
        return mc.world.getBlockState(b.up()).getBlock() instanceof BlockLog;
    }

    public boolean Lavamix(BlockPos b) {
        if (mc.world.getBlockState(b.up()).getBlock() instanceof BlockLiquid) {
            return true;
        }
        if (mc.world.getBlockState(b.down()).getBlock() instanceof BlockLiquid) {
            return true;
        }
        if (mc.world.getBlockState(b.add(1, 0, 0)).getBlock() instanceof BlockLiquid) {
            return true;
        }
        if (mc.world.getBlockState(b.add(0, 0, 1)).getBlock() instanceof BlockLiquid) {
            return true;
        }
        if (mc.world.getBlockState(b.add(-1, 0, 0)).getBlock() instanceof BlockLiquid) {
            return true;
        }
        return mc.world.getBlockState(b.add(0, 0, -1)).getBlock() instanceof BlockLiquid;
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        try {
            int blocklimit = 0;
            ArrayList<ChunkData> Remove = new ArrayList<>();
            for (ChunkData chunk : chunks) {
                if (chunk.Searched) {
                    if (mc.player.getDistance(chunk.chunkPos.getXEnd(), 100, chunk.chunkPos.getZEnd()) > 2000)
                        Remove.add(chunk);
                    for (BlockPos block : chunk.blocks) {
                        if (blocklimit > BlockLimit.getValue())
                            break;
                       // RenderBlock(Mode.getValString(), Standardbb(new BlockPos(block.x, block.y, block.z)), OverlayColor.getcolor(), LineWidth.getValue());

                        RenderUtil.blockEsp(new BlockPos(block.x, block.y, block.z), new Color(8, 253, 0, 255), 1.0, 1.0);

                        blocklimit++;
                    }

                }
            }
            chunks.removeAll(Remove);
        } catch (Exception ignored) {
        }
      //  super.onRenderWorldLast(event);
    }


    public static class ChunkData {
        private boolean Searched;

        public List<BlockPos> getBlocks() {
            return blocks;
        }

        public final List<BlockPos> blocks = new ArrayList<>();
        private final ChunkPos chunkPos;

        public ChunkData(ChunkPos chunkPos, boolean Searched) {
            this.chunkPos = chunkPos;
            this.Searched = Searched;
        }
    }
}
