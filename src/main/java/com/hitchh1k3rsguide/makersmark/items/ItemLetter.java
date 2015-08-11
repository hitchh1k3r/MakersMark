package com.hitchh1k3rsguide.makersmark.items;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.containers.GUILetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemLetter extends MakersBaseItem
{

    public static final String TAG_SIGNED  = MakersMark.MODID + ".signed";
    public static final String TAG_MESSAGE = MakersMark.MODID + ".message";
    public static final String TAG_STACK   = MakersMark.MODID + ".stack";
    public static final String TAG_TAKEN   = MakersMark.MODID + ".taken";
    public static final String TAG_SENDER  = MakersMark.MODID + ".sender";
    public static final String TAG_TITLE   = MakersMark.MODID + ".title";

    public ItemLetter()
    {
        super();
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            player.openGui(MakersMark.instance, GUILetter.GUI_ID, world, 0, 0, 0);
        }
        return itemStack;
    }

    @Override
    public String getUnlocalizedNameRaw()
    {
        return "letter";
    }

    @Override
    public void commonRegister()
    {
        super.commonRegister();

        GameRegistry.addShapelessRecipe(new ItemStack(this), new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage()), Items.paper);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().getBoolean(TAG_SIGNED))
        {
            return new ModelResourceLocation(getTextureName(), "inventory");
        }
        else
        {
            return new ModelResourceLocation(getTextureName() + "_open", "inventory");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientRegister()
    {
        ModelBakery.addVariantName(this, getTextureName(), getTextureName() + "_open");

        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return getModel(stack, null, 0);
            }
        });
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound nbttagcompound = stack.getTagCompound();
            String s = nbttagcompound.getString(TAG_SENDER);

            if (!StringUtils.isNullOrEmpty(s))
            {
                tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted(MakersMark.MODID + ".letter.byAuthor", s));
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound nbttagcompound = stack.getTagCompound();
            String s = nbttagcompound.getString(TAG_TITLE);

            if (!StringUtils.isNullOrEmpty(s))
            {
                return s;
            }
        }

        return super.getItemStackDisplayName(stack);
    }

}
