package com.hitchh1k3rsguide.makersmark.items;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.sided.IMakersBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class MakersBaseItem extends Item implements IMakersBase
{

    public MakersBaseItem()
    {
        this.setUnlocalizedName(getUnlocalizedNameRaw());
        this.setCreativeTab(MakersMark.getCreativeTab());
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

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return "item." + MakersMark.MODID + "." + getUnlocalizedNameRaw();
    }

}
