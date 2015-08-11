package com.hitchh1k3rsguide.makersmark.blocks;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.sided.IMakersBase;
import net.minecraft.block.Block;

import java.util.ArrayList;

public class MyBlocks
{

    public Block mailBox;
    private ArrayList<IMakersBase> blocks = new ArrayList<IMakersBase>();

    public void initialize()
    {
        mailBox = new BlockMailbox();

        blocks.add((IMakersBase) mailBox);
    }

    public void registerBlocks()
    {
        for (IMakersBase block : blocks)
        {
            MakersMark.instance.proxy.registerBlock(block);
        }
    }

    public void registerOres()
    {
        // Register any ore dictionary names here:
    }

}
