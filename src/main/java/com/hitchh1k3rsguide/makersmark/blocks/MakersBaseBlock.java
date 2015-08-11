package com.hitchh1k3rsguide.makersmark.blocks;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.sided.IMakersBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class MakersBaseBlock extends Block implements IMakersBase
{

    protected MakersBaseBlock(Material materialIn)
    {
        super(materialIn);
    }

    @Override
    public String getTextureName()
    {
        return MakersMark.MODID + ":" + getUnlocalizedNameRaw();
    }

    @Override
    public void commonRegister()
    {
        GameRegistry.registerBlock(this, getUnlocalizedNameRaw());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientRegister()
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getTextureName(), "inventory"));
    }

    @Override
    @SideOnly(Side.SERVER)
    public void serverRegister()
    {
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile." + MakersMark.MODID + "." + getUnlocalizedNameRaw();
    }

}
