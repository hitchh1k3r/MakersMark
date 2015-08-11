package com.hitchh1k3rsguide.makersmark.items.crafting;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.items.ItemCoin;
import com.hitchh1k3rsguide.makersmark.items.ItemCoinDie;
import com.hitchh1k3rsguide.makersmark.items.MyItems;
import com.hitchh1k3rsguide.makersmark.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.lang.reflect.Field;

public class CraftingDieSigning implements IRecipe
{

    private ItemStack output = null;
    public static final Field FIELD_eventHandler;

    static
    {
        FIELD_eventHandler = Utils.getField(InventoryCrafting.class, "eventHandler", "field_70465_c");
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world)
    {
        updateOutput(inventory);
        return getCraftingResult(inventory) != null;
    }

    private void updateOutput(InventoryCrafting inventory)
    {
        boolean overflow = false;
        boolean hasFlint = false;
        int xFlint = 0, yFlint = 0, xDie = -1, yDie = 0;
        output = null;
        for (int x = 0; x < inventory.getWidth(); ++x)
        {
            for (int y = 0; y < inventory.getHeight(); ++y)
            {
                ItemStack stack = inventory.getStackInRowAndColumn(x, y);
                if (stack != null)
                {
                    if (stack.getItem() == Items.flint && !hasFlint)
                    {
                        hasFlint = true;
                        xFlint = x;
                        yFlint = y;
                    }
                    else if (stack.getItem() == MakersMark.getItems().makersMark && output == null)
                    {
                        output = new ItemStack(MakersMark.getItems().coinDie, 1, stack.getItemDamage() * MakersMark.getItems().coinDie.getMaxDamage() / MakersMark.getItems().makersMark.getMaxDamage());
                        xDie = x;
                        yDie = y;
                    }
                    else if (stack.getItem() == MakersMark.getItems().coinDie && output == null)
                    {
                        output = stack.copy();
                    }
                    else
                    {
                        overflow = true;
                        break;
                    }
                }
            }
        }
        if (overflow || !hasFlint)
        {
            output = null;
        }
        else if (output != null)
        {
            EntityPlayer player = null;
            if (FIELD_eventHandler != null)
            {
                try
                {
                    Container container = (Container) FIELD_eventHandler.get(inventory);
                    Slot slot = container.getSlot(container.getInventory().size() - 1);
                    IInventory playerInventory = slot.inventory;
                    if (playerInventory instanceof InventoryPlayer)
                    {
                        player = ((InventoryPlayer) playerInventory).player;
                    }
                }
                catch (Exception ignored) {}
            }
            boolean playerValid = player != null && !MyItems.getMarkers(output).contains(player.getName());
            if (playerValid)
            {
                ItemCoinDie.addPlayer(output, player);
            }
            if (xDie >= 0)
            {
                int dX = xFlint - xDie;
                int dY = yFlint - yDie;
                if (dX >= -1 && dX <= 1 && dY >= -1 && dY <= 1)
                {
                    int p = ((dY + 1) * 3) + (dX + 1);
                    if (p > 4)
                    {
                        --p;
                    }
                    while (p >= ItemCoin.SHAPES.length && p > 0)
                    {
                        p -= ItemCoin.SHAPES.length;
                    }
                    NBTTagCompound tag = output.getTagCompound();
                    if (tag == null)
                    {
                        tag = new NBTTagCompound();
                    }
                    tag.setInteger(ItemCoin.TAG_SHAPE, p);
                    output.setTagCompound(tag);
                }
                else
                {
                    output = null;
                }
            }
            else if (!playerValid)
            {
                output = null;
            }
        }
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
    {
        return output;
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
