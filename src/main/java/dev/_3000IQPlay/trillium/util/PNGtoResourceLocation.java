package dev._3000IQPlay.trillium.util;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class PNGtoResourceLocation {



    public static ResourceLocation getTexture2(String name,String format) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File("Trillium/friendsAvatars/" + name + "." + format));
        } catch (Exception e) {
            return null;
        }
        DynamicTexture texture = new DynamicTexture(bufferedImage);
        WrappedResource wr = new WrappedResource(FMLClientHandler.instance().getClient().getTextureManager().getDynamicTextureLocation(name + "." + format, texture));
        return wr.location;
    }


    public static ResourceLocation getCustomImg(String name,String format) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File("Trillium/customimg/" + name + "." + format));
        } catch (Exception e) {
            return null;
        }
        DynamicTexture texture = new DynamicTexture(bufferedImage);
        WrappedResource wr = new WrappedResource(FMLClientHandler.instance().getClient().getTextureManager().getDynamicTextureLocation(name + "." + format, texture));
        return wr.location;
    }

    public static ResourceLocation getTexture3(String name,String format) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File("Trillium/skins/" + name + "." + format));
        } catch (Exception e) {
            return null;
        }
        DynamicTexture texture = new DynamicTexture(bufferedImage);
        WrappedResource wr = new WrappedResource(FMLClientHandler.instance().getClient().getTextureManager().getDynamicTextureLocation(name + "." + format, texture));
        return wr.location;
    }


    public static ResourceLocation getTexture(String name,String format) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File("Trillium/tmp/" + name + "." + format));
        } catch (Exception e) {
            return null;
        }
        float ratio = (float) bufferedImage.getWidth() / (float) bufferedImage.getHeight();  // ширина на высота 16/9 w 4   h 2  ratio 2

        DynamicTexture texture = new DynamicTexture(bufferedImage);
        WrappedResource wr = new WrappedResource(FMLClientHandler.instance().getClient().getTextureManager().getDynamicTextureLocation(name + "." + format, texture));
        return wr.location;
    }



    public static class WrappedResource {
        public final ResourceLocation location;

        public WrappedResource(ResourceLocation location) {
            this.location = location;
        }
    }
}