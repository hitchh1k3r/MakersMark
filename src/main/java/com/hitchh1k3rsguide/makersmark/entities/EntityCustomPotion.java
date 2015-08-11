package com.hitchh1k3rsguide.makersmark.entities;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.network.MessageAuxEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.Iterator;
import java.util.List;

public class EntityCustomPotion extends EntityThrowable
{

    private ItemStack potionStack;

    public EntityCustomPotion(World world)
    {
        super(world);
    }

    public EntityCustomPotion(World world, EntityLivingBase entity, ItemStack itemStack)
    {
        super(world, entity);
        this.potionStack = itemStack;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        if (potionStack != null)
        {
            dataWatcher.addObject(6, potionStack);
        }
        else
        {
            dataWatcher.addObject(6, new ItemStack(Items.potionitem));
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void onImpact(MovingObjectPosition collision)
    {
        if (!this.worldObj.isRemote)
        {
            if (potionStack != null && potionStack.getItem() instanceof ItemPotion)
            {
                List effects = ((ItemPotion) potionStack.getItem()).getEffects(this.potionStack);

                if (effects != null && !effects.isEmpty())
                {
                    AxisAlignedBB effectRange = new AxisAlignedBB(posX - 4.0D, posY - 2.0D, posZ - 4.0D, posX + 4.0D, posY + 2.0D, posZ + 4.0D);
                    List entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, effectRange);

                    if (entities != null && !entities.isEmpty())
                    {
                        Iterator entity_it = entities.iterator();

                        while (entity_it.hasNext())
                        {
                            EntityLivingBase entity = (EntityLivingBase) entity_it.next();
                            double distance = this.getDistanceSqToEntity(entity);

                            if (distance < 16.0D)
                            {
                                double effectiveness = 1.0D - Math.sqrt(distance) / 4.0D;

                                if (entity == collision.entityHit)
                                {
                                    effectiveness = 1.0D;
                                }

                                Iterator effect_it = effects.iterator();

                                while (effect_it.hasNext())
                                {
                                    PotionEffect effect = (PotionEffect) effect_it.next();
                                    int effectID = effect.getPotionID();

                                    if (Potion.potionTypes[effectID].isInstant())
                                    {
                                        Potion.potionTypes[effectID].affectEntity(this, this.getThrower(), entity, effect.getAmplifier(), effectiveness);
                                    }
                                    else
                                    {
                                        int duration = (int) (effectiveness * effect.getDuration() + 0.5D);

                                        if (duration > 20)
                                        {
                                            entity.addPotionEffect(new PotionEffect(effectID, duration, effect.getAmplifier()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            MessageAuxEvent message = new MessageAuxEvent(MessageAuxEvent.POTION_SPLASH, (int) Math.round(posX), (int) Math.round(posY), (int) Math.round(posZ), ((Item.getIdFromItem(potionStack.getItem()) << 4) + (potionStack.getItemDamage() & 15)));
            NetworkRegistry.TargetPoint target = new NetworkRegistry.TargetPoint(worldObj.provider.getDimensionId(), posX, posY, posZ, 16);
            MakersMark.getNetwork().sendToAllAround(message, target);
            this.setDead();
        }
    }

    public void setPotionStack(ItemStack newStack)
    {
        potionStack = newStack;
        dataWatcher.updateObject(6, potionStack);
    }

    public ItemStack getPotionStack()
    {
        if (potionStack == null)
        {
            potionStack = dataWatcher.getWatchableObjectItemStack(6);
            if (potionStack == null)
            {
                return null;
            }
        }
        return potionStack;
    }

    @Override
    protected float getGravityVelocity()
    {
        return 0.05F;
    }

    @Override
    protected float getInaccuracy()
    {
        return -20.0F;
    }

    @Override
    protected float getVelocity()
    {
        return 0.5F;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        super.readEntityFromNBT(tag);

        if (tag.hasKey("Potion", Constants.NBT.TAG_COMPOUND))
        {
            this.potionStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Potion"));
        }

        if (this.getPotionStack() == null)
        {
            this.setDead();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag)
    {
        super.writeEntityToNBT(tag);

        if (this.getPotionStack() != null)
        {
            tag.setTag("Potion", this.potionStack.writeToNBT(new NBTTagCompound()));
        }
    }

}