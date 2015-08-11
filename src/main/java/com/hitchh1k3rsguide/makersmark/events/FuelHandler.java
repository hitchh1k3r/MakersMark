package com.hitchh1k3rsguide.makersmark.events;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;

public class FuelHandler implements IFuelHandler
{

    @Override
    public int getBurnTime(ItemStack fuel)
    {
        if (fuel.getItem() == MakersMark.getItems().coin && fuel.getItemDamage() == 1)
        {
            return 200;
        }
        return 0;
    }

}
