package com.hitchh1k3rsguide.makersmark.network;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class MyMessages
{

    private SimpleNetworkWrapper netWrapper = new SimpleNetworkWrapper(MakersMark.MODID);

    public void registerMessages()
    {
        netWrapper.registerMessage(MessageMailPlayerList.Handler.class, MessageMailPlayerList.class, 0, Side.CLIENT);
        netWrapper.registerMessage(MessageGUIButton.Handler.class, MessageGUIButton.class, 1, Side.SERVER);
        netWrapper.registerMessage(MessageGUIString.Handler.class, MessageGUIString.class, 2, Side.SERVER);
        netWrapper.registerMessage(MessageAuxEvent.Handler.class, MessageAuxEvent.class, 3, Side.CLIENT);
        netWrapper.registerMessage(MessageServerSettings.Handler.class, MessageServerSettings.class, 4, Side.CLIENT);
    }

    public SimpleNetworkWrapper sender()
    {
        return netWrapper;
    }

}
