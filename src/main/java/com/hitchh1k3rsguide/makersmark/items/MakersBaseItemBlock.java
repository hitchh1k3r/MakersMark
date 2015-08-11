package com.hitchh1k3rsguide.makersmark.items;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.blocks.IColoredBlock;
import com.hitchh1k3rsguide.makersmark.sided.IMakersBase;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class MakersBaseItemBlock extends ItemBlock implements IMakersBase
{

    public MakersBaseItemBlock(Block block)
    {
        super(block);
    }

    @Override
    public String getTextureName()
    {
        return MakersMark.MODID + ":" + getUnlocalizedNameRaw();
    }

    @Override
    public void commonRegister()
    {
        GameRegistry.registerItem(this, getUnlocalizedNameRaw());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientRegister()
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(getTextureName(), "inventory"));
    }

    @Override
    @SideOnly(Side.SERVER)
    public void serverRegister()
    {
    }

    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
        if (block instanceof IColoredBlock)
        {
            return ((IColoredBlock) block).getColor(stack, pass);
        }
        return super.getColorFromItemStack(stack, pass);
    }

}
