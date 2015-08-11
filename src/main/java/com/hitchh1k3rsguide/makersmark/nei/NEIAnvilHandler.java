package com.hitchh1k3rsguide.makersmark.nei;

import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.DefaultOverlayRenderer;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.hitchh1k3rsguide.makersmark.MakersMark;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

public class NEIAnvilHandler extends TemplateRecipeHandler
{

    public class CustomAnvilRecipe extends CachedRecipe
    {

        final long offset = System.currentTimeMillis();

        public NEICommon.PositionedPlaceholder tool, material;
        public List<PositionedStack> ingredients = new ArrayList<PositionedStack>();

        public PositionedStack result;

        public CustomAnvilRecipe(Object tool, Object material, Object out)
        {
            if (tool instanceof NEICommon.PositionedPlaceholder.PlaceholderType)
            {
                this.tool = new NEICommon.PositionedPlaceholder(null, 22, 40, (NEICommon.PositionedPlaceholder.PlaceholderType) tool);
            }
            else if (tool instanceof NEICommon.PositionedPlaceholder)
            {
                this.tool = (NEICommon.PositionedPlaceholder) tool;
                this.tool.relx = 22;
                this.tool.rely = 40;
            }
            else
            {
                this.tool = new NEICommon.PositionedPlaceholder(tool, 22, 40, NEICommon.PositionedPlaceholder.PlaceholderType.STANDARD);
                this.tool.setMaxSize(1);
            }
            if (material instanceof NEICommon.PositionedPlaceholder.PlaceholderType)
            {
                this.material = new NEICommon.PositionedPlaceholder(null, 71, 40, (NEICommon.PositionedPlaceholder.PlaceholderType) material);
            }
            else if (material instanceof NEICommon.PositionedPlaceholder)
            {
                this.material = (NEICommon.PositionedPlaceholder) material;
                this.material.relx = 71;
                this.material.rely = 40;
            }
            else
            {
                this.material = new NEICommon.PositionedPlaceholder(material, 71, 40, NEICommon.PositionedPlaceholder.PlaceholderType.STANDARD);
                this.material.setMaxSize(1);
            }
            if (out instanceof NEICommon.PositionedPlaceholder.PlaceholderType)
            {
                this.result = new NEICommon.PositionedPlaceholder(null, 129, 40, (NEICommon.PositionedPlaceholder.PlaceholderType) out);
            }
            else if (out instanceof NEICommon.PositionedPlaceholder)
            {
                this.result = (NEICommon.PositionedPlaceholder) out;
                this.result.relx = 129;
                this.result.rely = 40;
            }
            else
            {
                this.result = new NEICommon.PositionedPlaceholder(out, 129, 40, NEICommon.PositionedPlaceholder.PlaceholderType.STANDARD);
                this.result.setMaxSize(1);
            }
            ingredients.add(this.tool);
            ingredients.add(this.material);
        }

        public void randomRenderPermutation(PositionedStack stack, long cycle, int itemIndex)
        {
            if (!NEICommon.randomRenderPermutation(stack, cycle + offset, itemIndex))
            {
                super.randomRenderPermutation(stack, (cycle / 20) + offset + itemIndex);
            }
        }

        @Override
        public List<PositionedStack> getIngredients()
        {
            return getCycledIngredients(cycleticks, ingredients);
        }

        @Override
        public PositionedStack getResult()
        {
            return result;
        }

        @Override
        public List<PositionedStack> getCycledIngredients(int cycle, List<PositionedStack> ingredients)
        {
            for (int itemIndex = 0; itemIndex < ingredients.size(); itemIndex++)
            {
                randomRenderPermutation(ingredients.get(itemIndex), cycle, itemIndex);
            }
            randomRenderPermutation(result, cycle, 2);

            return ingredients;
        }

        public void computeVisuals()
        {
            for (PositionedStack p : ingredients)
            {
                p.generatePermutations();
            }
        }

    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new RecipeTransferRect(new Rectangle(96, 40, 24, 18), "anvil"));
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass()
    {
        return GuiRepair.class;
    }

    @Override
    public String getRecipeName()
    {
        return NEIClientUtils.translate("makersmark.recipe.anvil");
    }

    private void addRecipe(String recipeID)
    {
        CustomAnvilRecipe recipe = null;
        if ("marking".equals(recipeID))
        {
            recipe = new CustomAnvilRecipe(NEICommon.PositionedPlaceholder.PlaceholderType.ANY_TOOL, new NEICommon.PositionedPlaceholder(new ItemStack(MakersMark.getItems().makersMark), 0, 0, NEICommon.PositionedPlaceholder.PlaceholderType.TAKES_DAMAGE), NEICommon.PositionedPlaceholder.PlaceholderType.REPEAT_LAST_RAND);
        }
        if (recipe != null)
        {
            recipe.computeVisuals();
            arecipes.add(recipe);
        }
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if ("anvil".equals(outputId))
        {
            addRecipe("marking");
        }
        else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        if (ingredient.getItem() == MakersMark.getItems().makersMark)
        {
            addRecipe("marking");
        }
    }

    @Override
    public String getGuiTexture()
    {
        return "textures/gui/container/anvil.png";
    }

    @Override
    public void drawBackground(int recipe)
    {
        GlStateManager.color(1, 1, 1, 1);
        changeTexture(getGuiTexture());
        drawTexturedModalRect(0, 0, 5, 7, 166, 65);
    }

    @Override
    public String getOverlayIdentifier()
    {
        return "anvil";
    }

    public boolean hasOverlay(GuiContainer gui, net.minecraft.inventory.Container container, int recipe)
    {
        return super.hasOverlay(gui, container, recipe) && RecipeInfo.hasDefaultOverlay(gui, "anvil");
    }

    @Override
    public IRecipeOverlayRenderer getOverlayRenderer(GuiContainer gui, int recipe)
    {
        IRecipeOverlayRenderer renderer = super.getOverlayRenderer(gui, recipe);
        if (renderer != null)
        {
            return renderer;
        }

        IStackPositioner positioner = RecipeInfo.getStackPositioner(gui, "anvil");
        if (positioner == null)
        {
            return null;
        }
        return new DefaultOverlayRenderer(getIngredientStacks(recipe), positioner);
    }

    @Override
    public IOverlayHandler getOverlayHandler(GuiContainer gui, int recipe)
    {
        IOverlayHandler handler = super.getOverlayHandler(gui, recipe);
        if (handler != null)
        {
            return handler;
        }

        return RecipeInfo.getOverlayHandler(gui, "anvil");
    }

    public void drawExtras(int recipe)
    {
        drawTexturedModalRect(54, 13, 0, 182, 110, 16);
    }

}
