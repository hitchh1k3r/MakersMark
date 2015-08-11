package com.hitchh1k3rsguide.makersmark.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public interface IMakersMessage
{

    public Class<? extends IMessageHandler> getHandler();

}
