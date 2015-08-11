package com.hitchh1k3rsguide.makersmark.items.crafting;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.containers.InventoryPickup;
import com.hitchh1k3rsguide.makersmark.items.ItemBag;
import com.hitchh1k3rsguide.makersmark.items.ItemCoin;
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
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.World;

public class CraftingBag implements IRecipe
{

    private ItemStack output = null;

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

    public static ItemStack getItemStack(InventoryPickup payload)
    {
        ItemStack ret = new ItemStack(MakersMark.getItems().bag);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag(ItemBag.TAG_INVENTORY, payload.saveInventoryToNBT());
        ret.setTagCompound(tag);
        return ret;
    }

    public static ItemStack getItemStack(InventoryCrafting inventory)
    {
        ItemStack ret = null;
        boolean overflow = false;
        boolean hasLeather = false;
        boolean hasString = false;
        boolean hasPayload = false;
        InventoryPickup payload = new InventoryPickup("", false, 3);
        for (int x = 0; x < inventory.getWidth(); ++x)
        {
            for (int y = 0; y < inventory.getHeight(); ++y)
            {
                ItemStack stack = inventory.getStackInRowAndColumn(x, y);
                if (stack != null)
                {
                    if (y == 0 && x == 1 && stack.getItem() == Items.string)
                    {
                        hasString = true;
                    }
                    else if (y == 2 && x == 1 && stack.getItem() == Items.leather)
                    {
                        hasLeather = true;
                    }
                    else if (y == 1)
                    {
                        hasPayload = true;
                        payload.setInventorySlotContents(x, stack.copy());
                    }
                    else
                    {
                        overflow = true;
                        break;
                    }
                }
            }
        }
        if (!overflow && hasLeather && hasString && hasPayload)
        {
            ret = getItemStack(payload);
            EntityPlayer player = null;
            if (CraftingDieSigning.FIELD_eventHandler != null)
            {
                try
                {
                    Container container = (Container) CraftingDieSigning.FIELD_eventHandler.get(inventory);
                    Slot slot = container.getSlot(container.getInventory().size() - 1);
                    IInventory playerInventory = slot.inventory;
                    if (playerInventory instanceof InventoryPlayer)
                    {
                        player = ((InventoryPlayer) playerInventory).player;
                    }
                }
                catch (Exception ignored) {}
            }
            if (player != null)
            {
                NBTTagCompound tag = ret.getTagCompound();
                if (tag == null)
                {
                    tag = new NBTTagCompound();
                }
                NBTTagCompound ownerCompound = new NBTTagCompound();
                NBTUtil.writeGameProfile(ownerCompound, player.getGameProfile());
                tag.setTag(ItemCoin.TAG_MINTERS, ownerCompound);
            }
        }
        return ret;
    }

    @Override
    public int getRecipeSize()
    {
        return 4;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return output;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inventory)
    {
        ItemStack[] remaining = new ItemStack[inventory.getSizeInventory()];
        for (int i = inventory.getWidth(); i < inventory.getSizeInventory() - inventory.getWidth(); ++i)
        {
            inventory.setInventorySlotContents(i, null);
        }
        return remaining;
    }

}
