package com.hitchh1k3rsguide.makersmark.sided;

import com.hitchh1k3rsguide.makersmark.events.ServerEvents;
import com.hitchh1k3rsguide.makersmark.network.MessageServerSettings;
import com.hitchh1k3rsguide.makersmark.sided.Proxy.IProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.LanguageRegistry;

import java.util.HashMap;

public class ServerSide implements IProxy
{

    @Override
    public void construction(FMLConstructionEvent event)
    {
    }

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        HashMap<String, String> lang = new HashMap<String, String>();
        // lang.put("hitchcore.update.description", "Update Available");
        lang.put("hitchcore.update.nag", "%1s has been updated!§4 (%2s -> %3s)");
        lang.put("hitchcore.update.change.prompt", "Type§3 /hitch changes§r for more information.");
        lang.put("hitchcore.update.change.list", "%1s Changes:§4 (%2s -> %3s)");
        lang.put("hitchcore.update.change.download", "You can download updates at:§3 http://hitchh1k3rsguide.com/mods");
        LanguageRegistry.instance().injectLanguage("en_US", lang);
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
