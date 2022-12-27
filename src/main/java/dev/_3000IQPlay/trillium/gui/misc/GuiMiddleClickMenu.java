package dev._3000IQPlay.trillium.gui.misc;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.gui.font.FontRendererWrapper;
import dev._3000IQPlay.trillium.modules.misc.MiddleClick;
import dev._3000IQPlay.trillium.util.GuiRenderHelper;
import dev._3000IQPlay.trillium.util.RectHelper;
import dev._3000IQPlay.trillium.util.Timer;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GuiMiddleClickMenu extends GuiScreen {
    private final EntityPlayer player;
    private final Timer timer = new Timer();

    public double posX;
    public double posY;




    public GuiMiddleClickMenu(EntityPlayer player) {
        this.player = player;
        timer.reset();
    }
    private Vector3d project2D(Float scaleFactor, double x, double y, double z) {
        float xPos = (float) x;
        float yPos = (float) y;
        float zPos = (float) z;
        IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
        FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        if (GLU.gluProject(xPos, yPos, zPos, modelview, projection, viewport, vector))
            return new Vector3d((vector.get(0) / scaleFactor), ((Display.getHeight() - vector.get(1)) / scaleFactor), vector.get(2));
        return null;
    }


   // @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Float scaleFactor = MiddleClick.getInstance().scalefactor.getValue();
        double scaling = scaleFactor / Math.pow(scaleFactor, 2);
        GlStateManager.scale(scaling, scaling, scaling);
        Color c = new Color(255, 255, 255);
        int color = 0;
        color = c.getRGB();
        float scale = 1;
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        mc.entityRenderer.setupOverlayRendering();

        super.drawScreen(mouseX, mouseY, partialTicks);

        boolean friended = Trillium.friendManager.isFriend(player.getName());
      //  boolean partied = PartyCommand.party.contains(player.getName());

        boolean practice = false;

        if (mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null) {
            if (mc.getCurrentServerData().serverIP.contains("pvp")) {
                practice = true;
            }
        }

        float width = Math.max(FontRendererWrapper.getStringWidth(friended ? "Unfriend" : "Friend"), FontRendererWrapper.getStringWidth(MiddleClick.getInstance().commandtext.getValue()));

        float height = 8F + FontRendererWrapper.getStringHeight(friended ? "Unfriend" : "Friend") + FontRendererWrapper.getStringHeight(MiddleClick.getInstance().commandtext.getValue());

        if (practice) {
            height += 4F + FontRendererWrapper.getStringHeight("Duel");
        }






        GuiRenderHelper.drawRect((float) posX - width/2F - 2F, (float) posY - 2F, width + 4F, height, 0x80000000);
        GuiRenderHelper.drawOutlineRect((float) posX - width/2F - 2F, (float) posY - 2F, width + 4F, height,1F, 0xD0000000);

        FontRendererWrapper.drawString(friended ? "Unfriend" : "Friend", (float) posX - FontRendererWrapper.getStringWidth(friended ? "Unfriend" : "Friend") / 2F, (float) posY, -1);
        FontRendererWrapper.drawString(MiddleClick.getInstance().commandtext.getValue(), (float) posX - FontRendererWrapper.getStringWidth(MiddleClick.getInstance().commandtext.getValue()) / 2F, (float) posY + 4F + FontRendererWrapper.getStringHeight(friended ? "Unfriend" : "Friend"), -1);
        if (practice) {
            FontRendererWrapper.drawString("Duel", (float) posX - FontRendererWrapper.getStringWidth("Duel") / 2F, (float) posY + 4F + FontRendererWrapper.getStringHeight(friended ? "Unfriend" : "Friend") + 4F + FontRendererWrapper.getStringHeight(MiddleClick.getInstance().commandtext.getValue()), -1);
        }

        if (timer.passedMs(5000)) {
            mc.displayGuiScreen(null);
        }
    }




   // @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);


        // System.out.println(screenPos.x +" " +screenPos.y);

        boolean friended = Trillium.friendManager.isFriend(player.getName());
        //  boolean partied = PartyCommand.party.contains(player.getName());

        boolean practice = false;

        if (mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null) {
            if (mc.getCurrentServerData().serverIP.contains("pvp")) {
                practice = true;
            }
        }

        if (mouseButton == 0) {
            if (mouseWithinBounds(mouseX, mouseY, (float) posX - FontRendererWrapper.getStringWidth(friended ? "Unfriend" : "Friend") / 2F, (float) posY, FontRendererWrapper.getStringWidth(friended ? "Unfriend" : "Friend"), FontRendererWrapper.getStringHeight(friended ? "Unfriend" : "Friend"))) {
                if (friended) {
                    Trillium.friendManager.removeFriend(player.getName());
                } else {
                    Trillium.friendManager.addFriend(player.getName());
                }
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                mc.displayGuiScreen(null);
            } else if (mouseWithinBounds(mouseX, mouseY, (float) posX - FontRendererWrapper.getStringWidth(MiddleClick.getInstance().commandtext.getValue()) / 2F, (float) posY + 4F + FontRendererWrapper.getStringHeight(friended ? "Unfriend" : "Friend"), FontRendererWrapper.getStringWidth(MiddleClick.getInstance().commandtext.getValue()), FontRendererWrapper.getStringHeight( MiddleClick.getInstance().commandtext.getValue()))) {
              //  if (partied) {
               //     PartyCommand.party.remove(player.getName());
               // } else {
               //     PartyCommand.party.add(player.getName());
               // }


                mc.player.sendChatMessage(MiddleClick.getInstance().commandname.getValue() + " " + player.getName() );

                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                mc.displayGuiScreen(null);
            } else if (practice && mouseWithinBounds(mouseX, mouseY, (float) posX - FontRendererWrapper.getStringWidth( MiddleClick.getInstance().commandtext.getValue()) / 2F, (float) posY + 4F + FontRendererWrapper.getStringHeight(friended ? "Unfriend" : "Friend") + 4F + FontRendererWrapper.getStringHeight( MiddleClick.getInstance().commandtext.getValue()), FontRendererWrapper.getStringWidth("Duel"), FontRendererWrapper.getStringHeight("Duel"))) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                mc.displayGuiScreen(null);
                mc.player.connection.sendPacket(new CPacketChatMessage("/duel " + player.getName()));
            }
        }
    }

    public static boolean mouseWithinBounds(int mouseX, int mouseY, double x, double y, double width, double height) {
        return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
    }

































    // КОСТЫЛЬ+++++++++++++++++++++++++++++++++++++++++++++++++





    private final int black = Color.BLACK.getRGB();

    private boolean isValid(Entity entity) {
        return entity instanceof EntityPlayer;
    }













}