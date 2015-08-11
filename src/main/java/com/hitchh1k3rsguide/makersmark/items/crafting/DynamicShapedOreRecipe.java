package com.hitchh1k3rsguide.makersmark.items.crafting;

import com.hitchh1k3rsguide.makersmark.util.ICondition;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class DynamicShapedOreRecipe extends ShapedOreRecipe
{

    private ICondition<InventoryCrafting> condition;

    public DynamicShapedOreRecipe(ItemStack output, ICondition<InventoryCrafting> condition, Object... recipe)
    {
        super(output, recipe);
        this.condition = condition;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world)
    {
        return super.matches(inv, world) && condition.test(inv);
    }
}
