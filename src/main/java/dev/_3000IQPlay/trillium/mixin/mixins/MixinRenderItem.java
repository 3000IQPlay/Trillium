package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.event.events.RenderItemEvent;
import dev._3000IQPlay.trillium.modules.render.CustomEnchants;
import dev._3000IQPlay.trillium.modules.render.ViewModel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderItem.class})
public
class MixinRenderItem {

    private static final ResourceLocation RESOURCE = new ResourceLocation("textures/rainbow.png");

    @Shadow
    private
    void renderModel ( IBakedModel model , int color , ItemStack stack ) {
    }

    @Redirect(method = "renderEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V", ordinal = 0))
    public void bindHook(TextureManager textureManager, ResourceLocation resource){
        if (CustomEnchants.getInstance().isEnabled()) { 
		    textureManager.bindTexture(RESOURCE);
        } else {
            textureManager.bindTexture(resource);
        }
    }

    @Inject(method = {"renderItemModel"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", shift = At.Shift.BEFORE)})
    private void renderItemModel ( ItemStack stack , IBakedModel bakedModel , ItemCameraTransforms.TransformType transform , boolean leftHanded , CallbackInfo ci ) {
        RenderItemEvent event = new RenderItemEvent (
                0f , 0f , 0f ,
                0f, 0f , 0f ,
                0.0f , 0.0f , 1.0f ,
                0.0f , 0.0f, 0.0f ,
                1.0f , 1.0f , 1.0f , 1.0f ,
                1.0f , 1.0f // , 1.0 , 1.0
        );
        MinecraftForge.EVENT_BUS.post ( event );
        if (ViewModel.getInstance().isEnabled() ) {
            if (!leftHanded) {
                GlStateManager.scale(event.getMainHandScaleX() ,event.getMainHandScaleY() ,event.getMainHandScaleZ());
            } else {
                GlStateManager.scale(event.getOffHandScaleX() ,event.getOffHandScaleY() ,event.getOffHandScaleZ());
            }
        }
    }
}