package com.hitchh1k3rsguide.makersmark.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotPickup extends Slot
{

    public SlotPickup(IPickupInventory inventory, int index, int x, int y)
    {
        super(inventory, index, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
    {
        ((IPickupInventory) inventory).onPickupSlot(playerIn, getSlotIndex());
        this.onSlotChanged();
    }

}
