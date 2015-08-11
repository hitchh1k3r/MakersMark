package com.hitchh1k3rsguide.makersmark.containers;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GUIBag extends GuiContainer
{

    private static final ResourceLocation BAG_INVENTORY_BACKGROUND = new ResourceLocation(MakersMark.MODID, "textures/gui/bag.png");

    public static final int GUI_ID = 2;

    public GUIBag(ItemStack bag, EntityPlayer player)
    {
        super(new ContainerBag(bag, player));
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRendererObj.drawString(((ContainerBag) inventorySlots).getName(false), 8, 6, 0x404040);
        fontRendererObj.drawString(((ContainerBag) inventorySlots).getName(true), 8, ySize - 94 + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BAG_INVENTORY_BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

}
