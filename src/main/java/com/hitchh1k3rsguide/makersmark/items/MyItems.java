package com.hitchh1k3rsguide.makersmark.items;

import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.CoreUtils;
import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.potions.MakersPotion;
import com.hitchh1k3rsguide.makersmark.sided.IMakersBase;
import com.mojang.authlib.GameProfile;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class MyItems
{

    public Item goldenFeather, letter, mintersMallet, makersMark, coinDie, coin, inspectorsGlass, advancedGlass, potion, bag;
    private ArrayList<IMakersBase> items = new ArrayList<IMakersBase>();

    public void initialize()
    {
        if (goldenFeather == null)
        {
            goldenFeather = new ItemGeneric("golden_feather", new ItemGeneric.RecipeRegister()
            {
                @Override
                public void registerRecipes(MakersBaseItem item)
                {
                    GameRegistry.addShapelessRecipe(new ItemStack(item), Items.feather, Items.gold_nugget);
                }
            });
            items.add((IMakersBase) goldenFeather);
            CoreUtils.tellCore(CoreUtils.MESSAGE_ADD_SHARED_ITEM, new Object[]{ "goldenFeather", goldenFeather });
        }
        letter = new ItemLetter();
        mintersMallet = new ItemGeneric("minters_mallet", new ItemGeneric.RecipeRegister()
        {
            @Override
            public void registerRecipes(MakersBaseItem item)
            {
                GameRegistry.addShapedRecipe(new ItemStack(item), " S ", " sS", "s  ", 'S', Blocks.stone, 's', Items.stick);
                GameRegistry.addShapedRecipe(new ItemStack(item), " S ", "Ss ", "  s", 'S', Blocks.stone, 's', Items.stick);
            }
        }).setMaxStackSize(1).setMaxDamage(256).setCreativeTab(CreativeTabs.tabTools);
        makersMark = new ItemGeneric("makers_mark", new ItemGeneric.RecipeRegister()
        {
            @Override
            public void registerRecipes(MakersBaseItem item)
            {
                GameRegistry.addShapelessRecipe(new ItemStack(item), Items.iron_ingot, Items.flint, new ItemStack(mintersMallet, 1, OreDictionary.WILDCARD_VALUE));
            }
        }).setMaxStackSize(1).setMaxDamage(8).setCreativeTab(CreativeTabs.tabTools);
        coinDie = new ItemCoinDie();
        coin = new ItemCoin();
        inspectorsGlass = new ItemInspectorsGlass(false);
        advancedGlass = new ItemInspectorsGlass(true);
        potion = new ItemCustomPotion("custom_potion", new MakersPotion[]{ MakersMark.getPotions().potionOfLevitation }, new Item[]{ goldenFeather });
        bag = new ItemBag();

        items.add((IMakersBase) letter);
        items.add((IMakersBase) mintersMallet);
        items.add((IMakersBase) makersMark);
        items.add((IMakersBase) coinDie);
        items.add((IMakersBase) coin);
        items.add((IMakersBase) inspectorsGlass);
        items.add((IMakersBase) advancedGlass);
        items.add((IMakersBase) potion);
        items.add((IMakersBase) bag);
    }

    public void registerItems()
    {
        for (IMakersBase item : items)
        {
            MakersMark.instance.proxy.registerItem(item);
        }
    }

    public void registerOres()
    {
        // Register any ore dictionary names here:
    }

    public static List<String> getMarkers(ItemStack stack)
    {
        ArrayList<String> out = new ArrayList<String>();
        if (stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag.hasKey(ItemCoin.TAG_MINTERS))
            {
                if ((stack.getItem() == MakersMark.getItems().coin && stack.getItemDamage() != 1) || stack.getItem() == MakersMark.getItems().coinDie)
                {
                    NBTTagList list = tag.getTagList(ItemCoin.TAG_MINTERS, Constants.NBT.TAG_COMPOUND);
                    for (int i = 0; i < list.tagCount(); ++i)
                    {
                        GameProfile profile = NBTUtil.readGameProfileFromNBT(list.getCompoundTagAt(i));
                        out.add(profile.getName());
                    }
                }
                else
                {
                    GameProfile profile = NBTUtil.readGameProfileFromNBT(tag.getCompoundTag(ItemCoin.TAG_MINTERS));
                    out.add(profile.getName());
                }
            }
        }
        return out;
    }
}
