package com.hitchh1k3rsguide.makersmark.items.crafting;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.items.ItemCoin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.World;

public class CraftingCoin implements IRecipe
{

    ItemStack output = null;

    @Override
    public boolean matches(InventoryCrafting inventory, World world)
    {
        return getCraftingResult(inventory) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
    {
        boolean overflow = false;

        boolean hasMallet = false;
        String metal = null;
        String wood = null;
        ItemStack die = null;

        int toolX = -1, toolY = -1, materialX = -1, materialY = -1, dieX = -1, dieY = -1;

        output = null;
        for (int x = 0; x < inventory.getWidth(); ++x)
        {
            for (int y = 0; y < inventory.getHeight(); ++y)
            {
                ItemStack stack = inventory.getStackInRowAndColumn(x, y);
                if (stack != null)
                {
                    if (stack.getItem() == MakersMark.getItems().mintersMallet && !hasMallet && toolX == -1 && toolY == -1 && wood == null)
                    {
                        toolX = x;
                        toolY = y;
                        hasMallet = true;
                    }
                    else if (stack.getItem() == MakersMark.getItems().coinDie && die == null && wood == null)
                    {
                        dieX = x;
                        dieY = y;
                        die = stack;
                    }
                    else if (stack.getItem() == MakersMark.getItems().coin && stack.getItemDamage() == 0 && metal == null && wood == null)
                    {
                        NBTTagCompound tag = stack.getTagCompound();
                        if (tag != null && !tag.hasKey(ItemCoin.TAG_SHAPE) && tag.hasKey(ItemCoin.TAG_MATERIAL))
                        {
                            materialX = x;
                            materialY = y;
                            metal = tag.getString(ItemCoin.TAG_MATERIAL);
                        }
                        else
                        {
                            overflow = true;
                            break;
                        }
                    }
                    else if (stack.getItem() instanceof ItemSword && toolX == -1 && toolY == -1 && metal == null && die == null && !hasMallet)
                    {
                        toolX = x;
                        toolY = y;
                    }
                    else if (wood == null && getWood(stack) != null && metal == null && die == null && !hasMallet)
                    {
                        materialX = x;
                        materialY = y;
                        wood = getWood(stack);
                    }
                    else
                    {
                        overflow = true;
                        break;
                    }
                }
            }
        }
        if (!overflow)
        {
            if (hasMallet && metal != null && die != null && wood == null)
            {
                int pX = dieX - materialX;
                int pY = dieY - materialY;
                int vX = toolX - materialX;
                int vY = toolY - materialY;
                if (vX >= -1 && vX <= 1 && vY >= -1 && vY <= 1 && pX >= -1 && pX <= 1 && pY >= -1 && pY <= 1)
                {
                    output = new ItemStack(MakersMark.getItems().coin);
                    NBTTagCompound tag = new NBTTagCompound();
                    tag.setString(ItemCoin.TAG_MATERIAL, metal);
                    NBTTagCompound dieTag = die.getTagCompound();
                    if (dieTag != null)
                    {
                        tag.setInteger(ItemCoin.TAG_SHAPE, dieTag.getInteger(ItemCoin.TAG_SHAPE));
                        if (dieTag.hasKey(ItemCoin.TAG_MINTERS))
                        {
                            tag.setTag(ItemCoin.TAG_MINTERS, dieTag.getTag(ItemCoin.TAG_MINTERS));
                        }
                        if (dieTag.hasKey("display") && dieTag.getCompoundTag("display").hasKey("Name"))
                        {
                            tag.setString(ItemCoin.TAG_NAME, dieTag.getCompoundTag("display").getString("Name"));
                        }
                    }

                    int p = ((pY + 1) * 3) + (pX + 1);
                    if (p > 4)
                    {
                        --p;
                    }
                    int v = ((vY + 1) * 3) + (vX + 1);
                    if (v > 4)
                    {
                        --v;
                    }

                    v = (v - p);
                    if (v < 0)
                    {
                        v += 7;
                    }
                    else
                    {
                        v += 6;
                    }
                    v %= 7;
                    tag.setInteger(ItemCoin.TAG_STYLE, p);
                    tag.setInteger(ItemCoin.TAG_PATTERN, v);
                    output.setTagCompound(tag);
                }
            }
            else if (toolX != -1 && toolY != -1 && wood != null && !hasMallet && metal == null)
            {
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
                    int dX = toolX - materialX;
                    int dY = toolY - materialY;
                    if (dX >= -1 && dX <= 1 && dY >= -1 && dY <= 1)
                    {
                        int p = ((dY + 1) * 3) + (dX + 1);
                        if (p > 4)
                        {
                            --p;
                        }
                        output = new ItemStack(MakersMark.getItems().coin, 1, 1);
                        NBTTagCompound tag = new NBTTagCompound();
                        tag.setString(ItemCoin.TAG_MATERIAL, wood);
                        tag.setInteger(ItemCoin.TAG_SHAPE, p);
                        NBTTagCompound playerTag = new NBTTagCompound();
                        NBTUtil.writeGameProfile(playerTag, player.getGameProfile());
                        tag.setTag(ItemCoin.TAG_MINTERS, playerTag);
                        output.setTagCompound(tag);
                    }
                }
            }
        }
        return output;
    }

    public static String getWood(ItemStack stack)
    {
        for (ItemCoin.MaterialDefinition material : ItemCoin.woods.values())
        {
            for (ItemStack wood : material.variantStacks)
            {
                if (stack.getItem() == wood.getItem() && stack.getItemDamage() == wood.getItemDamage())
                {
                    return material.name;
                }
            }
        }
        return null;
    }

    @Override
    public int getRecipeSize()
    {
        return 3;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return output;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inventory)
    {
        return new ItemStack[inventory.getSizeInventory()];
    }

}
