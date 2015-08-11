package com.hitchh1k3rsguide.makersmark.nei;

import codechicken.nei.ItemStackSet;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.BrewingRecipeHandler;
import com.hitchh1k3rsguide.makersmark.MakersMark;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.HashSet;

public class NEIPotionHandler extends BrewingRecipeHandler
{

    private static final int AWKWARD = 1 << 4;

    private static final int SLOWNESS   = 10;
    private static final int LEVITATION = 1;

    private static final int DRINK    = 1 << 13;
    private static final int SPLASH   = 1 << 14;
    private static final int LEVEL2   = 1 << 5;
    private static final int EXTENDED = 1 << 6;

    public static class CustomBrewingRecipe extends BrewingRecipeHandler.BrewingRecipe
    {

        public CustomBrewingRecipe(ItemStack ingred, ItemStack basePotion, ItemStack outputPotion)
        {
            super(ingred, 0, 0);
            precursorPotion = new PositionedStack(basePotion, 51, 35);
            ingredient = new PositionedStack(ingred, 74, 6);
            result = new PositionedStack(outputPotion, 97, 35);
        }

    }

    public static final ItemStackSet           allIngredients = new ItemStackSet();
    public static final HashSet<BrewingRecipe> allRecipes     = new HashSet<BrewingRecipe>();

    public static void initRecipes()
    {
        // Formatter off
        ItemStack feather   = new ItemStack(MakersMark.getItems().goldenFeather);
        ItemStack eye       = new ItemStack(                Items.fermented_spider_eye);
        ItemStack glowstone = new ItemStack(                Items.glowstone_dust);
        ItemStack redstone  = new ItemStack(                Items.redstone);
        ItemStack gunpowder = new ItemStack(                Items.gunpowder);

        allIngredients.add(feather);
        allIngredients.add(eye);
        allIngredients.add(glowstone);
        allIngredients.add(redstone);
        allIngredients.add(gunpowder);

        allRecipes.add(new CustomBrewingRecipe(feather,   new ItemStack(Items.potionitem,             1, AWKWARD),                        new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION)));

        allRecipes.add(new CustomBrewingRecipe(eye,       new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION),            new ItemStack(Items.potionitem,             1, DRINK  + SLOWNESS)));
        allRecipes.add(new CustomBrewingRecipe(glowstone, new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION),            new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION + LEVEL2)));
        allRecipes.add(new CustomBrewingRecipe(redstone,  new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION),            new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION + EXTENDED)));
        allRecipes.add(new CustomBrewingRecipe(gunpowder, new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION),            new ItemStack(MakersMark.getItems().potion, 1, SPLASH + LEVITATION)));

        allRecipes.add(new CustomBrewingRecipe(eye,       new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION + LEVEL2),   new ItemStack(Items.potionitem,             1, DRINK  + SLOWNESS   + LEVEL2)));
        allRecipes.add(new CustomBrewingRecipe(redstone,  new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION + LEVEL2),   new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION + EXTENDED)));
        allRecipes.add(new CustomBrewingRecipe(gunpowder, new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION + LEVEL2),   new ItemStack(MakersMark.getItems().potion, 1, SPLASH + LEVITATION + LEVEL2)));

        allRecipes.add(new CustomBrewingRecipe(eye,       new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION + EXTENDED), new ItemStack(Items.potionitem,             1, DRINK  + SLOWNESS   + EXTENDED)));
        allRecipes.add(new CustomBrewingRecipe(glowstone, new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION + EXTENDED), new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION + LEVEL2)));
        allRecipes.add(new CustomBrewingRecipe(gunpowder, new ItemStack(MakersMark.getItems().potion, 1, DRINK  + LEVITATION + EXTENDED), new ItemStack(MakersMark.getItems().potion, 1, SPLASH + LEVITATION + EXTENDED)));

        allRecipes.add(new CustomBrewingRecipe(eye,       new ItemStack(MakersMark.getItems().potion, 1, SPLASH + LEVITATION),            new ItemStack(Items.potionitem,             1, SPLASH + SLOWNESS)));
        allRecipes.add(new CustomBrewingRecipe(glowstone, new ItemStack(MakersMark.getItems().potion, 1, SPLASH + LEVITATION),            new ItemStack(MakersMark.getItems().potion, 1, SPLASH + LEVITATION + LEVEL2)));
        allRecipes.add(new CustomBrewingRecipe(redstone,  new ItemStack(MakersMark.getItems().potion, 1, SPLASH + LEVITATION),            new ItemStack(MakersMark.getItems().potion, 1, SPLASH + LEVITATION + EXTENDED)));

        allRecipes.add(new CustomBrewingRecipe(eye,       new ItemStack(MakersMark.getItems().potion, 1, SPLASH + LEVITATION + LEVEL2),   new ItemStack(Items.potionitem,             1, SPLASH + SLOWNESS   + LEVEL2)));
        allRecipes.add(new CustomBrewingRecipe(redstone,  new ItemStack(MakersMark.getItems().potion, 1, SPLASH + LEVITATION + LEVEL2),   new ItemStack(MakersMark.getItems().potion, 1, SPLASH + LEVITATION + EXTENDED)));

        allRecipes.add(new CustomBrewingRecipe(eye,       new ItemStack(MakersMark.getItems().potion, 1, SPLASH + LEVITATION + EXTENDED), new ItemStack(Items.potionitem,             1, SPLASH + SLOWNESS   + EXTENDED)));
        allRecipes.add(new CustomBrewingRecipe(glowstone, new ItemStack(MakersMark.getItems().potion, 1, SPLASH + LEVITATION + EXTENDED), new ItemStack(MakersMark.getItems().potion, 1, SPLASH + LEVITATION + LEVEL2)));
        // Formatter on
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals("brewing") && getClass() == NEIPotionHandler.class)
        {
            for (BrewingRecipe recipe : allRecipes)
            {
                arecipes.add(new CachedBrewingRecipe(recipe));
            }
        }
        else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        if (result.getItem() != MakersMark.getItems().potion && result.getItem() != Items.potionitem)
        {
            return;
        }
        int damage = result.getItemDamage();

        for (BrewingRecipe recipe : allRecipes)
        {
            if (recipe.result.item.getItemDamage() == damage)
            {
                arecipes.add(new CachedBrewingRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        if (ingredient.getItem() != MakersMark.getItems().potion && ingredient.getItem() != Items.potionitem && !allIngredients.contains(ingredient))
        {
            return;
        }

        for (BrewingRecipe recipe : allRecipes)
        {
            if (NEIServerUtils.areStacksSameType(recipe.ingredient.item, ingredient) || NEIServerUtils.areStacksSameType(recipe.precursorPotion.item, ingredient))
            {
                arecipes.add(new CachedBrewingRecipe(recipe));
            }
        }
    }

    @Override
    public String getRecipeName()
    {
        return NEIClientUtils.translate("makersmark.recipe.brewing");
    }

}
