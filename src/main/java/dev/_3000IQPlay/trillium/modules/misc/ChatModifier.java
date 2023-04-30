package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.ColorUtil;
import dev._3000IQPlay.trillium.util.RenderUtil;
import dev._3000IQPlay.trillium.util.TextUtil;
import dev._3000IQPlay.trillium.util.Timer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatModifier
        extends Module {
    private static ChatModifier INSTANCE = new ChatModifier();
    private final Timer timer = new Timer();
	public Setting<ColorSetting> cbgC = register(new Setting<ColorSetting>("ChatBGColor", new ColorSetting(-2038431489), v -> this.gradient.getValue() == false));
	public Setting<Boolean> gradient = register(new Setting<Boolean>("GradientBG", true));
	public final Setting<ColorSetting> gradientLB = this.register(new Setting<>("GLeftBotom", new ColorSetting(-8453889), v -> this.gradient.getValue()));
    public final Setting<ColorSetting> gradientLT = this.register(new Setting<>("GLeftTop", new ColorSetting(-16711681), v -> this.gradient.getValue()));
	public final Setting<ColorSetting> gradientRB = this.register(new Setting<>("GRightBottom", new ColorSetting(-16711808), v -> this.gradient.getValue()));
    public final Setting<ColorSetting> gradientRT = this.register(new Setting<>("GRightTop", new ColorSetting(-14024449), v -> this.gradient.getValue()));
	public Setting<Suffix> suffix = this.register(new Setting<Suffix>("Suffix", Suffix.None));
	public Setting<Boolean> ctc = register(new Setting<Boolean>("CustomTextColor", false));
	public Setting<TextColor> textcolor = this.register(new Setting<TextColor>("TextColorType", TextColor.GREEN, v -> this.ctc.getValue()));
	public Setting<Boolean> stc = register(new Setting<Boolean>("StiTextColor", false));
	public Setting<StiTextColor> stiTextColor = this.register(new Setting<StiTextColor>("StiTextColorType", StiTextColor.RANDOM, v -> this.stc.getValue()));
    public Setting<Boolean> clean = this.register(new Setting<Boolean>("CleanChat", false));
    public Setting<Boolean> infinite = this.register(new Setting<Boolean>("Infinite", false));

    public ChatModifier() {
        super("ChatModifier", "Modifies your chat", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static ChatModifier getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatModifier();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
	
	@Override
    public void onRender2D(Render2DEvent event) {
        if (ChatModifier.mc.currentScreen instanceof GuiChat) {
			ScaledResolution sr = new ScaledResolution(mc);
            if (this.gradient.getValue().booleanValue()) {
                RenderUtil.draw2DGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(),
			        new Color(this.gradientLB.getValue().getRed(), this.gradientLB.getValue().getGreen(), this.gradientLB.getValue().getBlue(), this.gradientLB.getValue().getAlpha() / 2).getRGB(),
			        new Color(this.gradientLT.getValue().getRed(), this.gradientLT.getValue().getGreen(), this.gradientLT.getValue().getBlue(), this.gradientLT.getValue().getAlpha() / 2).getRGB(),
			        new Color(this.gradientRB.getValue().getRed(), this.gradientRB.getValue().getGreen(), this.gradientRB.getValue().getBlue(), this.gradientRB.getValue().getAlpha() / 2).getRGB(),
			        new Color(this.gradientRT.getValue().getRed(), this.gradientRT.getValue().getGreen(), this.gradientRT.getValue().getBlue(), this.gradientRT.getValue().getAlpha() / 2).getRGB()
		        );
            } else {
				RenderUtil.drawRectangleCorrectly(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), ColorUtil.toRGBA(this.cbgC.getValue().getRed(), this.cbgC.getValue().getGreen(), this.cbgC.getValue().getBlue(), this.cbgC.getValue().getAlpha()));
            }    
		}
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage) {
            CPacketChatMessage packet = event.getPacket();
            String s = packet.getMessage();
            if (s.startsWith("/") || s.startsWith("!")) {
                return;
            }
            switch (this.suffix.getValue()) {
                case Trillium: {
                    s = s + " | Trillium";
                    break;
                }
				case ExperiumPlus: {
                    s = s + " | Experium+";
                    break;
                }
            }
			if (this.stc.getValue().booleanValue()) {
				switch (this.stiTextColor.getValue()) {
					case RANDOM: {
                	    int minNum = 1;
                	    int maxNum = 16;
                 	    int randomNumber = (int)Math.floor(Math.random()*(maxNum-minNum+1)+minNum);
                        if (randomNumber == 1) {
							s = "!2" + s;
					    }
     	                if (randomNumber == 2) {
							s = "!3" + s;
						}
                 	    if (randomNumber == 3) {
							s = "!4" + s;
					    }
                    	if (randomNumber == 4) {
							s = "!5" + s;
						}
                	    if (randomNumber == 5) {
							s = "!7" + s;
						}
               	        if (randomNumber == 6) {
							s = "!8" + s;
						}
                   	    if (randomNumber == 7) {
							s = "!9" + s;
						}
                    	if (randomNumber == 8) {
							s = "!g" + s;
						}
                    	if (randomNumber == 9) {
							s = "!c" + s;
						}
                    	if (randomNumber == 10) {
							s = "!d" + s;
						}
                    	if (randomNumber == 11) {
							s = "!a" + s;
						}
                    	if (randomNumber == 12) {
							s = "!e" + s;
						}
                   	    if (randomNumber == 13) {
							s = "!b" + s;
						}
                    	if (randomNumber == 14) {
							s = "!f" + s;
						}
                    	if (randomNumber == 15) {
							s = "!1" + s;
						}
                    	if (randomNumber == 16) {
							s = "!6" + s;
						}
                    	break;
					}	
                	case LIGHT_GREEN: {
                    	s = "!2" + s;
                    	break;
                	}
					case LIGHT_BLUE: {
                    	s = "!3" + s;
                    	break;
                	}
					case LIGHT_RED: {
                    	s = "!4" + s;
                	}
					case LIGHT_AQUA: {
                    	s = "!5" + s;
                    	break;
                	}
					case YELLOW: {
                    	s = "!7" + s;
						break;
                	}
					case LIGHT_GRAY: {
						s = "!8" + s;
						break;
                	}
					case LIGHT_PURPLE: {
						s = "!g" + s;
						break;
                	}
					case GRAY: {
						s = "!c" + s;
						break;
                	}
					case BLACK: {
						s = "!9" + s;
						break;
                	}
					case BLUE: {
						s = "!d" + s;
						break;
                	}
					case GREEN: {
						s = "!a" + s;
						break;
                	}
					case AQUA: {
						s = "!e" + s;
						break;
                	}
					case RED: {
						s = "!b" + s;
						break;
                	}
					case PURPLE: {
						s = "!f" + s;
						break;
                	}
					case GOLD: {
                    	s = "!6" + s;
						break;
                	}
					case WHITE: {
                    	s = "!1" + s;
                    	break;
                	}
					case ITALIC: {
						s = "!h" + s;
						break;
                	}
					case STRIKE: {
						s = "!k" + s;
						break;
                	}
					case UNDERLINE: {
						s = "!i" + s;
						break;
                	}
					case BOLD: {
						s = "!j" + s;
						break;
                	}
            	}
			}
			if (this.ctc.getValue().booleanValue()) {
			    switch (this.textcolor.getValue()) {
                    case GREEN: {
                        s = "> " + s;
                        break;
                    }
				    case YELLOW: {
                        s = "# " + s;
                        break;
                    }
				    case GOLD: {
                        s = "$ " + s;
                    }
				    case BLUE: {
                        s = "! " + s;
                        break;
                    }
				    case AQUA: {
                        s = "`` " + s;
					    break;
                    }
				    case PURPLE: {
                        s = "? " + s;
					    break;
                    }
				    case RED: { 
					    s = "& " + s;
				        break;
                    }
				    case DARKRED: {
					    s = "@ " + s;
					    break;
                    }
				    case GRAY: {
					    s = ". " + s;
					    break;
                    }
                }
			}
            if (s.length() >= 256) {
                s = s.substring(0, 256);
            }
            packet.message = s;
        }
    }

    @SubscribeEvent
    public void onChatPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() != 0 || event.getPacket() instanceof SPacketChat) {
            // empty if block
        }
    }

    private boolean shouldSendMessage(EntityPlayer player) {
        if (player.dimension != 1) {
            return false;
        }
        return player.getPosition().equals(new Vec3i(0, 240, 0));
    }

    public enum Suffix {
        None,
        Trillium,
		ExperiumPlus;
    }
	
	public enum TextColor {
        NONE,
        GREEN,
		YELLOW,
		GOLD,
		BLUE,
		AQUA,
		PURPLE,
		RED,
		DARKRED,
		GRAY;
    }
	
	public enum StiTextColor {
        NONE,
		RANDOM,
		GREEN,
        BLUE,
        AQUA,
		YELLOW,
        GOLD,
		RED,
		PURPLE,
        LIGHT_BLUE,
        LIGHT_GREEN,
        LIGHT_AQUA,
        LIGHT_RED,
        LIGHT_PURPLE,
		LIGHT_GRAY,
		GRAY,
		BLACK,
        WHITE,
        BOLD,
        STRIKE,
        UNDERLINE,
        ITALIC;
    }
}