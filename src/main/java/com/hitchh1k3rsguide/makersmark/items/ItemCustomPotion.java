package com.hitchh1k3rsguide.makersmark.items;

import com.google.common.collect.HashMultimap;
import com.hitchh1k3rsguide.makersmark.entities.EntityCustomPotion;
import com.hitchh1k3rsguide.makersmark.potions.MakersPotion;
import com.hitchh1k3rsguide.makersmark.sided.IMakersBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public class ItemCustomPotion extends ItemPotion implements IMakersBase
{

    private final MakersPotion[] potionTypes;
    private final String         name;

    public static final int DRINKABLE = 1 << 13, THROWABLE = 1 << 14, LEVEL2 = 1 << 5, EXTENDED = 1 << 6;

    @SuppressWarnings("rawtypes")
    private       HashMap         effectCache = new HashMap();
    public static Map<Item, Item> ingredients = new WeakHashMap<Item, Item>();


    public ItemCustomPotion(String name, MakersPotion[] potions, Item[] ingredients)
    {
        this.setUnlocalizedName(getUnlocalizedNameRaw());
        this.setCreativeTab(CreativeTabs.tabBrewing);
        this.potionTypes = potions;
        this.name = name;
        for (int i = 0; i < ingredients.length; ++i)
        {
            if (i < potions.length)
            {
                Item ingredient = ingredients[i];
                ItemCustomPotion.ingredients.put(ingredient, this);
                ingredient.setPotionEffect(getPotionEffectString(potions[i]));
                ingredient.setCreativeTab(CreativeTabs.tabBrewing);
            }
        }
    }

    public static boolean isIngredient(Item item)
    {
        return ingredients.containsKey(item);
    }

    @Override
    public String getUnlocalizedNameRaw()
    {
        return name;
    }

    @Override
    public String getTextureName()
    {
        return "minecraft:potion";
    }

    @Override
    public void commonRegister()
    {
        GameRegistry.registerItem(this, getUnlocalizedNameRaw());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientRegister()
    {
        ModelBakery.addVariantName(this, "minecraft:bottle_splash", "minecraft:bottle_drinkable");

        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, new ItemMeshDefinition()
        {
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return ItemPotion.isSplash(stack.getMetadata()) ? new ModelResourceLocation("minecraft:bottle_splash", "inventory") : new ModelResourceLocation("minecraft:bottle_drinkable", "inventory");
            }
        });
    }

    @Override
    public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining)
    {
        return Items.potionitem.getModel(stack, player, useRemaining);
    }

    @Override
    @SideOnly(Side.SERVER)
    public void serverRegister()
    {
    }

    public String getPotionEffectString(Potion potion)
    {
        for (int i = 0; i < 15 && i < potionTypes.length; ++i)
        {
            if (potion == potionTypes[i])
            {
                ++i;
                return ((i & 1) > 0 ? "+" : "-") + "0" + ((i & 2) > 0 ? "+" : "-") + "1" + ((i & 4) > 0 ? "+" : "-") + "2" + ((i & 8) > 0 ? "+" : "-") + "3&4-4+13";
            }
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List getEffects(ItemStack itemStack)
    {
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("CustomPotionEffects", 9))
        {
            ArrayList arraylist = new ArrayList();
            NBTTagList nbttaglist = itemStack.getTagCompound().getTagList("CustomPotionEffects", 10);

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttagcompound);

                if (potioneffect != null)
                {
                    arraylist.add(potioneffect);
                }
            }

            return arraylist;
        }
        else
        {
            List list = (List) this.effectCache.get(Integer.valueOf(itemStack.getItemDamage()));

            if (list == null)
            {
                list = getPotionEffects(itemStack.getItemDamage());
                this.effectCache.put(itemStack.getItemDamage(), list);
            }

            return list;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List getEffects(int meta)
    {
        List list = (List) this.effectCache.get(Integer.valueOf(meta));

        if (list == null)
        {
            list = getPotionEffects(meta);
            this.effectCache.put(meta, list);
        }

        return list;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getPotionEffects(int meta)
    {
        int potionID = (meta & 15) - 1;
        if (potionID < 0 || potionID >= potionTypes.length)
        {
            return null;
        }
        MakersPotion potion = potionTypes[potionID];
        if (potion == null)
        {
            return null;
        }

        ArrayList output = new ArrayList();

        int duration = potion.getDuration(meta);
        int amplifier = potion.getAmplifier(meta);

        PotionEffect potioneffect = new PotionEffect(potion.getId(), duration, amplifier);

        if ((meta & THROWABLE) != 0)
        {
            potioneffect.setSplashPotion(true);
        }

        output.add(potioneffect);

        return output;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromDamage(int meta)
    {
        int potionID = (meta & 15) - 1;
        if (potionID < 0 || potionID >= potionTypes.length)
        {
            return super.getColorFromDamage(meta);
        }
        Potion potion = potionTypes[potionID];
        if (potion == null)
        {
            return 0xFFFFFF;
        }
        return potion.getLiquidColor();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String getItemStackDisplayName(ItemStack itemStack)
    {
        if (itemStack.getItemDamage() == 0)
        {
            return StatCollector.translateToLocal("item.emptyPotion.name").trim();
        }
        else
        {
            String prefix = "";

            if (isSplash(itemStack.getItemDamage()))
            {
                prefix = StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
            }

            List effects = getEffects(itemStack);
            String suffix;

            if (effects != null && !effects.isEmpty())
            {
                suffix = ((PotionEffect) effects.get(0)).getEffectName();
                suffix = suffix + ".postfix";
                return prefix + StatCollector.translateToLocal(suffix).trim();
            }
            else
            {
                suffix = PotionHelper.getPotionPrefix(itemStack.getItemDamage());
                return StatCollector.translateToLocal(suffix).trim() + " " + StatCollector.translateToLocal("item.potion.name").trim();
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List infoList, boolean p_77624_4_)
    {
        if (itemStack.getItemDamage() != 0)
        {
            List effects = getEffects(itemStack);
            HashMultimap hashmultimap = HashMultimap.create();
            Iterator iterator1;

            if (effects != null && !effects.isEmpty())
            {
                iterator1 = effects.iterator();

                while (iterator1.hasNext())
                {
                    PotionEffect potioneffect = (PotionEffect) iterator1.next();
                    String s1 = StatCollector.translateToLocal(potioneffect.getEffectName()).trim();
                    Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                    Map map = potion.getAttributeModifierMap();

                    if (map != null && map.size() > 0 && (!(potion instanceof MakersPotion) || ((MakersPotion) potion).showModifiers()))
                    {
                        Iterator iterator = map.entrySet().iterator();

                        while (iterator.hasNext())
                        {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();
                            AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                            hashmultimap.put(((IAttribute) entry.getKey()).getAttributeUnlocalizedName(), attributemodifier1);
                        }
                    }

                    if (potioneffect.getAmplifier() > 0)
                    {
                        s1 = s1
                             + " "
                             + StatCollector.translateToLocal(
                                "potion.potency." + potioneffect.getAmplifier()).trim();
                    }

                    if (potioneffect.getDuration() > 20)
                    {
                        s1 = s1 + " (" + Potion.getDurationString(potioneffect) + ")";
                    }

                    if (potion.isBadEffect())
                    {
                        infoList.add(EnumChatFormatting.RED + s1);
                    }
                    else
                    {
                        infoList.add(EnumChatFormatting.GRAY + s1);
                    }
                }
            }
            else
            {
                String s = StatCollector.translateToLocal("potion.empty").trim();
                infoList.add(EnumChatFormatting.GRAY + s);
            }

            if (!hashmultimap.isEmpty())
            {
                infoList.add("");
                infoList.add(EnumChatFormatting.DARK_PURPLE
                             + StatCollector.translateToLocal("potion.effects.whenDrank"));
                iterator1 = hashmultimap.entries().iterator();

                while (iterator1.hasNext())
                {
                    Map.Entry entry1 = (Map.Entry) iterator1.next();
                    AttributeModifier attributemodifier2 = (AttributeModifier) entry1.getValue();
                    double d0 = attributemodifier2.getAmount();
                    double d1;

                    if (attributemodifier2.getOperation() != 1
                        && attributemodifier2.getOperation() != 2)
                    {
                        d1 = attributemodifier2.getAmount();
                    }
                    else
                    {
                        d1 = attributemodifier2.getAmount() * 100.0D;
                    }

                    if (d0 > 0.0D)
                    {
                        infoList.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), StatCollector.translateToLocal("attribute.name." + entry1.getKey())));
                    }
                    else if (d0 < 0.0D)
                    {
                        d1 *= -1.0D;
                        infoList.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), StatCollector.translateToLocal("attribute.name." + entry1.getKey())));
                    }
                }
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List outList)
    {
        for (int type = 0; type < 15 && type < potionTypes.length; ++type)
        {
            outList.add(new ItemStack(item, 1, type + 1 | DRINKABLE));
            if (potionTypes[type].isUpgradeable())
            {
                outList.add(new ItemStack(item, 1, type + 1 | DRINKABLE | LEVEL2));
            }
            if (potionTypes[type].isExtendable())
            {
                outList.add(new ItemStack(item, 1, type + 1 | DRINKABLE | EXTENDED));
            }
            outList.add(new ItemStack(item, 1, type + 1 | THROWABLE));
            if (potionTypes[type].isUpgradeable())
            {
                outList.add(new ItemStack(item, 1, type + 1 | THROWABLE | LEVEL2));
            }
            if (potionTypes[type].isExtendable())
            {
                outList.add(new ItemStack(item, 1, type + 1 | THROWABLE | EXTENDED));
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        if (isSplash(itemStack.getItemDamage()))
        {
            if (!player.capabilities.isCreativeMode)
            {
                --itemStack.stackSize;
            }

            world.playSoundAtEntity(player, "random.bow", 0.5F,
                                    0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

            if (!world.isRemote)
            {
                Entity entity = new EntityCustomPotion(world, player, itemStack);
                world.spawnEntityInWorld(entity);
                ((EntityCustomPotion) entity).setPotionStack(itemStack);
            }

            return itemStack;
        }
        else
        {
            player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
            return itemStack;
        }
    }

}