package com.hitchh1k3rsguide.makersmark.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryPickup extends InventoryBasic implements IPickupInventory
{

    public InventoryPickup(String name, boolean customName, int size)
    {
        super(name, customName, size);
    }

    public void loadInventoryFromNBT(NBTTagList p_70486_1_)
    {
        int i;

        for (i = 0; i < this.getSizeInventory(); ++i)
        {
            this.setInventorySlotContents(i, null);
        }

        for (i = 0; i < p_70486_1_.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = p_70486_1_.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < this.getSizeInventory())
            {
                this.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
            }
        }
    }

    public NBTTagList saveInventoryToNBT()
    {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.getStackInSlot(i);

            if (itemstack != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        return nbttaglist;
    }

    @Override
    public void onPickupSlot(EntityPlayer player, int index) {}

}
