package com.hitchh1k3rsguide.makersmark.sided;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.entities.EntityCustomPotion;
import com.hitchh1k3rsguide.makersmark.events.ClientEvents;
import com.hitchh1k3rsguide.makersmark.graphics.RenderCustomSplashPotion;
import com.hitchh1k3rsguide.makersmark.items.ItemCoin;
import com.hitchh1k3rsguide.makersmark.sided.Proxy.IProxy;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.*;

public class ClientSide implements IProxy
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
        RenderingRegistry.registerEntityRenderingHandler(EntityCustomPotion.class, new RenderCustomSplashPotion());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        ClientEvents eventHooks = new ClientEvents();
        MinecraftForge.EVENT_BUS.register(eventHooks);
        FMLCommonHandler.instance().bus().register(eventHooks);

        Item item = Item.getItemFromBlock(MakersMark.getBlocks().mailBox);
        for (ItemCoin.MaterialDefinition wood : ItemCoin.woods.values())
        {
            ModelBakery.addVariantName(item, MakersMark.MODID + ":" + wood.name + "_mailbox");
        }
    }

    @Override
    public void loadComplete(FMLLoadCompleteEvent event)
    {
    }

    @Override
    public void serverPreStart(FMLServerAboutToStartEvent event)
    {
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
        item.clientRegister();
    }

    @Override
    public void registerBlock(IMakersBase block)
    {
        block.clientRegister();
    }

}
