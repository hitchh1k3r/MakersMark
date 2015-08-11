package com.hitchh1k3rsguide.makersmark.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotLimit extends Slot
{

    private Item          limit;
    private ValidityCheck checker;

    public SlotLimit(IInventory inventory, int index, Item limit, int x, int y)
    {
        super(inventory, index, x, y);
        this.limit = limit;
    }

    public SlotLimit(IInventory inventory, int index, int x, int y, ValidityCheck checker)
    {
        super(inventory, index, x, y);
        this.checker = checker;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        if (checker != null)
        {
            return checker.isItemValid(stack);
        }
        return stack.getItem().equals(limit);
    }

    abstract public static class ValidityCheck
    {

        abstract boolean isItemValid(ItemStack stack);

    }

}
