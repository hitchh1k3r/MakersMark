package com.hitchh1k3rsguide.makersmark.network;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.items.ItemCustomPotion;
import com.hitchh1k3rsguide.makersmark.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Method;

public class MessageAuxEvent implements IMessage
{

    public static final int POTION_SPLASH = 0;

    private int type;
    private int x, y, z, data;

    @SideOnly(Side.CLIENT)
    private static Method METHOD_spawnEntityFX;
    private boolean initialized = false;

    public MessageAuxEvent()
    {
    }

    public MessageAuxEvent(int type, int x, int y, int z, int data)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.data = data;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(type);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(data);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        type = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        data = buf.readInt();
    }

    @SideOnly(Side.CLIENT)
    private void doWorldAuxEvent()
    {
        if (!initialized)
        {
            initialized = true;
            METHOD_spawnEntityFX = Utils.getMethod(RenderGlobal.class, "spawnEntityFX", "func_174974_b", Integer.TYPE, Boolean.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, int[].class);
        }

        World world = Minecraft.getMinecraft().theWorld;
        if (type == POTION_SPLASH)
        {
            int i;
            for (i = 0; i < 8; ++i)
            {
                world.spawnParticle(EnumParticleTypes.ITEM_CRACK, (double) x, (double) y, (double) z, world.rand.nextGaussian() * 0.15D, world.rand.nextDouble() * 0.2D, world.rand.nextGaussian() * 0.15D, Item.getIdFromItem(MakersMark.getItems().potion), data | ItemCustomPotion.THROWABLE);
            }

            int color = ((ItemCustomPotion) MakersMark.getItems().potion).getColorFromDamage(data);
            float red = (float) (color >> 16 & 255) / 255.0F;
            float green = (float) (color >> 8 & 255) / 255.0F;
            float blue = (float) (color >> 0 & 255) / 255.0F;
            EnumParticleTypes enumparticletypes = EnumParticleTypes.SPELL;

            if (((ItemCustomPotion) MakersMark.getItems().potion).isEffectInstant(data))
            {
                enumparticletypes = EnumParticleTypes.SPELL_INSTANT;
            }

            for (i = 0; i < 100; ++i)
            {
                double mag = world.rand.nextDouble() * 4.0D;
                double dir = world.rand.nextDouble() * Math.PI * 2.0D;
                double dX = Math.cos(dir) * mag;
                double dY = 0.01D + world.rand.nextDouble() * 0.5D;
                double dZ = Math.sin(dir) * mag;
                try
                {
                    // "func_179344_e" will be called "getShouldIgnoreRange"
                    EntityFX entityfx = (EntityFX) METHOD_spawnEntityFX.invoke(Minecraft.getMinecraft().renderGlobal, enumparticletypes.getParticleID(), enumparticletypes.func_179344_e(), x + dX * 0.1D, y + 0.3D, z + dZ * 0.1D, dX, dY, dZ, new int[0]);
                    if (entityfx != null)
                    {
                        float intensity = 0.75F + world.rand.nextFloat() * 0.25F;
                        entityfx.setRBGColorF(red * intensity, green * intensity, blue * intensity);
                        entityfx.multiplyVelocity((float) mag);
                    }
                }
                catch (Exception ignored)
                {
                }
            }
            world.playSound(this.x + 0.5D, this.y + 0.5D, this.z + 0.5D, "game.potion.smash", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F, false);
        }
    }

    public static class Handler implements IMessageHandler<MessageAuxEvent, IMessage>
    {

        @Override
        public IMessage onMessage(MessageAuxEvent message, MessageContext ctx)
        {
            message.doWorldAuxEvent();
            return null;
        }

    }

}