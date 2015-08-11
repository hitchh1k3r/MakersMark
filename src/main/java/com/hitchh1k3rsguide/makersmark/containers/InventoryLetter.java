package com.hitchh1k3rsguide.makersmark.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;

public class InventoryLetter extends InventoryBasic implements IPickupInventory
{

    public InventoryLetter()
    {
        super("", false, 1);
    }

    @Override
    public void onPickupSlot(EntityPlayer player, int index)
    {
        if (player.openContainer instanceof ContainerLetter)
        {
            ((ContainerLetter) player.openContainer).taken = true;
            ((ContainerLetter) player.openContainer).removeTakeSlots();
        }
    }

}
