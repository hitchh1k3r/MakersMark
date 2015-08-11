package com.hitchh1k3rsguide.makersmark.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.api.ItemFilter;
import codechicken.nei.recipe.BrewingRecipeHandler;
import codechicken.nei.recipe.DefaultOverlayHandler;
import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.config.MakersConfig;
import com.hitchh1k3rsguide.makersmark.containers.SlotLimit;
import com.hitchh1k3rsguide.makersmark.containers.SlotPickup;
import com.hitchh1k3rsguide.makersmark.items.ItemCoin;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.item.ItemStack;

import java.util.Iterator;


public class NEIMakersMarkConfig implements IConfigureNEI
{

    @Override
    public void loadConfig()
    {
        API.registerRecipeHandler(new NEIAnvilHandler());
        API.registerUsageHandler(new NEIAnvilHandler());
        API.registerRecipeHandler(new NEIShapedHandler());
        API.registerUsageHandler(new NEIShapedHandler());
        API.registerRecipeHandler(new NEIPotionHandler());
        API.registerUsageHandler(new NEIPotionHandler());
        NEICommon.initLists();
        NEIPotionHandler.initRecipes();

        API.setItemListEntries(MakersMark.getItems().coin, ((ItemCoin) MakersMark.getItems().coin).getNEIItems());
        API.addItemFilter(new ItemFilter.ItemFilterProvider()
        {
            @Override
            public ItemFilter getFilter()
            {
                return new ItemFilter()
                {
                    @Override
                    public boolean matches(ItemStack item)
                    {
                        if (item.getItem() == MakersMark.getItems().inspectorsGlass)
                        {
                            return !MakersConfig.ServerConfig.limitedInspection;
                        }
                        return true;
                    }
                };
            }
        });

        API.addFastTransferExemptSlot(SlotPickup.class);
        API.addFastTransferExemptSlot(SlotLimit.class);

        API.registerGuiOverlay(GuiRepair.class, "anvil", 5, 7);
        API.registerGuiOverlayHandler(GuiRepair.class, new DefaultOverlayHandler(5, 7), "anvil");

        Iterator<BrewingRecipeHandler.BrewingRecipe> it = BrewingRecipeHandler.apotions.iterator();
        while (it.hasNext())
        {
            BrewingRecipeHandler.BrewingRecipe recipe = it.next();
            if (recipe.ingredient.item.getItem() == MakersMark.getItems().goldenFeather)
            {
                it.remove();
            }
        }

        for (ItemStack stack : BrewingRecipeHandler.ingredients.keys())
        {
            if (stack.getItem() == MakersMark.getItems().goldenFeather)
            {
                BrewingRecipeHandler.ingredients.remove(stack);
            }
        }
    }

    @Override
    public String getName()
    {
        return MakersMark.MODNAME;
    }

    @Override
    public String getVersion()
    {
        return MakersMark.VERSION;
    }

}