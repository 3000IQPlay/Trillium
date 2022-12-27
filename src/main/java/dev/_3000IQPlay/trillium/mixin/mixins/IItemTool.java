package dev._3000IQPlay.trillium.mixin.mixins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemTool.class)
public interface IItemTool
{
    @Accessor(value = "attackDamage")
    float getAttackDamage();

    @Accessor(value = "toolMaterial")
    Item.ToolMaterial getToolMaterial();

}