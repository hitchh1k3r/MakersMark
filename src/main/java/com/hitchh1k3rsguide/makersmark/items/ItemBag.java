package com.hitchh1k3rsguide.makersmark.items;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.containers.GUIBag;
import com.hitchh1k3rsguide.makersmark.containers.InventoryPickup;
import com.hitchh1k3rsguide.makersmark.items.crafting.CraftingBag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.List;
import java.util.Random;

public class ItemBag extends MakersBaseItem
{

    public static final String TAG_INVENTORY = MakersMark.MODID + ".inventory";
    public static final String TAG_LOOT      = MakersMark.MODID + ".loot";

    public ItemBag()
    {
        super();
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            player.openGui(MakersMark.instance, GUIBag.GUI_ID, world, 0, 0, 0);
            itemStack = new ItemStack(Items.leather);
        }
        return itemStack;
    }

    @Override
    public String getUnlocalizedNameRaw()
    {
        return "bag";
    }

    @Override
    public void commonRegister()
    {
        super.commonRegister();

        GameRegistry.addRecipe(new CraftingBag());
        RecipeSorter.register(MakersMark.MODID + ":bag_crafting", CraftingBag.class, RecipeSorter.Category.SHAPED, "after:minecraft:bookcloning");

        ChestGenHooks.addItem(ChestGenHooks.BONUS_CHEST, new WeightedRandomChestContent(getStackForLoot(ChestGenHooks.BONUS_CHEST), 1, 2, 2));
        ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(getStackForLoot(ChestGenHooks.DUNGEON_CHEST), 1, 2, 2));
        ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(getStackForLoot(ChestGenHooks.MINESHAFT_CORRIDOR), 1, 2, 2));
        ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(getStackForLoot(ChestGenHooks.PYRAMID_DESERT_CHEST), 1, 2, 2));
        ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(getStackForLoot(ChestGenHooks.PYRAMID_JUNGLE_CHEST), 1, 2, 2));
        ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_DISPENSER, new WeightedRandomChestContent(getStackForLoot(ChestGenHooks.PYRAMID_JUNGLE_DISPENSER), 1, 2, 2));
        ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CORRIDOR, new WeightedRandomChestContent(getStackForLoot(ChestGenHooks.STRONGHOLD_CORRIDOR), 1, 2, 2));
        ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CROSSING, new WeightedRandomChestContent(getStackForLoot(ChestGenHooks.STRONGHOLD_CROSSING), 1, 2, 2));
        ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_LIBRARY, new WeightedRandomChestContent(getStackForLoot(ChestGenHooks.STRONGHOLD_LIBRARY), 1, 2, 2));
        ChestGenHooks.addItem(ChestGenHooks.VILLAGE_BLACKSMITH, new WeightedRandomChestContent(getStackForLoot(ChestGenHooks.VILLAGE_BLACKSMITH), 1, 2, 2));
    }

    private ItemStack getStackForLoot(String category)
    {
        ItemStack stack = new ItemStack(this);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(TAG_LOOT, category);
        stack.setTagCompound(tag);
        return stack;
    }

    public static void setInventory(InventoryPickup inventory, ItemStack bag)
    {
        NBTTagCompound tag = bag.getTagCompound();
        String loot = ChestGenHooks.BONUS_CHEST;
        if (tag != null)
        {
            if (tag.hasKey(TAG_INVENTORY))
            {
                inventory.loadInventoryFromNBT(tag.getTagList(TAG_INVENTORY, Constants.NBT.TAG_COMPOUND));
                loot = null;
            }
            else
            {
                if (tag.hasKey(TAG_LOOT))
                {
                    loot = tag.getString(TAG_LOOT);
                }
            }
        }

        if (loot != null)
        {
            Random rand = new Random();
            if (rand.nextDouble() > 0.5D)
            {
                inventory.setInventorySlotContents(0, nonBagLoot(loot, rand));
            }
            else if (rand.nextDouble() > 0.7D)
            {
                inventory.setInventorySlotContents(0, nonBagLoot(ChestGenHooks.MINESHAFT_CORRIDOR, rand));
            }
            else if (rand.nextDouble() > 0.7D)
            {
                inventory.setInventorySlotContents(0, nonBagLoot(ChestGenHooks.STRONGHOLD_CORRIDOR, rand));
            }
            else if (rand.nextDouble() > 0.7D)
            {
                inventory.setInventorySlotContents(0, nonBagLoot(ChestGenHooks.PYRAMID_JUNGLE_CHEST, rand));
            }
            inventory.setInventorySlotContents(1, nonBagLoot(loot, rand));
            if (rand.nextDouble() > 0.5D)
            {
                inventory.setInventorySlotContents(2, nonBagLoot(loot, rand));
            }
            else if (rand.nextDouble() > 0.7D)
            {
                inventory.setInventorySlotContents(2, nonBagLoot(ChestGenHooks.VILLAGE_BLACKSMITH, rand));
            }
            else if (rand.nextDouble() > 0.7D)
            {
                inventory.setInventorySlotContents(2, nonBagLoot(ChestGenHooks.PYRAMID_DESERT_CHEST, rand));
            }
            else if (rand.nextDouble() > 0.7D)
            {
                inventory.setInventorySlotContents(2, nonBagLoot(ChestGenHooks.DUNGEON_CHEST, rand));
            }
        }
    }

    private static ItemStack nonBagLoot(String category, Random rand)
    {
        int overflow = 20;
        ItemStack stack = ChestGenHooks.getOneItem(category, rand);
        while (stack.getItem() == MakersMark.getItems().bag && overflow > 0)
        {
            --overflow;
            stack = ChestGenHooks.getOneItem(category, rand);
        }
        return stack;
    }

    public static void generateContents(EntityPlayer player)
    {
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == MakersMark.getItems().bag)
            {
                if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(TAG_INVENTORY))
                {
                    InventoryPickup temp = new InventoryPickup("", false, 3);
                    setInventory(temp, stack);
                    ItemStack newBag = CraftingBag.getItemStack(temp);
                    Slot slot = player.openContainer.getSlotFromInventory(player.inventory, i);
                    player.inventory.setInventorySlotContents(i, newBag);
                    ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(player.openContainer.windowId, slot.slotNumber, newBag));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void addInventoryToTooltip(ItemStack stack, List tooltip)
    {
        InventoryPickup temp = new InventoryPickup("", false, 3);
        ItemBag.setInventory(temp, stack);
        if (temp.getStackInSlot(0) != null)
        {
            tooltip.add(EnumChatFormatting.GRAY + temp.getStackInSlot(0).getDisplayName() + ((temp.getStackInSlot(0).stackSize > 1) ? " x" + temp.getStackInSlot(0).stackSize : ""));
        }
        if (temp.getStackInSlot(1) != null)
        {
            tooltip.add(EnumChatFormatting.GRAY + temp.getStackInSlot(1).getDisplayName() + ((temp.getStackInSlot(1).stackSize > 1) ? " x" + temp.getStackInSlot(1).stackSize : ""));
        }
        if (temp.getStackInSlot(2) != null)
        {
            tooltip.add(EnumChatFormatting.GRAY + temp.getStackInSlot(2).getDisplayName() + ((temp.getStackInSlot(2).stackSize > 1) ? " x" + temp.getStackInSlot(2).stackSize : ""));
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiContainer)
        {
            GuiContainer craftScreen = (GuiContainer) Minecraft.getMinecraft().currentScreen;
            if (craftScreen.inventorySlots != null && craftScreen.inventorySlots.inventorySlots != null)
            {
                for (Object slot : craftScreen.inventorySlots.inventorySlots)
                {
                    if (slot instanceof SlotCrafting)
                    {
                        if (stack == ((SlotCrafting) slot).getStack())
                        {
                            addInventoryToTooltip(stack, tooltip);
                        }
                        break;
                    }
                }
            }
        }
    }

}
