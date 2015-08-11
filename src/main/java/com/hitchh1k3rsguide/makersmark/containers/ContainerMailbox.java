package com.hitchh1k3rsguide.makersmark.containers;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.blocks.tileentity.TEMailbox;
import com.hitchh1k3rsguide.makersmark.items.ItemLetter;
import com.hitchh1k3rsguide.makersmark.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.UUID;

public class ContainerMailbox extends Container implements IButtonReciver
{

    private static HashMap<UUID, Integer> mailCount = new HashMap<UUID, Integer>();

    private InventoryMailbox mailboxInventory;
    private IInventory       topInventory;
    private InventoryPlayer  playerInventory;
    private UUID[]           users;
    private EntityPlayer     player;
    private boolean          restricted;

    // public ContainerMailbox(TileEntity te, EntityPlayer player) { this(te, player, false); }

    public ContainerMailbox(TileEntity te, EntityPlayer player, boolean restricted)
    {
        topInventory = new InventoryBasic("", false, 2);
        mailboxInventory = new InventoryMailbox(((TEMailbox) te).getName(), ((TEMailbox) te).hasCustomName());
        if (!player.worldObj.isRemote)
        {
            mailboxInventory.loadInventory(player);
        }
        this.player = player;
        this.restricted = restricted;
        playerInventory = player.inventory;
        int j;
        int k;

        // Top Slots:
        addSlotToContainer(new SlotLimit(topInventory, 0, 8, 18, new SlotLimit.ValidityCheck()
        {
            @Override
            boolean isItemValid(ItemStack stack)
            {
                if (stack != null && stack.getItem() == MakersMark.getItems().letter)
                {
                    return !(stack.hasTagCompound() && stack.getTagCompound().getBoolean(ItemLetter.TAG_SIGNED));
                }
                return false;
            }
        }));
        addSlotToContainer(new SlotLimit(topInventory, 1, MakersMark.getItems().goldenFeather, 26, 18));

        // Player Inventory:
        for (j = 0; j < 3; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                addSlotToContainer(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 140 + j * 18));
            }
        }
        for (j = 0; j < 9; ++j)
        {
            addSlotToContainer(new Slot(playerInventory, j, 8 + j * 18, 198));
        }

        // Mailbox Inventory:
        if (!restricted)
        {
            for (j = 0; j < 5; ++j)
            {
                for (k = 0; k < 9; ++k)
                {
                    addSlotToContainer(new SlotPickup(mailboxInventory, k + j * 9, 8 + k * 18, 37 + j * 18));
                }
            }
        }
    }

    public static UUID[] getUUIDs(EntityPlayer player)
    {
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            return new UUID[]{ player.getGameProfile().getId() };
        }
        Object[] in = mailCount.keySet().toArray();
        UUID[] out = new UUID[in.length];
        for (int i = 0; i < in.length; ++i)
        {
            out[i] = (UUID) in[i];
        }
        return out;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return mailboxInventory.isUseableByPlayer(playerIn);
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

            if (index < 2)
            {
                if (!mergeItemStack(itemstack1, 2, 2 + (4 * 9), true))
                {
                    return null;
                }
            }
            else if (index < 2 + (4 * 9))
            {
                if (!((getSlot(0).isItemValid(itemstack1) && mergeItemStack(itemstack1, 0, 1, false)) ||
                      (getSlot(1).isItemValid(itemstack1) && mergeItemStack(itemstack1, 1, 2, false))))
                {
                    return null;
                }
            }
            else
            {
                if (!mergeItemStack(itemstack1, 2, 2 + (4 * 9), true))
                {
                    return null;
                }
                else
                {
                    mailboxInventory.onPickupSlot(playerIn, slot.getSlotIndex());
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
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);

        if (!player.worldObj.isRemote)
        {
            for (int i = 0; i < 2; ++i)
            {
                ItemStack itemstack = this.topInventory.getStackInSlotOnClosing(i);

                if (itemstack != null)
                {
                    player.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
            if (!restricted)
            {
                mailboxInventory.crunchInventory(player);
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
            return mailboxInventory.getDisplayName().getUnformattedText();
        }
    }

    @Override
    public void onButton(int id)
    {
        ItemStack stack = topInventory.getStackInSlot(0);
        ItemStack backup = stack.copy();
        NBTTagCompound tags = new NBTTagCompound();
        NBTTagCompound playerTag = Utils.getPlayerTag(users[id]);
        if (playerTag != null && topInventory.getStackInSlot(1) != null)
        {
            if (stack.getItem() instanceof ItemLetter)
            {
                NBTTagCompound tag = stack.getTagCompound();
                if (tag == null)
                {
                    tag = new NBTTagCompound();
                }
                tag.setBoolean(ItemLetter.TAG_SIGNED, true);
                tag.setString(ItemLetter.TAG_SENDER, player.getDisplayName().getUnformattedText());
                if (tag.hasKey("display") && tag.getCompoundTag("display").hasKey("Name"))
                {
                    NBTTagCompound display = tag.getCompoundTag("display");
                    tag.setString(ItemLetter.TAG_TITLE, display.getString("Name"));
                    display.removeTag("Name");
                    tag.setTag("display", display);
                }
                stack.setTagCompound(tag);
            }

            tags.setTag(EntityPlayer.PERSISTED_NBT_TAG, playerTag.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG));
            if (InventoryMailbox.addToNBTInventory(tags.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG), stack, users[id]))
            {
                Utils.putPlayerTag(users[id], tags);
                topInventory.setInventorySlotContents(0, null);
                topInventory.decrStackSize(1, 1);
            }
            else
            {
                topInventory.setInventorySlotContents(0, backup);
            }
        }
    }

    public boolean isReady()
    {
        return topInventory.getStackInSlot(0) != null && topInventory.getStackInSlot(1) != null;
    }

    public void setUsers(UUID[] users)
    {
        this.users = users;
    }

    public static void reloadMailCount()
    {
        mailCount = new HashMap<UUID, Integer>();
        for (File playerFile : Utils.PLAYER_FOLDER.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                // 11111111-2222-3333-4444-555555555555.dat
                return name.length() == 40 && name.endsWith(".dat");
            }
        }))
        {
            UUID uuid = UUID.fromString(playerFile.getName().substring(0, 36));
            NBTTagCompound tags = Utils.getPlayerTag(uuid);
            if (tags != null)
            {
                tags = tags.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
                if (tags.hasKey(InventoryMailbox.INVENTORY_NBT_TAG))
                {
                    mailCount.put(uuid, tags.getTagList(InventoryMailbox.INVENTORY_NBT_TAG, Constants.NBT.TAG_COMPOUND).tagCount());
                }
            }
        }
    }

    public static int getMailCount(UUID uuid)
    {
        Integer num = mailCount.get(uuid);
        if (num == null)
        {
            num = Integer.MAX_VALUE;
        }
        return num;
    }

    public static void updateMailCount(UUID uuid, int count)
    {
        mailCount.put(uuid, count);
    }

}
