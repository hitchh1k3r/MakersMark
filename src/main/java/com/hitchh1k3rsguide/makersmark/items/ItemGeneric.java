package com.hitchh1k3rsguide.makersmark.items;

import com.hitchh1k3rsguide.makersmark.MakersMark;

public class ItemGeneric extends MakersBaseItem
{

    private String         name;
    private RecipeRegister recipeRegister;

    public ItemGeneric(String name, RecipeRegister recipeRegister)
    {
        this.name = name;
        this.recipeRegister = recipeRegister;
        this.setUnlocalizedName(getUnlocalizedNameRaw());
        this.setCreativeTab(MakersMark.getCreativeTab());
    }

    @Override
    public String getUnlocalizedNameRaw()
    {
        return name;
    }

    @Override
    public void commonRegister()
    {
        super.commonRegister();

        recipeRegister.registerRecipes(this);
    }

    public static abstract class RecipeRegister
    {

        public abstract void registerRecipes(MakersBaseItem item);

    }
}
