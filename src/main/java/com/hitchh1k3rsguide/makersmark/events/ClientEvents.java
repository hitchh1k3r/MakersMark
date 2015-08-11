package com.hitchh1k3rsguide.makersmark.events;

import com.hitchh1k3rsguide.makersmark.items.ItemCoin;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents
{

    @SubscribeEvent
    public void textureUpdate(TextureStitchEvent.Post event)
    {
        ItemCoin.MaterialDefinition.calculateColors(event.map, ItemCoin.metals.values(), true);
        ItemCoin.MaterialDefinition.calculateColors(event.map, ItemCoin.woods.values(), false);
    }

    public static void firstRenderable()
    {
        ItemCoin.MaterialDefinition.calculateColors(Minecraft.getMinecraft().getTextureMapBlocks(), ItemCoin.metals.values(), true);
        ItemCoin.MaterialDefinition.calculateColors(Minecraft.getMinecraft().getTextureMapBlocks(), ItemCoin.woods.values(), false);
    }

}
