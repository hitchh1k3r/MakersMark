package com.hitchh1k3rsguide.makersmark.items.crafting;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.blocks.BlockMailbox;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class CraftingMailboxColors implements IRecipe
{

    private              ItemStack output     = null;
    private static final int[]     colorToOre = new int[16];

    static
    {
        colorToOre[0] = OreDictionary.getOreID("dyeBlack");
        colorToOre[1] = OreDictionary.getOreID("dyeRed");
        colorToOre[2] = OreDictionary.getOreID("dyeGreen");
        colorToOre[3] = OreDictionary.getOreID("dyeBrown");
        colorToOre[4] = OreDictionary.getOreID("dyeBlue");
        colorToOre[5] = OreDictionary.getOreID("dyePurple");
        colorToOre[6] = OreDictionary.getOreID("dyeCyan");
        colorToOre[7] = OreDictionary.getOreID("dyeLightGray");
        colorToOre[8] = OreDictionary.getOreID("dyeGray");
        colorToOre[9] = OreDictionary.getOreID("dyePink");
        colorToOre[10] = OreDictionary.getOreID("dyeLime");
        colorToOre[11] = OreDictionary.getOreID("dyeYellow");
        colorToOre[12] = OreDictionary.getOreID("dyeLightBlue");
        colorToOre[13] = OreDictionary.getOreID("dyeMagenta");
        colorToOre[14] = OreDictionary.getOreID("dyeOrange");
        colorToOre[15] = OreDictionary.getOreID("dyeWhite");
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world)
    {
        return getCraftingResult(inventory) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
    {
        output = getItemStack(inventory);
        return output;
    }

    public static ItemStack getItemStack(InventoryCrafting inventory)
    {
        ItemStack ret = null;
        boolean overflow = false;
        ItemStack mailbox = null;
        int color = -1;
        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null)
            {
                if (mailbox == null && stack.getItem() == Item.getItemFromBlock(MakersMark.getBlocks().mailBox))
                {
                    mailbox = stack;
                }
                else if (color == -1)
                {
                    int[] ores = OreDictionary.getOreIDs(stack);
                    for (int o = 0; o < ores.length && color == -1; ++o)
                    {
                        for (int c = 0; c < colorToOre.length && color == -1; ++c)
                        {
                            if (colorToOre[c] == ores[o])
                            {
                                color = c;
                            }
                        }
                    }
                    if (color == -1)
                    {
                        overflow = true;
                        break;
                    }
                }
                else
                {
                    overflow = true;
                    break;
                }
            }
        }
        if (!overflow && mailbox != null && color != -1)
        {
            ret = mailbox.copy();
            NBTTagCompound tag = ret.getTagCompound();
            if (tag == null)
            {
                tag = new NBTTagCompound();
            }
            tag.setInteger(BlockMailbox.TAG_MAILBOX_COLOR, color);
            ret.setTagCompound(tag);
        }
        return ret;
    }

    @Override
    public int getRecipeSize()
    {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return output;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inventory)
    {
        return new ItemStack[inventory.getSizeInventory()];
    }

}