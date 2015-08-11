package com.hitchh1k3rsguide.makersmark.graphics;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.entities.EntityCustomPotion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCustomSplashPotion extends RenderSnowball
{

    public RenderCustomSplashPotion()
    {
        super(Minecraft.getMinecraft().getRenderManager(), MakersMark.getItems().potion, Minecraft.getMinecraft().getRenderItem());
    }

    @Override
    public ItemStack func_177082_d(Entity entity) // might be called: getStackFromEntity or something...
    {
        return ((EntityCustomPotion) entity).getPotionStack();
    }

}