package com.hitchh1k3rsguide.makersmark.containers;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.util.Utils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GUILetter extends GuiContainer
{

    private static final ResourceLocation LETTER_BACKGROUND = new ResourceLocation(MakersMark.MODID, "textures/gui/letter.png");

    private static ItemStack letterStack = new ItemStack(MakersMark.getItems().letter);

    public static final int GUI_ID = 1;

    private int cursorX, cursorY, cursorIndex, updateCount;
    private ContainerLetter containerLetter;
    private boolean requestInventory = false;
    private Utils.WrapLine[] lines;

    public GUILetter(ItemStack letter, EntityPlayer player)
    {
        super(new ContainerLetter(letter, player));
        containerLetter = (ContainerLetter) inventorySlots;
        ySize = 166;
        cursorX = 0;
        cursorY = 0;
        cursorIndex = 0;
        updateCount = 0;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        lines = Utils.wrapString(fontRendererObj, containerLetter.message, 119, 15);
        cursorIndex = containerLetter.message.length();
        updateCursorPos();
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    private void updateCursorPos()
    {
        int mostUnder = 0;
        for (int i = 0; i < lines.length; ++i)
        {
            if (lines[i].startIndex <= cursorIndex)
            {
                mostUnder = i;
            }
        }

        cursorX = fontRendererObj.getStringWidth(lines[mostUnder].line.substring(0, cursorIndex - lines[mostUnder].startIndex));
        cursorY = 9 * mostUnder;
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        ++this.updateCount;
        if (containerLetter.signed)
        {
            containerLetter.setInventoryShowing(containerLetter.playerInventory.getItemStack() != null);
        }
        else
        {
            boolean open = false;
            if (containerLetter.playerInventory.getItemStack() != null)
            {
                if (containerLetter.inventory.getStackInSlot(0) != null)
                {
                    requestInventory = false;
                }
                open = true;
            }
            containerLetter.setInventoryShowing(open || requestInventory);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if (!containerLetter.signed && mouseX > guiLeft + 121 && mouseX < guiLeft + 121 + 18 && mouseY > guiTop + 144 && mouseY < guiTop + 144 + 18)
        {
            drawGradientRect(122, 145, 122 + 16, 145 + 16, 0x40FFFFFF, 0x40FFFFFF);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (containerLetter.signed)
        {
            if (containerLetter.inventoryOpen)
            {
                GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
            }
            mc.getTextureManager().bindTexture(LETTER_BACKGROUND);

            // paper:
            drawTexturedModalRect(guiLeft + 23, guiTop, 0, 0, 130, 166);

            // pickup item:
            drawTexturedModalRect(guiLeft + 121, guiTop + 144, 130, 0, 18, 18);

            // background pickup item:
            if (containerLetter.taken && containerLetter.itemStack != null)
            {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.enableRescaleNormal();
                itemRender.renderItemAndEffectIntoGUI(containerLetter.itemStack, guiLeft + 122, guiTop + 145);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableDepth();
                if (containerLetter.itemStack.stackSize > 1)
                {
                    fontRendererObj.drawStringWithShadow("" + containerLetter.itemStack.stackSize, guiLeft + 122 + 19 - 2 - fontRendererObj.getStringWidth("" + containerLetter.itemStack.stackSize), guiTop + 145 + 6 + 3, 0xFFFFFF);
                }

                // faded effect:
                if (containerLetter.inventoryOpen)
                {
                    GlStateManager.color(0.5F, 0.5F, 0.5F, 0.6F);
                }
                else
                {
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 0.6f);
                }
                mc.getTextureManager().bindTexture(LETTER_BACKGROUND);

                drawTexturedModalRect(guiLeft + 121, guiTop + 144, 130, 0, 18, 18);
                GlStateManager.enableDepth();
            }
        }
        else
        {
            mc.getTextureManager().bindTexture(LETTER_BACKGROUND);

            // paper:
            drawTexturedModalRect(guiLeft + 23, guiTop, 0, 0, 130, 166);

            // item:
            drawTexturedModalRect(guiLeft + 100, guiTop + 144, 130, 0, 18, 18);

            // okay:
            drawTexturedModalRect(guiLeft + 121, guiTop + 144, 148, 0, 18, 18);

            // cursor:
            if (!containerLetter.inventoryOpen)
            {
                if ((updateCount / 6) % 2 == 0)
                {
                    drawTexturedModalRect(guiLeft + 27 + cursorX, guiTop + 5 + cursorY, 130, 18, 2, 10);
                }
                else
                {
                    drawTexturedModalRect(guiLeft + 27 + cursorX, guiTop + 5 + cursorY, 132, 18, 2, 10);
                }
            }
        }

        // message:
        int l = 0;
        for (Utils.WrapLine line : lines)
        {
            fontRendererObj.drawString(line.line, guiLeft + 27, guiTop + 6 + l, 0x000000);
            l += 9;
        }

        // inventory:
        if (containerLetter.inventoryOpen)
        {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            mc.getTextureManager().bindTexture(LETTER_BACKGROUND);

            drawTexturedModalRect(guiLeft, guiTop + 29, 0, 166, 176, 88);

            itemRender.renderItemIntoGUI(letterStack, guiLeft + containerLetter.invBlankX, guiTop + containerLetter.invBlankY);
            zLevel = 101;
            drawGradientRect(guiLeft + containerLetter.invBlankX, guiTop + containerLetter.invBlankY, guiLeft + containerLetter.invBlankX + 16, guiTop + containerLetter.invBlankY + 16, 0x60000000, 0x60000000);
            zLevel = 0;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseY > guiTop + 145 && mouseY < guiTop + 145 + 16)
        {
            if (mouseX > guiLeft + 101 && mouseX < guiLeft + 101 + 16 && !containerLetter.signed)
            {
                requestInventory = !requestInventory;
            }
            else if (mouseX > guiLeft + 122 && mouseX < guiLeft + 122 + 16 && !containerLetter.signed)
            {
                this.mc.thePlayer.closeScreen();
            }
        }

        if (!containerLetter.signed && !containerLetter.inventoryOpen)
        {
            if (mouseY > guiTop + 4 && mouseY < guiTop + 143 && mouseX > guiLeft + 25 && mouseX < guiLeft + 148)
            {
                int l = ((mouseY - guiTop + 6) / 9) - 1;
                if (l < 0)
                {
                    l = 0;
                }
                else if (l > lines.length - 1)
                {
                    l = lines.length - 1;
                }
                int lineNumber = l;
                cursorX = mouseX - (guiLeft + 27);
                if (lineNumber > lines.length - 1)
                {
                    cursorIndex = containerLetter.message.length();
                }
                else
                {
                    int distance = Integer.MAX_VALUE;
                    int x = 0;
                    for (int i = lines[lineNumber].startIndex; i <= lines[lineNumber].startIndex + lines[lineNumber].line.length(); ++i)
                    {
                        int d = Math.abs(x - cursorX);
                        if (d > distance)
                        {
                            break;
                        }
                        else
                        {
                            distance = d;
                            cursorIndex = i;
                            if (i < lines[lineNumber].startIndex + lines[lineNumber].line.length())
                            {
                                x += fontRendererObj.getCharWidth(containerLetter.message.charAt(i));
                            }
                        }
                    }
                }
                updateCursorPos();
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (containerLetter.inventoryOpen)
        {
            if (keyCode != this.mc.gameSettings.keyBindsHotbar[containerLetter.invBlankID].getKeyCode())
            {
                super.keyTyped(typedChar, keyCode);
            }
        }
        else if (containerLetter.signed)
        {
            if (keyCode == Keyboard.KEY_ESCAPE || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode())
            {
                this.mc.thePlayer.closeScreen();
            }
        }
        else
        {
            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                this.mc.thePlayer.closeScreen();
            }
            else if (keyCode == Keyboard.KEY_LEFT)
            {
                if (cursorIndex > 0)
                {
                    --cursorIndex;
                }
            }
            else if (keyCode == Keyboard.KEY_RIGHT)
            {
                if (cursorIndex < containerLetter.message.length())
                {
                    ++cursorIndex;
                }
            }
            else if (keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_DOWN || keyCode == Keyboard.KEY_HOME || keyCode == Keyboard.KEY_END)
            {
                int lineNumber = 0;
                for (int i = 0; i < lines.length; ++i)
                {
                    if (lines[i].startIndex <= cursorIndex)
                    {
                        lineNumber = i;
                    }
                }
                if (keyCode == Keyboard.KEY_UP)
                {
                    --lineNumber;
                }
                else if (keyCode == Keyboard.KEY_DOWN)
                {
                    ++lineNumber;
                }
                if (keyCode == Keyboard.KEY_HOME)
                {
                    cursorIndex = lines[lineNumber].startIndex;
                }
                else if (keyCode == Keyboard.KEY_END)
                {
                    cursorIndex = lineNumber == lines.length - 1 ? containerLetter.message.length() : lines[lineNumber + 1].startIndex - 1;
                }
                else if (lineNumber < 0)
                {
                    cursorIndex = 0;
                }
                else if (lineNumber > lines.length - 1)
                {
                    cursorIndex = containerLetter.message.length();
                }
                else
                {
                    int distance = Integer.MAX_VALUE;
                    int x = 0;
                    for (int i = lines[lineNumber].startIndex; i <= lines[lineNumber].startIndex + lines[lineNumber].line.length(); ++i)
                    {
                        int d = Math.abs(x - cursorX);
                        if (d > distance)
                        {
                            break;
                        }
                        else
                        {
                            distance = d;
                            cursorIndex = i;
                            if (i < lines[lineNumber].startIndex + lines[lineNumber].line.length())
                            {
                                x += fontRendererObj.getCharWidth(containerLetter.message.charAt(i));
                            }
                        }
                    }
                }
            }
            else if (keyCode == Keyboard.KEY_BACK)
            {
                if (cursorIndex > 0)
                {
                    containerLetter.message = containerLetter.message.substring(0, cursorIndex - 1) + containerLetter.message.substring(cursorIndex);
                    --cursorIndex;
                    lines = Utils.wrapString(fontRendererObj, containerLetter.message, 119, 15);
                }
            }
            else if (keyCode == Keyboard.KEY_DELETE)
            {
                if (cursorIndex < containerLetter.message.length())
                {
                    containerLetter.message = containerLetter.message.substring(0, cursorIndex) + containerLetter.message.substring(cursorIndex + 1);
                    lines = Utils.wrapString(fontRendererObj, containerLetter.message, 119, 15);
                }
            }
            else if ((!Character.isISOControl(typedChar) && fontRendererObj.getCharWidth(typedChar) > 0) || keyCode == Keyboard.KEY_RETURN)
            {
                if (keyCode == Keyboard.KEY_RETURN)
                {
                    typedChar = '\n';
                }
                String msg = containerLetter.message.substring(0, cursorIndex) + typedChar + containerLetter.message.substring(cursorIndex);
                Utils.WrapLine[] msgLines = Utils.wrapString(fontRendererObj, msg, 119, 15);
                if (msgLines[msgLines.length - 1].startIndex + msgLines[msgLines.length - 1].line.length() == msg.length())
                {
                    containerLetter.message = msg;
                    lines = msgLines;
                    ++cursorIndex;
                }
            }

            updateCursorPos();
        }
    }

}
