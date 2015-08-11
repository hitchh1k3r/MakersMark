package com.hitchh1k3rsguide.makersmark.potions;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.items.ItemCustomPotion;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionLevitation extends MakersPotion
{

    public PotionLevitation(int id)
    {
        super(id, MakersMark.MODID + ":levitation", false, 0xE5E78C);
        this.setPotionName("potion." + MakersMark.MODID + ".levitation");
        this.setIconIndex(0, 0);
        this.registerPotionAttributeModifier(SharedMonsterAttributes.movementSpeed, "0bb8d008-0e43-4acb-88cc-22df2cba854d", 2.0D, 2);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier)
    {
        if (entity instanceof EntityPlayer)
        {
            if (((EntityPlayer) entity).capabilities.isFlying)
            {
                return;
            }
        }

        AxisAlignedBB bb = entity.getEntityBoundingBox();

        if (bb == null)
        {
            return;
        }

        double height = (amplifier + 1) * 5;
        if (height < 1)
        {
            height = 1;
        }
        else if (height > 20)
        {
            height = 20;
        }
        PotionEffect effect = entity.getActivePotionEffect(this);
        if (effect.getDuration() < 100)
        {
            height *= (effect.getDuration() / 100.0F);
        }

        bb = new AxisAlignedBB(bb.minX + 0.1D, bb.minY - height, bb.minZ + 0.1D, bb.maxX - 0.1D, bb.maxY - 0.1D, bb.maxZ - 0.1D);

        boolean hoverNeeded = (entity.worldObj.getCollidingBoundingBoxes(entity, bb).size() > 0 || entity.worldObj.isAnyLiquid(bb));
        for (int i = 0; i < ((entity instanceof EntityPig && ((EntityPig) entity).getAIControlledByPlayer().shouldExecute()) ? 2 : 1); ++i)
        {
            if (!entity.isSneaking() && hoverNeeded)
            {
                entity.motionY += 0.1;
            }
            else if (entity.motionY < 0)
            {
                if (hoverNeeded)
                {
                    entity.motionY *= 0.85;
                }
                else if (entity.isSneaking())
                {
                    entity.motionY *= 0.95;
                }
                else
                {
                    entity.motionY *= 0.9;
                }
            }
        }
        entity.fallDistance = 0;
    }

    @Override
    public boolean isReady(int duration, int amplifier)
    {
        return true;
    }

    @Override
    public boolean isBadEffect()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex()
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(MyPotions.icons);
        return super.getStatusIconIndex();
    }

    @Override
    public boolean isExtendable()
    {
        return true;
    }

    @Override
    public boolean isUpgradeable()
    {
        return true;
    }

    @Override
    public int getDuration(int meta)
    {
        return (meta & ItemCustomPotion.EXTENDED) != 0 ? 90 * 20 : 45 * 20;
    }

    @Override
    public int getAmplifier(int meta)
    {
        return (meta & ItemCustomPotion.LEVEL2) != 0 ? 1 : 0;
    }

    @Override
    public boolean showModifiers()
    {
        return false;
    }

    @Override
    public double getAttributeModifierAmount(int amplifier, AttributeModifier modifier)
    {
        return modifier.getAmount();
    }

}
