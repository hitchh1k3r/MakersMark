package com.hitchh1k3rsguide.makersmark.sided;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IMakersBase
{

    public String getUnlocalizedNameRaw();

    public String getTextureName();

    public void commonRegister();

    @SideOnly(Side.CLIENT)
    public void clientRegister();

    @SideOnly(Side.SERVER)
    public void serverRegister();

}
