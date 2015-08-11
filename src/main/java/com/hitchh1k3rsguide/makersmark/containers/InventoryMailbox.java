package com.hitchh1k3rsguide.makersmark.containers;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class InventoryMailbox extends InventoryBasic implements IPickupInventory
{

    public static final String INVENTORY_NBT_TAG = MakersMark.MODID + ".mailbox.inventory";

    public InventoryMailbox(String title, boolean customName)
    {
        super(title, customName, 5 * 9);
    }

    private void loadInventoryFromNBT(NBTTagList p_70486_1_)
    {
        int i;

        for (i = 0; i < this.getSizeInventory(); ++i)
        {
            this.setInventorySlotContents(i, (ItemStack) null);
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

    private NBTTagList saveInventoryToNBT()
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

    public void saveInventory(EntityPlayer player)
    {
        NBTTagList nbt = saveInventoryToNBT();
        Utils.getPlayerPersistantData(player).setTag(INVENTORY_NBT_TAG, nbt);
        ContainerMailbox.updateMailCount(player.getGameProfile().getId(), nbt.tagCount());
    }

    public void loadInventory(EntityPlayer player)
    {
        NBTTagCompound nbt = Utils.getPlayerPersistantData(player);
        if (nbt.hasKey(INVENTORY_NBT_TAG))
        {
            loadInventoryFromNBT(Utils.getPlayerPersistantData(player).getTagList(INVENTORY_NBT_TAG, Constants.NBT.TAG_COMPOUND));
        }
    }

    public static boolean addToNBTInventory(NBTTagCompound playerPersistantTag, ItemStack stack, UUID ownerUUID)
    {
        NBTTagList tagList = playerPersistantTag.getTagList(INVENTORY_NBT_TAG, Constants.NBT.TAG_COMPOUND);
        boolean[] slotsTaken = new boolean[5 * 9];
        if (tagList.tagCount() < 5 * 9)
        {
            for (int i = 0; i < tagList.tagCount(); ++i)
            {
                NBTTagCompound itemTag = tagList.getCompoundTagAt(i);
                slotsTaken[itemTag.getByte("Slot")] = true;
            }
            for (int i = 0; i < 5 * 9; ++i)
            {
                if (!slotsTaken[i])
                {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    nbttagcompound.setByte("Slot", (byte) i);
                    stack.writeToNBT(nbttagcompound);
                    tagList.appendTag(nbttagcompound);
                    ContainerMailbox.updateMailCount(ownerUUID, tagList.tagCount());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onPickupSlot(EntityPlayer player, int index)
    {
        if (!player.worldObj.isRemote)
        {
            loadInventory(player);
            setInventorySlotContents(index, null);
            saveInventory(player);
        }
    }

    public void crunchInventory(EntityPlayer player)
    {
        loadInventory(player);
        int shift = 0;
        for (int i = 0; i < getSizeInventory(); ++i)
        {
            while (getStackInSlot(i + shift) == null && i + shift < getSizeInventory())
            {
                ++shift;
            }
            if (i + shift >= getSizeInventory())
            {
                setInventorySlotContents(i, null);
            }
            else
            {
                setInventorySlotContents(i, getStackInSlot(i + shift));
            }
        }
        saveInventory(player);
    }

}
