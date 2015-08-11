package com.hitchh1k3rsguide.makersmark.containers;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.blocks.tileentity.TEMailbox;
import com.hitchh1k3rsguide.makersmark.config.MakersConfig;
import com.hitchh1k3rsguide.makersmark.items.ItemBag;
import com.hitchh1k3rsguide.makersmark.items.ItemInspectorsGlass;
import com.hitchh1k3rsguide.makersmark.network.MessageMailPlayerList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.util.UUID;

public class GUIHandler implements IGuiHandler
{

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == GUIInspection.GUI_ID)
        {
            ItemBag.generateContents(player);
            return null;
        }
        else if (ID == GUILetter.GUI_ID)
        {
            if (player.getHeldItem().getItem() == MakersMark.getItems().letter)
            {
                return new ContainerLetter(player.getHeldItem(), player);
            }
        }
        else if (ID == GUIBag.GUI_ID)
        {
            if (player.getHeldItem().getItem() == MakersMark.getItems().bag)
            {
                return new ContainerBag(player.getHeldItem(), player);
            }
        }
        else
        {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te != null)
            {
                if (ID == GUIMailbox.GUI_ID && te instanceof TEMailbox)
                {
                    UUID[] ids = ContainerMailbox.getUUIDs(player);
                    String[] names = new String[ids.length];
                    boolean[] active = new boolean[ids.length];
                    int selected = 0;
                    int i = 0;
                    boolean restricted = (!FMLCommonHandler.instance().getSide().isClient() && ((TEMailbox) te).owner != null && !((TEMailbox) te).owner.getId().equals(player.getGameProfile().getId()));
                    for (UUID uuid : ids)
                    {
                        names[i] = UsernameCache.getLastKnownUsername(uuid);
                        if (((TEMailbox) te).owner == null)
                        {
                            if (uuid.equals(player.getGameProfile().getId()))
                            {
                                selected = i;
                            }
                        }
                        else
                        {
                            if (uuid.equals(((TEMailbox) te).owner.getId()))
                            {
                                selected = i;
                            }
                        }
                        active[i] = ContainerMailbox.getMailCount(uuid) < 5 * 9;
                        ++i;
                    }
                    ContainerMailbox container = new ContainerMailbox(te, player, restricted);
                    container.setUsers(ids);
                    MakersMark.getNetwork().sendTo(new MessageMailPlayerList(names, active, selected, restricted), (EntityPlayerMP) player);
                    return container;
                }
            }
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == GUILetter.GUI_ID)
        {
            if (player.getHeldItem() != null && player.getHeldItem().getItem() == MakersMark.getItems().letter)
            {
                return new GUILetter(player.getHeldItem(), player);
            }
        }
        else if (ID == GUIBag.GUI_ID)
        {
            if (player.getHeldItem() != null && player.getHeldItem().getItem() == MakersMark.getItems().bag)
            {
                return new GUIBag(player.getHeldItem(), player);
            }
        }
        else if (ID == GUIInspection.GUI_ID)
        {
            if (player.getHeldItem() != null)
            {
                if (MakersConfig.ServerConfig.limitedInspection && player.getHeldItem().getItem() instanceof ItemInspectorsGlass)
                {
                    return new GUIInspection(GUIInspection.Mode.LIMITED, player);
                }
                else if (player.getHeldItem().getItem() == MakersMark.getItems().inspectorsGlass)
                {
                    return new GUIInspection(GUIInspection.Mode.NORMAL, player);
                }
                else if (player.getHeldItem().getItem() == MakersMark.getItems().advancedGlass)
                {
                    return new GUIInspection(GUIInspection.Mode.ADVANCED, player);
                }
            }
        }
        else
        {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

            if (te != null)
            {
                if (ID == GUIMailbox.GUI_ID)
                {
                    return new GUIMailbox(te, player);
                }
            }
        }

        return null;
    }

}
