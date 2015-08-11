package com.hitchh1k3rsguide.makersmark.containers;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.items.ItemLetter;
import com.hitchh1k3rsguide.makersmark.network.MessageGUIString;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class ContainerLetter extends Container implements IStringReciever
{

    public boolean         inventoryOpen   = false;
    public boolean         signed          = false;
    public boolean         taken           = false;
    public String          message         = "";
    public ItemStack       itemStack       = null;
    public InventoryPlayer playerInventory = null;
    public InventoryLetter inventory       = null;

    private List basicInventory, fullInventory;
    public int invBlankX, invBlankY, invBlankID;

    public ContainerLetter(ItemStack letter, EntityPlayer player)
    {
        inventory = new InventoryLetter();
        NBTTagCompound tag = letter.getTagCompound();
        if (tag != null)
        {
            signed = tag.getBoolean(ItemLetter.TAG_SIGNED);
            taken = tag.getBoolean(ItemLetter.TAG_TAKEN);
            message = tag.getString(ItemLetter.TAG_MESSAGE);
            itemStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(ItemLetter.TAG_STACK));
            if (!taken)
            {
                inventory.setInventorySlotContents(0, itemStack);
            }
        }

        playerInventory = player.inventory;

        // Letter Inventory:
        if (!signed)
        {
            addSlotToContainer(new Slot(inventory, 0, 101, 145));
        }
        else
        {
            if (taken || itemStack == null)
            {
                addSlotToContainer(new SlotPickup(inventory, 0, -640, -640));
            }
            else
            {
                addSlotToContainer(new SlotPickup(inventory, 0, 122, 145));
            }
        }

        // Player Inventory:
        int j, k;
        for (j = 0; j < 3; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                addSlotToContainer(new Slot(playerInventory, k + j * 9 + 9, -640, -640));
            }
        }
        for (j = 0; j < 9; ++j)
        {
            if (letter.equals(playerInventory.getStackInSlot(j)))
            {
                invBlankX = 8 + j * 18;
                invBlankY = 94;
            }
            else
            {
                addSlotToContainer(new Slot(playerInventory, j, -640, -640));
            }
        }

        basicInventory = new ArrayList(inventorySlots);
        inventorySlots = new ArrayList();

        // Letter Inventory:
        if (!signed)
        {
            addSlotToContainer(new Slot(inventory, 0, 101, 145));
        }
        else
        {
            if (taken || itemStack == null)
            {
                addSlotToContainer(new SlotPickup(inventory, 0, -640, -640));
            }
            else
            {
                addSlotToContainer(new SlotPickup(inventory, 0, 122, 145));
            }
        }

        // Player Inventory:
        for (j = 0; j < 3; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                addSlotToContainer(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 36 + j * 18));
            }
        }
        for (j = 0; j < 9; ++j)
        {
            if (letter.equals(playerInventory.getStackInSlot(j)))
            {
                invBlankID = j;
                invBlankX = 8 + j * 18;
                invBlankY = 94;
            }
            else
            {
                addSlotToContainer(new Slot(playerInventory, j, 8 + j * 18, 94));
            }
        }

        fullInventory = new ArrayList(inventorySlots);

        if (player.worldObj.isRemote)
        {
            inventorySlots = basicInventory;
        }

        // 8, 36
        // 8, 94
        // 101, 145
        // 122, 145
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    public void setInventoryShowing(boolean inventoryOpen)
    {
        this.inventoryOpen = inventoryOpen;
        if (inventoryOpen)
        {
            inventorySlots = fullInventory;
        }
        else
        {
            inventorySlots = basicInventory;
        }
    }

    public void onContainerClosed(EntityPlayer player)
    {
        if (player.worldObj.isRemote)
        {
            MakersMark.getNetwork().sendToServer(new MessageGUIString(message));
        }

        super.onContainerClosed(player);

        ItemStack letter = player.getHeldItem();
        if (letter != null && letter.getItem() == MakersMark.getItems().letter)
        {
            NBTTagCompound tag = letter.getTagCompound();
            if (tag == null)
            {
                tag = new NBTTagCompound();
            }
            if (!signed)
            {
                itemStack = inventory.getStackInSlot(0);
                NBTTagCompound itemTag = new NBTTagCompound();
                if (itemStack != null)
                {
                    itemStack.writeToNBT(itemTag);
                }
                tag.setTag(ItemLetter.TAG_STACK, itemTag);
                tag.setString(ItemLetter.TAG_MESSAGE, message);
            }
            else
            {
                tag.setBoolean(ItemLetter.TAG_TAKEN, taken);
                if (taken && inventory.getStackInSlot(0) != null)
                {
                    player.dropPlayerItemWithRandomChoice(inventory.getStackInSlot(0), false);
                }
            }
            letter.setTagCompound(tag);
        }
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

            if (index < 1)
            {
                if (!mergeItemStack(itemstack1, 1, inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else
            {
                if (!mergeItemStack(itemstack1, 0, 1, false))
                {
                    return null;
                }
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
    public void onString(String msg)
    {
        message = msg;
    }

    public void removeTakeSlots()
    {
        if (inventory.getStackInSlot(0) == null)
        {
            ((Slot) basicInventory.get(0)).xDisplayPosition = -640;
            ((Slot) basicInventory.get(0)).yDisplayPosition = -640;
            ((Slot) fullInventory.get(0)).xDisplayPosition = -640;
            ((Slot) fullInventory.get(0)).yDisplayPosition = -640;
        }
    }

}
