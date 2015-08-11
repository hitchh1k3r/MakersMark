package com.hitchh1k3rsguide.makersmark.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public interface IPickupInventory extends IInventory
{

    public void onPickupSlot(EntityPlayer player, int index);

}
