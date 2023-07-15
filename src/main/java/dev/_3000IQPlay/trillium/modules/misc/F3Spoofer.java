package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.Timer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class F3Spoofer extends Module {
    public Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.Hide));
    public Setting<Coords> coords = this.register(new Setting<>("Coords", Coords.Normal));
    public Setting<Boolean> block = this.register(new Setting<>("Block", true, v -> coords.getValue() != Coords.None));
    public Setting<Boolean> chunk = this.register(new Setting<>("Chunk", true, v -> coords.getValue() != Coords.None));
    public Setting<Boolean> fps1 = this.register(new Setting<>("FPS", true));
    public Setting<Boolean> direction = this.register(new Setting<>("Direction", true));
    public Setting<Boolean> biome = this.register(new Setting<>("Biome", true));
   // public Setting<Boolean> system = this.register(new Setting<>("System", true));

    public F3Spoofer() {
        super("F3Spoofer", "Spoof f3 menu", Module.Category.MISC, true, false, false);
        spoofX = (int) Math.sqrt(ThreadLocalRandom.current().nextInt());
        spoofZ = (int) Math.sqrt(ThreadLocalRandom.current().nextInt());
        spoofMode = ThreadLocalRandom.current().nextInt(5);
        System.out.println(spoofMode);
    }

    private int spoofMode;
    private int spoofX;
    private int spoofZ;
    Random random = ThreadLocalRandom.current();
    Timer timerUtil = new Timer();
    private long randomX;
    private long randomY;
    private long randomZ;
    private int fps;
    private int height;
    private int width;
    private EnumFacing enumFacing;

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (mc.gameSettings.showDebugInfo) {
            for (int i = 0; i < event.getLeft().size(); i++) {
                if (coords.getValue() != Coords.None) {
                    if (event.getLeft().get(i).contains("Looking"))
                        event.getLeft().set(i, "look at porn");
                    if (event.getLeft().get(i).contains("XYZ")) {
                        if (coords.getValue() == Coords.Normal) {
                            if (mode.getValue() == Mode.Random) {
                                event.getLeft().set(i, "XYZ: " + randomX + " / " + randomY + " / " + randomZ);
                            } else {
                                event.getLeft().set(i, "XYZ: Looks like blud might be on 0, 0, 0 like fr idk bro");
                            }
                        } else {
                            switch (spoofMode) {
                                case 0: {
                                    event.getLeft().set(i, "XYZ: "
                                            + String.format("%.3f", mc.player.posX - spoofX * Math.PI) + " / "
                                            + String.format("%.3f", mc.player.posY) + " / "
                                            + String.format("%.3f", mc.player.posZ - spoofZ * Math.E));
                                    break;
                                }
                                case 1: {
                                    event.getLeft().set(i, "XYZ: "
                                            + String.format("%.3f", Math.abs(mc.player.posX + spoofX * Math.sqrt(spoofX))) + " / "
                                            + String.format("%.3f", mc.player.posY) + " / "
                                            + String.format("%.3f", Math.abs(mc.player.posZ + spoofZ * Math.sqrt(spoofZ))));
                                    break;
                                }
                                case 2: {
                                    event.getLeft().set(i, "XYZ: "
                                            + String.format("%.3f", mc.player.posX - (spoofX * spoofZ)) + " / "
                                            + String.format("%.3f", mc.player.posY) + " / "
                                            + String.format("%.3f", mc.player.posZ - (spoofX * spoofZ)));
                                    break;
                                }
                                case 3: {
                                    event.getLeft().set(i, "XYZ: "
                                            + String.format("%.3f", mc.player.posX + spoofX * spoofZ) + " / "
                                            + String.format("%.3f", mc.player.posY) + " / "
                                            + String.format("%.3f", mc.player.posZ + spoofX * Math.sqrt(spoofX)));
                                    break;
                                }
                                case 4: {
                                    event.getLeft().set(i, "XYZ: "
                                            + String.format("%.3f", mc.player.posZ - spoofX * Math.sin(spoofX) == 1 || Math.sin(spoofX) == -1 ? .34837467347 : Math.sin(spoofX)) + " / "
                                            + String.format("%.3f", mc.player.posY) + " / "
                                            + String.format("%.3f", mc.player.posX + spoofZ * Math.cos(spoofZ) == 1 || Math.cos(spoofZ) == -1 ? .32834872347 : Math.cos(spoofZ)));
                                    break;
                                }
                                case 5: {
                                    event.getLeft().set(i, "XYZ: "
                                            + String.format("%.3f", mc.player.posX + Math.sqrt(spoofX)) + " / "
                                            + String.format("%.3f", mc.player.posY) + " / "
                                            + String.format("%.3f", mc.player.posZ - Math.sqrt(spoofZ)));
                                    break;
                                }
                            }


                        }
                    }
                    if (event.getLeft().get(i).contains("Block:") && block.getValue()) {
                        if (coords.getValue() == Coords.Normal) {
                            event.getLeft().set(i, "Block: Nutela!");
                        } else {
                            switch (spoofMode) {
                                case 0: {
                                    event.getLeft().set(i, "Block: "
                                            + String.format("%.3f", mc.player.posX - spoofX * Math.PI) + " / "
                                            + String.format("%.3f", mc.player.posY) + " / "
                                            + String.format("%.3f", mc.player.posZ - spoofZ * Math.E));
                                    break;
                                }
                                case 1: {
                                    event.getLeft().set(i, "Block: "
                                            + String.format("%.3f", Math.abs(mc.player.posX + spoofX * Math.sqrt(spoofX))) + " / "
                                            + String.format("%.3f", mc.player.posY) + " / "
                                            + String.format("%.3f", Math.abs(mc.player.posZ + spoofZ * Math.sqrt(spoofZ))));
                                    break;
                                }
                                case 2: {
                                    event.getLeft().set(i, "Block: "
                                            + String.format("%.3f", mc.player.posX - (spoofX * spoofZ)) + " / "
                                            + String.format("%.3f", mc.player.posY) + " / "
                                            + String.format("%.3f", mc.player.posZ - (spoofX * spoofZ)));
                                    break;
                                }
                                case 3: {
                                    event.getLeft().set(i, "Block: "
                                            + String.format("%.3f", mc.player.posX + spoofX * spoofZ) + " / "
                                            + String.format("%.3f", mc.player.posY) + " / "
                                            + String.format("%.3f", mc.player.posZ + spoofX * Math.sqrt(spoofX)));
                                    break;
                                }
                                case 4: {
                                    event.getLeft().set(i, "Block: "
                                            + String.format("%.3f", mc.player.posZ - spoofX * Math.sin(spoofX) == 1 || Math.sin(spoofX) == -1 ? .34837467347 : Math.sin(spoofX)) + " / "
                                            + String.format("%.3f", mc.player.posY) + " / "
                                            + String.format("%.3f", mc.player.posX + spoofZ * Math.cos(spoofZ) == 1 || Math.cos(spoofZ) == -1 ? .32834872347 : Math.cos(spoofZ)));
                                    break;
                                }
                                case 5: {
                                    event.getLeft().set(i, "Block: "
                                            + String.format("%.3f", mc.player.posX + Math.sqrt(spoofX)) + " / "
                                            + String.format("%.3f", mc.player.posY) + " / "
                                            + String.format("%.3f", mc.player.posZ - Math.sqrt(spoofZ)));
                                    break;
                                }
                            }
                        }
                    }

                    if (event.getLeft().get(i).contains("Chunk:") && chunk.getValue())
                        event.getLeft().set(i, "Chunk: The current one!");
                }
                if (fps1.getValue())
                    if (event.getLeft().get(i).contains("fps")) {
                        if (mode.getValue() == Mode.Random) {
                            event.getLeft().set(i, "fps: " + fps);
                        } else {
                            event.getLeft().set(i, "fps: High");
                        }
                    }
                if (direction.getValue())
                    if (event.getLeft().get(i).contains("Facing:")) {
                        if (mode.getValue() == Mode.Random) {
                            event.getLeft().set(i, "Facing: " + enumFacing);
                        } else {
                            event.getLeft().set(i, "Facing: Your mom");
                        }
                    }

                if (biome.getValue())
                    if (event.getLeft().get(i).contains("Biome:"))
                        event.getLeft().set(i, "Biome: Abyss!");
//                if (system.getValue()) {
//                    if (event.getRight().get(i).contains("Display:")) {
//                        if (mode.getValue() == Mode.Random) {
//                            event.getRight().set(i, "Display: " + width + "x " + height);
//                        } else {
//                            event.getRight().set(i, "Display: square");
//                        }
//
//                        continue;
//                    }
//                    if (event.getRight().get(i).contains("CPU:")) {
//                        event.getRight().set(i, "CPU: 256x NVIDIA");
//                        continue;
//                    }
//                    if (event.getRight().get(i).contains("NVIDIA") || event.getRight().get(i).contains("AMD") || event.getRight().get(i).contains("PCIe")) {
//                        event.getRight().set(i, "Potato!");
//                    }
//                }
            }
        }

    }

    @Override
    public void onTick() {
        if (mc.gameSettings.showDebugInfo && mode.getValue() == Mode.Random && timerUtil.passedMs(1000)) {
            randomX = random.nextLong();
            randomY = random.nextLong();
            randomZ = random.nextLong();
            fps = random.nextInt();
            height = random.nextInt();
            width = random.nextInt();
            EnumFacing[] temp = EnumFacing.values();
            enumFacing = temp[random.nextInt(temp.length)];
        }
    }

    public enum Mode {
        Hide,
        Random;
    }

    public enum Coords {
        None,
        Spoof,
        Normal
    }
}