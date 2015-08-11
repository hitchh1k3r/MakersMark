package com.hitchh1k3rsguide.makersmark.containers;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.items.ItemBag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBag extends Container
{

    private InventoryPickup inventory;
    private IInventory playerInventory;

    public ContainerBag(ItemStack bag, EntityPlayer player)
    {
        inventory = new InventoryPickup("container." + MakersMark.MODID + ".bag", false, 3);
        if (!player.worldObj.isRemote)
        {
            ItemBag.setInventory(inventory, bag);
        }

        playerInventory = player.inventory;

        // Bag Inventory:
        addSlotToContainer(new SlotPickup(inventory, 0, 52, 30));
        addSlotToContainer(new SlotPickup(inventory, 1, 80, 43));
        addSlotToContainer(new SlotPickup(inventory, 2, 108, 30));

        // Player Inventory:
        int j, k;
        for (j = 0; j < 3; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                addSlotToContainer(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }
        for (j = 0; j < 9; ++j)
        {
            addSlotToContainer(new Slot(playerInventory, j, 8 + j * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 3)
            {
                if (!mergeItemStack(itemstack1, 3, this.getInventory().size(), true))
                {
                    return null;
                }
            }
            else
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);

        if (!player.worldObj.isRemote)
        {
            for (int i = 0; i < 3; ++i)
            {
                ItemStack itemstack = inventory.getStackInSlotOnClosing(i);

                if (itemstack != null)
                {
                    player.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
    }

    public String getName(boolean bottom)
    {
        if (bottom)
        {
            return playerInventory.getDisplayName().getUnformattedText();
        }
        else
        {
            return inventory.getDisplayName().getUnformattedText();
        }
    }

}
