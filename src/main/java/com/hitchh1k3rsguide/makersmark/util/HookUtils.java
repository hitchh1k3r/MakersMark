package com.hitchh1k3rsguide.makersmark.util;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import net.minecraft.entity.player.EntityPlayerMP;

public class HookUtils
{

    public static boolean isFlightKickDisabled(EntityPlayerMP playerEntity)
    {
        return playerEntity.getActivePotionEffect(MakersMark.getPotions().potionOfLevitation) != null;
    }

}
