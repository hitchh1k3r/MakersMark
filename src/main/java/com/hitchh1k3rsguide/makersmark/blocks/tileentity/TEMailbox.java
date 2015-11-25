package com.hitchh1k3rsguide.makersmark.blocks.tileentity;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.blocks.BlockMailbox;
import com.hitchh1k3rsguide.makersmark.graphics.IMakersRenderingTE;
import com.hitchh1k3rsguide.makersmark.items.ItemCoin;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.opengl.GL11;

public class TEMailbox extends TileEntity implements IMakersRenderingTE
{

    public static final String TAG_COLOR = MakersMark.MODID + ".color";
    public static final String TAG_OWNER = MakersMark.MODID + ".owner";
    public static final String TAG_WOOD  = MakersMark.MODID + ".wood";
    public static final String TAG_NAME  = MakersMark.MODID + ".name";

    public GameProfile owner;
    public int         color;
    public static int[] textColors = new int[]{ 0x222222, 0xB42222, 0x22B422, 0x6B6B22, 0x2222B4, 0xB422B4,
                                                0x226BB4, 0xB4B4B4, 0x6B6B6B, 0xFF6B6B, 0xB4FF6B, 0xFFFF6B,
                                                0x6B6BFF, 0xFF6BFF, 0xFFB422, 0xFFFFFF };
    private String                      mailboxCustomName;
    private ItemCoin.MaterialDefinition wood;

    public TEMailbox()
    {
    }

    public ItemCoin.MaterialDefinition getWood()
    {
        return (wood != null ? wood : BlockMailbox.getDefaultWood());
    }

    public void setWood(String name)
    {
        if (name != null)
        {
            wood = ItemCoin.woods.get(name);
        }
        else
        {
            wood = BlockMailbox.getDefaultWood();
        }
    }

    public String getName()
    {
        return this.hasCustomName() ? mailboxCustomName : "container." + MakersMark.MODID + ".mailbox";
    }

    public boolean hasCustomName()
    {
        return mailboxCustomName != null && mailboxCustomName.length() > 0;
    }

    public void setCustomInventoryName(String p_145951_1_)
    {
        mailboxCustomName = p_145951_1_;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setByte(TAG_COLOR, (byte) (this.color & 15));

        if (this.owner != null)
        {
            NBTTagCompound ownerCompound = new NBTTagCompound();
            NBTUtil.writeGameProfile(ownerCompound, this.owner);
            tag.setTag(TAG_OWNER, ownerCompound);
        }

        if (wood != null)
        {
            tag.setString(TAG_WOOD, wood.name);
        }

        if (this.hasCustomName())
        {
            tag.setString(TAG_NAME, mailboxCustomName);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        this.color = tag.getByte(TAG_COLOR);
        this.owner = NBTUtil.readGameProfileFromNBT(tag.getCompoundTag(TAG_OWNER));
        this.wood = ItemCoin.woods.get(tag.getString(TAG_WOOD));
        if (worldObj != null && worldObj.isRemote)
        {
            worldObj.markBlockForUpdate(pos);
        }

        if (tag.hasKey(TAG_NAME, Constants.NBT.TAG_STRING))
        {
            mailboxCustomName = tag.getString(TAG_NAME);
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(pos, 4, nbttagcompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double renderX, double renderY, double renderZ, float partialTick, int blockDamage, TileEntitySpecialRenderer renderer)
    {
        IBlockState state = worldObj.getBlockState(pos);
        if (state.getBlock() == MakersMark.getBlocks().mailBox)
        {
            FontRenderer fontRenderer = renderer.getFontRenderer();
            String name;
            if (hasCustomName())
            {
                name = getName();
            }
            else if (owner != null)
            {
                name = owner.getName();
            }
            else
            {
                name = StatCollector.translateToLocal(getName());
            }
            float textHover = 0.001f;
            float scale = 0.5625f / Math.max(fontRenderer.getStringWidth(name), 14);
            float offset = 0.40625f + textHover;
            if (state.getProperties().containsKey(BlockMailbox.FACING))
            {
                EnumFacing facing = (EnumFacing) state.getValue(BlockMailbox.FACING);
                // NORTH:
                int rot = 0;
                float xOff = 0;
                float zOff = -1;
                if (facing == EnumFacing.EAST)
                {
                    rot = 1;
                    xOff = 1;
                    zOff = 0;
                }
                else if (facing == EnumFacing.SOUTH)
                {
                    rot = 2;
                    xOff = 0;
                    zOff = 1;
                }
                else if (facing == EnumFacing.WEST)
                {
                    rot = 3;
                    xOff = -1;
                    zOff = 0;
                }

                GlStateManager.enableRescaleNormal();
                GlStateManager.pushMatrix();
                GlStateManager.translate(xOff * offset, 0, zOff * offset);
                GlStateManager.translate((float) renderX + 0.5F, (float) renderY + 0.5F - 0.03125f, (float) renderZ + 0.5F);
                GlStateManager.scale(scale, scale, scale);
                GL11.glNormal3f(0.0F, 0.0F, 1.0F);
                GL11.glRotatef(180, 0, 0, 1);
                GL11.glRotatef(90 * rot, 0, 1, 0);
                GlStateManager.depthMask(false);
                fontRenderer.drawString(name, -fontRenderer.getStringWidth(name) / 2, 0, textColors[this.color]);
                GlStateManager.depthMask(true);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.popMatrix();
            }
        }
    }

    public int getColor()
    {
        return textColors[color];
    }
}
