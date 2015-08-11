package com.hitchh1k3rsguide.makersmark.events;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.blocks.BlockMailbox;
import com.hitchh1k3rsguide.makersmark.blocks.tileentity.TEMailbox;
import com.hitchh1k3rsguide.makersmark.items.ItemCoin;
import com.hitchh1k3rsguide.makersmark.items.ItemCustomPotion;
import com.hitchh1k3rsguide.makersmark.network.MessageServerSettings;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.PotionHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.List;
import java.util.Random;

public class CommonEvents
{

    private Random rand = new Random();

    @SubscribeEvent
    public void playJoinEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            MakersMark.getNetwork().sendTo(new MessageServerSettings(), (EntityPlayerMP) event.player);
        }
    }

    long lastAnvil = 0L;

    @SubscribeEvent
    public void onCrafted(PlayerEvent.ItemCraftedEvent event)
    {
        for (int i = 0; i < event.craftMatrix.getSizeInventory(); ++i)
        {
            ItemStack stack = event.craftMatrix.getStackInSlot(i);
            if (stack != null && stack.getItem() == MakersMark.getItems().mintersMallet && event.crafting.getItem() != MakersMark.getItems().bag)
            {
                if (event.player != null && event.player.worldObj.isRemote && event.player.worldObj.getTotalWorldTime() > lastAnvil + 30)
                {
                    lastAnvil = event.player.worldObj.getTotalWorldTime();
                    event.player.playSound("random.anvil_use", 1.0F, rand.nextFloat() * 0.1F + 0.9F);
                }
                stack.setItemDamage(stack.getItemDamage() + 1);
                if (stack.getItemDamage() < stack.getMaxDamage())
                {
                    ++stack.stackSize;
                }
            }
        }
        if (event.crafting.getItem() == MakersMark.getItems().coin || event.crafting.getItem() == MakersMark.getItems().coinDie || event.crafting.getItem() == MakersMark.getItems().makersMark)
        {
            for (int i = 0; i < event.craftMatrix.getSizeInventory(); ++i)
            {
                ItemStack stack = event.craftMatrix.getStackInSlot(i);
                if (stack != null)
                {
                    if (stack.getItem() instanceof ItemSword || (event.crafting.getItem() == MakersMark.getItems().coin && stack.getItem() == MakersMark.getItems().coinDie))
                    {
                        stack.setItemDamage(stack.getItemDamage() + 1);
                        if (stack.getItemDamage() < stack.getMaxDamage())
                        {
                            ++stack.stackSize;
                        }
                    }
                }
            }
        }
    }

    public static final String TAG_NEEDSSIGNATURE = MakersMark.MODID + ".needsSignature";

    @SubscribeEvent
    public void anvilRecipes(AnvilUpdateEvent event)
    {
        if (event.left != null && event.right != null && event.right.getItem() == MakersMark.getItems().makersMark)
        {
            if (event.left.getMaxStackSize() == 1 && event.left.getItem() != MakersMark.getItems().makersMark && event.left.getItem() != MakersMark.getItems().bag)
            {
                NBTTagCompound tag = event.left.getTagCompound();
                if (tag == null)
                {
                    tag = new NBTTagCompound();
                }
                else
                {
                    tag = (NBTTagCompound) tag.copy();
                }
                if (!tag.hasKey(ItemCoin.TAG_MINTERS))
                {
                    event.output = event.left.copy();
                    int baseCost = event.output.getRepairCost();
                    event.cost = baseCost + 1;
                    if (!event.name.isEmpty())
                    {
                        NBTTagCompound display = tag.getCompoundTag("display");
                        if (display == null)
                        {
                            display = new NBTTagCompound();
                        }
                        display.setString("Name", event.name);
                        tag.setTag("display", display);
                    }
                    else if (tag.hasKey("display") && tag.getCompoundTag("display").hasKey("Name"))
                    {
                        NBTTagCompound display = tag.getCompoundTag("display");
                        display.removeTag("Name");
                        tag.setTag("display", display);
                    }
                    tag.setBoolean(TAG_NEEDSSIGNATURE, true);
                    event.output.setTagCompound(tag);
                    event.output.setRepairCost(baseCost * 2 + 1);
                    event.materialCost = 1;
                }
            }
        }
    }

    @SubscribeEvent
    public void anvilCrafting(AnvilRepairEvent event)
    {
        if (event.left != null && event.right != null &&
            (event.left.getItem() == MakersMark.getItems().makersMark || event.right.getItem() == MakersMark.getItems().makersMark)
            && event.entityPlayer != null)
        {
            ///////////////////////
            // NOTE (hitch) This is to deal with a bug in Forge where left and right are reversed in this event: (and should work if it's fixed in the future)
            ItemStack mark;
            if (event.left.getItem() == MakersMark.getItems().makersMark)
            {
                mark = event.left;
            }
            else
            {
                mark = event.right;
            }
            ///////////////////////
            mark.stackSize++;
            mark.setItemDamage(mark.getItemDamage() + 1);
            if (mark.getItemDamage() >= mark.getMaxDamage())
            {
                mark.stackSize--;
            }
            ItemStack stack;
            for (int i = -1; i < event.entityPlayer.inventory.getSizeInventory(); ++i)
            {
                if (i == -1)
                {
                    stack = event.entityPlayer.inventory.getItemStack();
                }
                else
                {
                    stack = event.entityPlayer.inventory.getStackInSlot(i);
                }
                if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_NEEDSSIGNATURE))
                {
                    NBTTagCompound tag = stack.getTagCompound();
                    tag.removeTag(TAG_NEEDSSIGNATURE);
                    NBTTagCompound playerTag = new NBTTagCompound();
                    NBTUtil.writeGameProfile(playerTag, event.entityPlayer.getGameProfile());
                    tag.setTag(ItemCoin.TAG_MINTERS, playerTag);
                }
            }
        }
    }

    @SubscribeEvent
    public void potionRecipes(PotionBrewEvent.Pre event)
    {
        if (event.getItem(3) != null)
        {
            boolean corruption = false;
            if (event.getItem(3).getItem() == Items.fermented_spider_eye)
            {
                for (int i = 0; i < 3; ++i)
                {
                    if (event.getItem(i) != null && event.getItem(i).getItem() == MakersMark.getItems().potion)
                    {
                        final int LEVITATION = 1;
                        final int SLOWNESS = 10;

                        ItemStack stack = event.getItem(i);
                        int meta = stack.getItemDamage();
                        int effect = meta & 15;
                        if (effect == LEVITATION)
                        {
                            stack.setItem(Items.potionitem);
                            stack.setItemDamage(meta - effect + SLOWNESS);
                            event.setItem(i, stack);
                            corruption = true;
                        }
                    }
                }
            }
            if (ItemCustomPotion.ingredients.containsKey(event.getItem(3).getItem()) || corruption)
            {
                Item potion = ItemCustomPotion.ingredients.get(event.getItem(3).getItem());
                if (potion == null)
                {
                    potion = Items.potionitem;
                }

                for (int i = 0; i < 3; ++i)
                {
                    if (event.getItem(i) != null && event.getItem(i).getItem() instanceof ItemPotion && (!corruption || event.getItem(i).getItem() != MakersMark.getItems().potion))
                    {
                        ItemStack stack = event.getItem(i);
                        int j = stack.getMetadata();
                        int k = this.potionModifiers(j, event.getItem(3));

                        List list = ((ItemPotion) potion).getEffects(j);
                        List list1 = ((ItemPotion) potion).getEffects(k);

                        if (((j <= 0 || list != list1) && (list == null || !list.equals(list1) && list1 != null) && j != k) ||
                            !ItemPotion.isSplash(j) && ItemPotion.isSplash(k))
                        {
                            stack.setItem(potion);
                            stack.setItemDamage(k);
                            event.setItem(i, stack);
                        }
                    }
                }

                --event.getItem(3).stackSize;
                if (event.getItem(3).stackSize <= 0)
                {
                    event.setItem(3, null);
                }
                event.setCanceled(true);
            }
        }
    }

    private int potionModifiers(int meta, ItemStack ingredient)
    {
        return ingredient == null ? meta : (ingredient.getItem().isPotionIngredient(ingredient) ? PotionHelper.applyIngredient(meta, ingredient.getItem().getPotionEffect(ingredient)) : meta);
    }

    @SubscribeEvent
    public void breakBlock(BlockEvent.BreakEvent event)
    {
        if (event.state.getBlock() == MakersMark.getBlocks().mailBox)
        {
            TileEntity te = event.world.getTileEntity(event.pos);
            if (te != null && te instanceof TEMailbox)
            {
                TEMailbox mailBox = (TEMailbox) te;
                BlockMailbox.breakingColor = mailBox.color;
            }
        }
    }

}
