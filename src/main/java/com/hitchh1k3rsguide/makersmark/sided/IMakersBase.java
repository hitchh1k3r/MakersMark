package com.hitchh1k3rsguide.makersmark.sided;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IMakersBase
{

    String getUnlocalizedNameRaw();

    String getTextureName();

    void commonRegister();

    @SideOnly(Side.CLIENT)
    void clientRegister();

    @SideOnly(Side.SERVER)
    void serverRegister();

}
