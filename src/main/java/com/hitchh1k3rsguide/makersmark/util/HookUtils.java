package com.hitchh1k3rsguide.makersmark.util;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class HookUtils
{

    public static boolean isFlightKickDisabled(EntityPlayerMP playerEntity)
    {
        return playerEntity.getActivePotionEffect(MakersMark.getPotions().potionOfLevitation) != null;
    }

    public static boolean canBrewResult = false;

    public static boolean canBrew(ItemStack[] brewingItemStacks)
    {
        if (brewingItemStacks[3] != null)
        {
            // canBrewResult = true;
            // return true;
        }
        return false;
    }

}
