package com.hitchh1k3rsguide.makersmark.blocks;

import net.minecraft.item.ItemStack;

public interface IColoredBlock
{

    int getColor(ItemStack stack, int pass);

}
