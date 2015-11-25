package com.hitchh1k3rsguide.makersmark.sided;

import com.hitchh1k3rsguide.makersmark.events.ServerEvents;
import com.hitchh1k3rsguide.makersmark.network.MessageServerSettings;
import com.hitchh1k3rsguide.makersmark.sided.Proxy.IProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.*;

public class ServerSide implements IProxy
{

    @Override
    public void construction(FMLConstructionEvent event)
    {
    }

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        ServerEvents eventHooks = new ServerEvents();
        MinecraftForge.EVENT_BUS.register(eventHooks);
        FMLCommonHandler.instance().bus().register(eventHooks);
    }

    @Override
    public void loadComplete(FMLLoadCompleteEvent event)
    {
    }

    @Override
    public void serverPreStart(FMLServerAboutToStartEvent event)
    {
        new MessageServerSettings().processPacket();
    }

    @Override
    public void serverStart(FMLServerStartingEvent event)
    {
    }

    @Override
    public void serverPostStart(FMLServerStartedEvent event)
    {
    }

    @Override
    public void serverPreStop(FMLServerStoppingEvent event)
    {
    }

    @Override
    public void serverPostStop(FMLServerStoppedEvent event)
    {
    }

    @Override
    public void registerItem(IMakersBase item)
    {
        item.serverRegister();
    }

    @Override
    public void registerBlock(IMakersBase block)
    {
        block.serverRegister();
    }

}
