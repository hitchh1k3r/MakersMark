package com.hitchh1k3rsguide.makersmark.items;

import com.google.common.base.Function;
import com.hitchh1k3rsguide.makersmark.blocks.BlockMailbox;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemMailbox extends MakersBaseItemBlock
{

    private final Block                       theBlock;
    private final Function<ItemStack, String> nameFunction;

    public ItemMailbox(Block block)
    {
        super(block);
        this.theBlock = block;
        this.nameFunction = new Function<ItemStack, String>()
        {

            public String apply(ItemStack stack)
            {
                return BlockMailbox.getWood(stack).name;
            }

        };
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName() + "." + nameFunction.apply(stack);
    }

    @Override
    public String getUnlocalizedNameRaw()
    {
        return "mailbox";
    }

    @Override
    public String getTextureName()
    {
        return "";
    }

    @Override
    public int getMetadata(int metadata)
    {
        return metadata;
    }

}
