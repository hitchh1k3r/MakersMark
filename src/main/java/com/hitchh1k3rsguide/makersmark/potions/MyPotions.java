package com.hitchh1k3rsguide.makersmark.potions;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.util.PotionUtils;
import net.minecraft.util.ResourceLocation;

public class MyPotions
{

    public MakersPotion potionOfLevitation;
    public static final ResourceLocation icons = new ResourceLocation(MakersMark.MODID, "textures/misc/potions.png");

    public void createPotions()
    {
        potionOfLevitation = PotionUtils.newPotion(PotionLevitation.class);
    }

}
