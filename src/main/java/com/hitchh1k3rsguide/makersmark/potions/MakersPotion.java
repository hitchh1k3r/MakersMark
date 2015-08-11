package com.hitchh1k3rsguide.makersmark.potions;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

abstract public class MakersPotion extends Potion
{

    protected MakersPotion(int potionID, String name, boolean isNegativeEffect, int liquidColor)
    {
        super(potionID, new ResourceLocation(name), isNegativeEffect, liquidColor);
    }

    abstract public boolean isExtendable();

    abstract public boolean isUpgradeable();

    abstract public int getDuration(int meta);

    abstract public int getAmplifier(int meta);

    public abstract boolean showModifiers();

}
