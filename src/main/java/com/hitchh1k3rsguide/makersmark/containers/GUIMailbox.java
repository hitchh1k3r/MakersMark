package com.hitchh1k3rsguide.makersmark.containers;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.network.MessageGUIButton;
import com.hitchh1k3rsguide.makersmark.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GUIMailbox extends GuiContainer
{

    private static final ResourceLocation MAILBOX_INVENTORY_BACKGROUND = new ResourceLocation(MakersMark.MODID, "textures/gui/mailbox.png");

    public static final int GUI_ID = 0;

    private static String[]  usernames  = new String[0];
    private static boolean[] active     = new boolean[0];
    private static boolean   restricted = true;

    private static int nameIndex = 0;

    private BlockPos position;

    public GUIMailbox(TileEntity te, EntityPlayer player)
    {
        super(new ContainerMailbox(te, player, GUIMailbox.restricted));
        position = te.getPos();
        ySize = 221;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRendererObj.drawString(((ContainerMailbox) inventorySlots).getName(false), 8, 6, 0x404040);
        fontRendererObj.drawString(((ContainerMailbox) inventorySlots).getName(true), 8, ySize - 94 + 2, 0x404040);
        if (GUIMailbox.nameIndex >= 0 && usernames.length > GUIMailbox.nameIndex)
        {
            String s = Utils.limitRenderString(fontRendererObj, usernames[GUIMailbox.nameIndex], 82);
            fontRendererObj.drawString(s, 57, 24, 0x303030);
            fontRendererObj.drawString(s, 56, 23, (active[GUIMailbox.nameIndex] ? 0xFFFFFF : 0x606060));
        }
        int guiX = mouseX - guiLeft;
        int guiY = mouseY - guiTop;
        if (isReady() && guiX > 151 && guiX < 151 + 18 && guiY > 17 && guiY < 17 + 18)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.colorMask(true, true, true, false);
            drawGradientRect(152, 18, 152 + 16, 18 + 16, 0x40FFFFFF, 0x40FFFFFF);
            GlStateManager.colorMask(true, true, true, true);
        }
        if (GUIMailbox.restricted)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.colorMask(true, true, true, false);
            for (int j = 0; j < 5; ++j)
            {
                for (int k = 0; k < 9; ++k)
                {
                    drawGradientRect(8 + k * 18, 37 + j * 18, 16 + 8 + k * 18, 16 + 37 + j * 18, 0x80000000, 0x80000000);
                }
            }
            GlStateManager.colorMask(true, true, true, true);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(MAILBOX_INVENTORY_BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        if (isReady())
        {
            drawTexturedModalRect(guiLeft + 151, guiTop + 17, xSize, 0, 18, 18);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int guiX = mouseX - guiLeft;
        int guiY = mouseY - guiTop;

        if (guiX > 45 && guiX < 52 && guiY > 21 && guiY < 32 && usernames.length > 0)
        {
            buttonSound();
            GUIMailbox.nameIndex = (GUIMailbox.nameIndex + (usernames.length - 1)) % usernames.length;
        }
        else if (guiX > 142 && guiX < 149 && guiY > 21 && guiY < 32 && usernames.length > 0)
        {
            buttonSound();
            GUIMailbox.nameIndex = (GUIMailbox.nameIndex + 1) % usernames.length;
        }
        else if (isReady() && guiX > 151 && guiX < 151 + 18 && guiY > 17 && guiY < 17 + 18 && usernames.length > 0)
        {
            buttonSound();
            MakersMark.getNetwork().sendToServer(new MessageGUIButton(GUIMailbox.nameIndex));
        }
    }

    private boolean isReady()
    {
        return active.length > GUIMailbox.nameIndex && active[GUIMailbox.nameIndex] && ((ContainerMailbox) inventorySlots).isReady();
    }

    private void buttonSound()
    {
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public static void setUsernames(String[] usernames, boolean[] active, int selected, boolean restricted)
    {
        GUIMailbox.usernames = usernames;
        GUIMailbox.active = active;
        GUIMailbox.restricted = restricted;
        GUIMailbox.nameIndex = selected;
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (Minecraft.getMinecraft().theWorld.getBlockState(position).getBlock() != MakersMark.getBlocks().mailBox || Minecraft.getMinecraft().thePlayer.getDistanceSq(position) > 64.0D)
        {
            this.mc.thePlayer.closeScreen();
        }
    }

}
