package com.hitchh1k3rsguide.makersmark.items.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;

public class ShapedRecipeNoContainers extends ShapedRecipes
{

    public ShapedRecipeNoContainers(int width, int height, ItemStack[] stacks, ItemStack output)
    {
        super(width, height, stacks, output);
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting p_179532_1_)
    {
        return new ItemStack[p_179532_1_.getSizeInventory()];
    }
}
