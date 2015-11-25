package com.hitchh1k3rsguide.makersmark.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.ItemInfo;
import com.hitchh1k3rsguide.makersmark.MakersMark;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.*;

public class NEICommon
{

    public static ItemStack[] allItems  = null;
    public static ItemStack[] allTools  = null;
    public static ItemStack[] allSwords = null;

    public static void initLists()
    {
        if (allItems == null)
        {
            ArrayList<ItemStack> listAll = new ArrayList<ItemStack>();
            Iterator it = Item.itemRegistry.iterator();
            while (it.hasNext())
            {
                Item item = (Item) it.next();
                List<ItemStack> listVars = ItemInfo.getItemOverrides(item);
                if (listVars.size() == 0)
                {
                    item.getSubItems(item, null, listVars);
                }
                listAll.addAll(listVars);
            }
            allItems = new ItemStack[listAll.size()];
            listAll.toArray(allItems);
        }
        if (allTools == null)
        {
            ArrayList<ItemStack> listAll = new ArrayList<ItemStack>();
            for (ItemStack iStack : allItems)
            {
                if (iStack.getMaxStackSize() == 1 && iStack.getItem() != MakersMark.getItems().makersMark && iStack.getItem() != MakersMark.getItems().bag)
                {
                    listAll.add(iStack);
                }
            }
            allTools = new ItemStack[listAll.size()];
            listAll.toArray(allTools);
        }
        if (allSwords == null)
        {
            ArrayList<ItemStack> listAll = new ArrayList<ItemStack>();
            for (ItemStack iStack : allItems)
            {
                if (iStack.getItem() instanceof ItemSword)
                {
                    listAll.add(iStack);
                }
            }
            Collections.sort(listAll, new Comparator<ItemStack>()
            {
                @Override
                public int compare(ItemStack o1, ItemStack o2)
                {
                    return o1.getMaxDamage() - o2.getMaxDamage();
                }
            });
            allSwords = new ItemStack[listAll.size()];
            listAll.toArray(allSwords);
        }
    }

    private static ItemStack lastItem = null;

    public static boolean randomRenderPermutation(PositionedStack stack, long cycle, int itemIndex)
    {
        if (stack instanceof PositionedPlaceholder)
        {
            Random rand = new Random((cycle / 5) + (itemIndex * 100));
            if (((PositionedPlaceholder) stack).type == PositionedPlaceholder.PlaceholderType.ANY_STACK_OPTIONAL && (rand.nextInt(5) > 3))
            {
                stack.item = null;
                lastItem = null;
                return true;
            }
            else if (((PositionedPlaceholder) stack).type == PositionedPlaceholder.PlaceholderType.ANY_STACK || ((PositionedPlaceholder) stack).type == PositionedPlaceholder.PlaceholderType.ANY_STACK_OPTIONAL)
            {
                ItemStack rStack = allItems[Math.abs(rand.nextInt()) % allItems.length];
                stack.item = rStack.copy();
                stack.item.stackSize = rand.nextInt(Math.max(rStack.getMaxStackSize(), 1)) + 1;
                lastItem = stack.item;
                return true;
            }
            else if (((PositionedPlaceholder) stack).type == PositionedPlaceholder.PlaceholderType.ANY_TOOL)
            {
                ItemStack rStack = allTools[Math.abs(rand.nextInt()) % allTools.length];
                stack.item = rStack.copy();
                lastItem = stack.item;
                return true;
            }
            else if (((PositionedPlaceholder) stack).type == PositionedPlaceholder.PlaceholderType.REPEAT_LAST_RAND)
            {
                stack.item = lastItem;
                return true;
            }
            else if (((PositionedPlaceholder) stack).type == PositionedPlaceholder.PlaceholderType.TAKES_DAMAGE)
            {
                int damage = (stack.item.getItemDamage() + ((stack.item.getMaxDamage() < 32) ? 1 : Math.max(4, stack.item.getMaxDamage() / 64)));
                if (damage > stack.item.getMaxDamage())
                {
                    damage = 0;
                    if (stack.items.length > 1)
                    {
                        ((PositionedPlaceholder) stack).switchIndex = (((PositionedPlaceholder) stack).switchIndex + 1) % stack.items.length;
                        stack.item = stack.items[((PositionedPlaceholder) stack).switchIndex].copy();
                    }
                }
                stack.item.setItemDamage(damage);
                return true;
            }
            else if (((PositionedPlaceholder) stack).type == PositionedPlaceholder.PlaceholderType.BASIC_CYCLE)
            {
                int index = (int) ((cycle / 20L) % (long) stack.items.length);
                stack.item = stack.items[index];
                return true;
            }
        }
        return false;
    }

    public static class PositionedPlaceholder extends PositionedStack
    {

        enum PlaceholderType
        {
            STANDARD, ANY_STACK, ANY_TOOL, ANY_STACK_OPTIONAL, REPEAT_LAST_RAND, TAKES_DAMAGE, BASIC_CYCLE
        }

        public final PlaceholderType type;
        public int switchIndex = 0;

        public PositionedPlaceholder(Object item, int x, int y, PlaceholderType type)
        {
            super((item == null ? new ItemStack(type == PlaceholderType.ANY_TOOL ? Items.stone_pickaxe : Items.apple) : item), x, y);
            this.type = type;
        }

    }

}
