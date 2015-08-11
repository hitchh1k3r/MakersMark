package com.hitchh1k3rsguide.makersmark.containers;

import com.google.common.collect.Lists;
import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.items.ItemBag;
import com.hitchh1k3rsguide.makersmark.items.MyItems;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class GUIInspection extends GuiScreen
{

    public static final int GUI_ID = 3;

    private static final ResourceLocation INSPECTION_BACKGROUND = new ResourceLocation(MakersMark.MODID, "textures/gui/inspection.png");

    private int xSize, ySize;
    private int guiLeft, guiTop;
    private EntityPlayer owner;
    private int          selection;
    private int          midX, midY;
    private boolean moved = false;

    public enum Mode
    {
        NORMAL, ADVANCED, LIMITED
    }

    private Mode mode;

    public GUIInspection(Mode mode, EntityPlayer owner)
    {
        this.mode = mode;
        this.xSize = 176;
        this.ySize = 99;
        this.owner = owner;
        for (int i = 0; i < 9; ++i)
        {
            if (owner.getHeldItem() == owner.inventory.getStackInSlot(i))
            {
                selection = i;
            }
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        midX = guiLeft + 8 + (18 * selection) + 8;
        midY = guiTop + 76 + 8;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int xScale = scaledresolution.getScaledWidth();
        int yScale = scaledresolution.getScaledHeight();
        Mouse.setCursorPosition(midX * this.mc.displayWidth / xScale, midY * this.mc.displayHeight / yScale + 1);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (!moved)
        {
            if (Mouse.getDX() != 0 || Mouse.getDY() != 0)
            {
                moved = true;
            }
            else
            {
                mouseX = midX;
                mouseY = midY;
            }
        }

        this.drawDefaultBackground();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(INSPECTION_BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        for (int x = 0; x < 9; ++x)
        {
            ItemStack stack = owner.inventory.getStackInSlot(x);
            if (stack != null)
            {
                drawItemStack(stack, guiLeft + 8 + (18 * x), guiTop + 76);
            }

            if (selection == x)
            {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.colorMask(true, true, true, false);
                this.drawGradientRect(guiLeft + 8 + (18 * x), guiTop + 76, guiLeft + 8 + (18 * x) + 16, guiTop + 76 + 16, 0x80000000, 0x80000000);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
            else if (mouseX >= guiLeft + 7 + (18 * x) && mouseX < guiLeft + 7 + (18 * x) + 18 &&
                     mouseY >= guiTop + 75 && mouseY < guiTop + 75 + 18)
            {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.colorMask(true, true, true, false);
                this.drawGradientRect(guiLeft + 8 + (18 * x), guiTop + 76, guiLeft + 8 + (18 * x) + 16, guiTop + 76 + 16, 0x80FFFFFF, 0x80FFFFFF);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }
        for (int y = 0; y < 3; ++y)
        {
            for (int x = 0; x < 9; ++x)
            {
                ItemStack stack = owner.inventory.getStackInSlot(x + (y * 9) + 9);
                if (stack != null)
                {
                    drawItemStack(stack, guiLeft + 8 + (18 * x), guiTop + 18 + (18 * y));
                }

                if (mouseX >= guiLeft + 7 + (18 * x) && mouseX < guiLeft + 7 + (18 * x) + 18 &&
                    mouseY >= guiTop + 17 + (18 * y) && mouseY < guiTop + 17 + (18 * y) + 18)
                {
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    GlStateManager.colorMask(true, true, true, false);
                    this.drawGradientRect(guiLeft + 8 + (18 * x), guiTop + 18 + (18 * y), guiLeft + 8 + (18 * x) + 16, guiTop + 18 + (18 * y) + 16, 0x80FFFFFF, 0x80FFFFFF);
                    GlStateManager.colorMask(true, true, true, true);
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                }
            }
        }

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.colorMask(true, true, true, false);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;
        this.itemRender.renderItemAndEffectIntoGUI(owner.getHeldItem(), mouseX - 8, mouseY - 8);
        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;

        fontRendererObj.drawString(("" + StatCollector.translateToLocal("container." + MakersMark.MODID + ".inspection")).trim(), guiLeft + 8, guiTop + 6, 0x404040);

        for (int x = 0; x < 9; ++x)
        {
            if (selection != x && mouseX >= guiLeft + 7 + (18 * x) && mouseX < guiLeft + 7 + (18 * x) + 18 &&
                mouseY >= guiTop + 75 && mouseY < guiTop + 75 + 18)
            {
                renderToolTip(owner.inventory.getStackInSlot(x), mouseX, mouseY);
            }
        }
        for (int y = 0; y < 3; ++y)
        {
            for (int x = 0; x < 9; ++x)
            {
                if (mouseX >= guiLeft + 7 + (18 * x) && mouseX < guiLeft + 7 + (18 * x) + 18 &&
                    mouseY >= guiTop + 17 + (18 * y) && mouseY < guiTop + 17 + (18 * y) + 18)
                {
                    renderToolTip(owner.inventory.getStackInSlot(x + (y * 9) + 9), mouseX, mouseY);
                }
            }
        }

        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    private void drawItemStack(ItemStack stack, int x, int y)
    {
        if (stack != null)
        {
            FontRenderer font = stack.getItem().getFontRenderer(stack);
            if (font == null)
            {
                font = fontRendererObj;
            }
            this.zLevel = 200.0F;
            this.itemRender.zLevel = 200.0F;
            this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
            String s = "";
            if (stack.stackSize != 1)
            {
                s = "" + (stack.stackSize < 1 ? EnumChatFormatting.RED : "") + stack.stackSize;
            }
            this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, s);
            this.zLevel = 0.0F;
            this.itemRender.zLevel = 0.0F;
        }
    }

    @Override
    protected void renderToolTip(ItemStack stack, int x, int y)
    {
        if (stack != null)
        {
            ArrayList<String> list = Lists.newArrayList();
            String s = stack.getDisplayName();

            if (stack.hasDisplayName())
            {
                s = EnumChatFormatting.ITALIC + s;
            }

            s = s + EnumChatFormatting.RESET;
            if (!stack.hasDisplayName() && stack.getItem() == Items.filled_map)
            {
                s = s + " #" + stack.getItemDamage();
            }
            list.add(stack.getRarity().rarityColor + s);

            if (mode == Mode.NORMAL || mode == Mode.LIMITED)
            {
                if (mode == Mode.LIMITED && stack.getItem() == MakersMark.getItems().coin)
                {
                    list.add((EnumChatFormatting.DARK_BLUE + StatCollector.translateToLocal("makersmark.report.limited.coin")).trim());
                }
                else
                {
                    List<String> markers = MyItems.getMarkers(stack);
                    if (markers.contains(owner.getName()))
                    {
                        list.add((EnumChatFormatting.DARK_GREEN + StatCollector.translateToLocal("makersmark.report.basic.yes")).trim());
                    }
                    else
                    {
                        list.add((EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("makersmark.report.basic.no")).trim());
                    }
                }
            }
            else if (mode == Mode.ADVANCED)
            {
                List<String> markers = MyItems.getMarkers(stack);
                if (stack.getItem() == MakersMark.getItems().coin || stack.getItem() == MakersMark.getItems().coinDie)
                {
                    if (markers.size() > 0)
                    {
                        list.add((EnumChatFormatting.BLUE + StatCollector.translateToLocal("makersmark.report.advanced.coin.yes")).trim());
                        for (String marker : markers)
                        {
                            list.add((marker.equals(owner.getName()) ? EnumChatFormatting.YELLOW : EnumChatFormatting.DARK_BLUE) + marker);
                        }
                    }
                    else
                    {
                        list.add((EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("makersmark.report.advanced.coin.no")).trim());
                    }
                }
                else
                {
                    if (markers.size() > 0)
                    {
                        list.add((EnumChatFormatting.DARK_GREEN + StatCollector.translateToLocalFormatted("makersmark.report.advanced.object.yes", EnumChatFormatting.YELLOW + markers.get(0) + EnumChatFormatting.DARK_GREEN)).trim());
                    }
                    else
                    {
                        list.add((EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("makersmark.report.advanced.object.no")).trim());
                    }
                }
            }
            if (stack.getItem() == MakersMark.getItems().bag)
            {
                ItemBag.addInventoryToTooltip(stack, list);
            }

            FontRenderer font = stack.getItem().getFontRenderer(stack);
            this.drawHoveringText(list, x, y, (font == null ? fontRendererObj : font));
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead)
        {
            this.mc.thePlayer.closeScreen();
        }
    }

}
