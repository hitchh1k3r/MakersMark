package com.hitchh1k3rsguide.makersmark;

import com.hitchh1k3rsguide.makersmark.asm.ASMTransformer;
import com.hitchh1k3rsguide.makersmark.blocks.MyBlocks;
import com.hitchh1k3rsguide.makersmark.items.MyItems;
import com.hitchh1k3rsguide.makersmark.potions.MyPotions;
import com.hitchh1k3rsguide.makersmark.sided.Proxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(version = MakersMark.VERSION, modid = MakersMark.MODID, canBeDeactivated = false, dependencies = "required-after:hitchcore", acceptedMinecraftVersions = "[1.8,1.9)")
public class MakersMark
{

    public static final String MODNAME = "Maker's Mark: The Minter's Mod";
    public static final String MODID   = "makersmark";
    public static final String VERSION = "2.0.2";
    public static final Logger LOGGER  = LogManager.getLogger(MODNAME);

    @Instance(MODID)
    public static MakersMark instance;
    public Proxy proxy = new Proxy();

    public static CreativeTabs getCreativeTab()
    {
        return instance.proxy.creativeTab;
    }

    public static MyBlocks getBlocks()
    {
        return instance.proxy.blocks;
    }

    public static MyItems getItems()
    {
        return instance.proxy.items;
    }

    public static MyPotions getPotions()
    {
        return instance.proxy.potions;
    }

    public static SimpleNetworkWrapper getNetwork()
    {
        return instance.proxy.messages.sender();
    }

    @EventHandler
    public void construction(FMLConstructionEvent event)
    {
        proxy.construction(event);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ASMTransformer.doPatches();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event)
    {
        proxy.loadComplete(event);
    }

    @EventHandler
    public void serverPreStart(FMLServerAboutToStartEvent event)
    {
        proxy.serverPreStart(event);
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event)
    {
        proxy.serverStart(event);
    }

    @EventHandler
    public void serverPostStart(FMLServerStartedEvent event)
    {
        proxy.serverPostStart(event);
    }

    @EventHandler
    public void serverPreStop(FMLServerStoppingEvent event)
    {
        proxy.serverPreStop(event);
    }

    @EventHandler
    public void serverPostStop(FMLServerStoppedEvent event)
    {
        proxy.serverPostStop(event);
    }

}
