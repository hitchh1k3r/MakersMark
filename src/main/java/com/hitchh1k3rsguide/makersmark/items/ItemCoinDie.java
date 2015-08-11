package com.hitchh1k3rsguide.makersmark.items;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.items.crafting.CraftingDieSigning;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.*;

public class ItemCoinDie extends MakersBaseItem
{

    public ItemCoinDie()
    {
        super();
        setMaxStackSize(1);
        setMaxDamage(64);
        setNoRepair();
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public String getUnlocalizedNameRaw()
    {
        return "die";
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubItems(Item itemIn, CreativeTabs tab, java.util.List list)
    {
        for (int p = 0; p < ItemCoin.SHAPES.length; ++p)
        {
            ItemStack stack = new ItemStack(itemIn);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(ItemCoin.TAG_SHAPE, p);
            stack.setTagCompound(tag);
            list.add(stack);
        }
    }

    @Override
    public void commonRegister()
    {
        super.commonRegister();

        GameRegistry.addRecipe(new CraftingDieSigning());
        RecipeSorter.register(MakersMark.MODID + ":die_signing", CraftingDieSigning.class, RecipeSorter.Category.SHAPED, "");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientRegister()
    {
        String[] names = new String[(ItemCoin.SHAPES.length)];
        int i = 0;
        for (String pattern : ItemCoin.SHAPES)
        {
            names[i] = MakersMark.MODID + ":die_" + pattern;
            ++i;
        }
        ModelBakery.addVariantName(this, names);

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
    public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag.hasKey(ItemCoin.TAG_SHAPE))
            {
                int p = tag.getInteger(ItemCoin.TAG_SHAPE);
                if (p >= 0 && p < ItemCoin.SHAPES.length)
                {
                    return new ModelResourceLocation(MakersMark.MODID + ":die_" + ItemCoin.SHAPES[p], "inventory");
                }
            }
        }
        return new ModelResourceLocation(getTextureName() + "_A", "inventory");
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced)
    {
        if (stack.hasTagCompound())
        {
            String newName = playerIn.getName();
            NBTTagCompound nbttagcompound = stack.getTagCompound();
            NBTTagList list = nbttagcompound.getTagList(ItemCoin.TAG_MINTERS, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); ++i)
            {
                GameProfile profile = NBTUtil.readGameProfileFromNBT(list.getCompoundTagAt(i));
                if (newName.equals(profile.getName()))
                {
                    tooltip.add(EnumChatFormatting.YELLOW + profile.getName());
                }
                else
                {
                    tooltip.add(EnumChatFormatting.GRAY + profile.getName());
                }
            }
        }
    }

    public static void addPlayer(ItemStack stack, EntityPlayer player)
    {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null)
        {
            tag = new NBTTagCompound();
        }
        NBTTagList list = tag.getTagList(ItemCoin.TAG_MINTERS, Constants.NBT.TAG_COMPOUND);
        Map<String, GameProfile> profiles = new HashMap<String, GameProfile>();
        GameProfile profile = player.getGameProfile();
        boolean isNew = true;
        if (list != null)
        {
            for (int q = 0; q < list.tagCount(); ++q)
            {
                GameProfile coinProfile = NBTUtil.readGameProfileFromNBT(list.getCompoundTagAt(q));
                if (coinProfile != null)
                {
                    if (coinProfile.getName().equals(profile.getName()))
                    {
                        isNew = false;
                    }
                    else if (!profiles.containsKey(coinProfile.getName()))
                    {
                        profiles.put(coinProfile.getName(), coinProfile);
                    }
                }
            }
        }
        if (isNew)
        {
            profiles.put(profile.getName(), profile);
            List<String> names = new ArrayList<String>(profiles.keySet());
            Collections.sort(names);
            list = new NBTTagList();
            for (String name : names)
            {
                NBTTagCompound profileTag = new NBTTagCompound();
                NBTUtil.writeGameProfile(profileTag, profiles.get(name));
                list.appendTag(profileTag);
            }
        }
        tag.setTag(ItemCoin.TAG_MINTERS, list);
        stack.setTagCompound(tag);
    }

}
