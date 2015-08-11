package com.hitchh1k3rsguide.makersmark.nei;

import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.DefaultOverlayRenderer;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.ShapedRecipeHandler;
import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.blocks.BlockMailbox;
import com.hitchh1k3rsguide.makersmark.items.ItemCoin;
import com.hitchh1k3rsguide.makersmark.items.ItemCoinDie;
import com.hitchh1k3rsguide.makersmark.items.crafting.CraftingCoin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class NEIShapedHandler extends ShapedRecipeHandler
{

    public class CustomShapedRecipe extends CachedShapedRecipe
    {

        final long offset = System.currentTimeMillis();

        public CustomShapedRecipe(int width, int height, Object[] items, Object out)
        {
            super(width, height, items, ((out instanceof ItemStack) ? (ItemStack) out : new ItemStack(Items.apple)));
            if (out instanceof NEICommon.PositionedPlaceholder)
            {
                this.result = (NEICommon.PositionedPlaceholder) out;
            }
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
        public List<PositionedStack> getCycledIngredients(int cycle, List<PositionedStack> ingredients)
        {
            for (int itemIndex = 0; itemIndex < ingredients.size(); itemIndex++)
            {
                randomRenderPermutation(ingredients.get(itemIndex), cycle, itemIndex);
            }
            randomRenderPermutation(result, cycle, 10);

            return ingredients;
        }

        @Override
        public void setIngredients(int width, int height, Object[] items)
        {
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    if (items[y * width + x] == null)
                    {
                        continue;
                    }

                    PositionedStack stack;
                    if (items[y * width + x] instanceof NEICommon.PositionedPlaceholder.PlaceholderType)
                    {
                        stack = new NEICommon.PositionedPlaceholder(null, 25 + x * 18, 6 + y * 18, (NEICommon.PositionedPlaceholder.PlaceholderType) items[y * width + x]);
                    }
                    else if (items[y * width + x] instanceof NEICommon.PositionedPlaceholder)
                    {
                        stack = (NEICommon.PositionedPlaceholder) items[y * width + x];
                        stack.relx = 25 + x * 18;
                        stack.rely = 6 + y * 18;
                    }
                    else
                    {
                        stack = new PositionedStack(items[y * width + x], 25 + x * 18, 6 + y * 18, false);
                        stack.setMaxSize(1);
                    }
                    ingredients.add(stack);
                }
            }
        }

    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if ("crafting".equals(outputId))
        {
            addRecipe("bag");
            addRecipe("dieA");
            addRecipe("dieB");
            addRecipe("dieC");
            addRecipe("dieD");
            addRecipe("dieE");
            addRecipe("dieF");
            addRecipe("dieG");
            addRecipe("dieH");
            addRecipe("signA");
            addRecipe("signB");
            addRecipe("signC");
            addRecipe("signD");
            addRecipe("signE");
            addRecipe("signF");
            addRecipe("signG");
            addRecipe("signH");
            addRecipe("mailboxColors");
            this.metal = "unknown";
            addCoins(0);
            for (ItemCoin.MaterialDefinition wood : ItemCoin.woods.values())
            {
                addTokens(wood);
            }
        }
        else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    private Object[] generatePositionRecipe(Object root, Object... positionals)
    {
        boolean[] blackout = new boolean[]{ false, false, false, false, true, false, false, false, false };
        Object[] ret = new Object[3 * 3];
        ret[4] = root;
        for (int i = 0; i < positionals.length; i += 2)
        {
            int pos = (Integer) positionals[i];
            Object stack = positionals[i + 1];
            for (int bi = 0; bi <= pos; ++bi)
            {
                if (blackout[bi])
                {
                    ++pos;
                }
                pos %= 9;
            }
            if (!blackout[pos])
            {
                blackout[pos] = true;
            }
            ret[pos] = stack;
        }
        return ret;
    }

    private String metal = "unknown";

    private void addCoins(int shape)
    {
        for (int pattern = 0; pattern < 8; ++pattern)
        {
            for (int variant = 0; variant < 7; ++variant)
            {
                addRecipe("coin" + (char) (shape + 'A') + (char) (pattern + 'a') + (char) (variant + '1'));
            }
        }
    }

    private void addTokens(ItemCoin.MaterialDefinition wood)
    {
        if (wood != null)
        {
            for (int pattern = 0; pattern < 8; ++pattern)
            {
                ItemStack coin = new ItemStack(MakersMark.getItems().coin, 1, 1);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString(ItemCoin.TAG_MATERIAL, wood.name);
                tag.setInteger(ItemCoin.TAG_SHAPE, pattern);
                coin.setTagCompound(tag);
                ItemStack plank = wood.getIngot();
                ShapedRecipeHandler.CachedShapedRecipe recipe = new CustomShapedRecipe(3, 3, generatePositionRecipe(plank, pattern, new NEICommon.PositionedPlaceholder(NEICommon.allSwords, 0, 0, NEICommon.PositionedPlaceholder.PlaceholderType.TAKES_DAMAGE)), coin);
                recipe.computeVisuals();
                arecipes.add(recipe);
            }
        }
    }

    private void addRecipe(String recipeID)
    {
        ShapedRecipeHandler.CachedShapedRecipe recipe = null;
        if ("bag".equals(recipeID))
        {
            recipe = new CustomShapedRecipe(3, 3, new Object[]{ null, new ItemStack(Items.string), null, NEICommon.PositionedPlaceholder.PlaceholderType.ANY_STACK_OPTIONAL, NEICommon.PositionedPlaceholder.PlaceholderType.ANY_STACK_OPTIONAL, NEICommon.PositionedPlaceholder.PlaceholderType.ANY_STACK_OPTIONAL, null, new ItemStack(Items.leather), null }, new ItemStack(MakersMark.getItems().bag));
        }
        else if (recipeID.startsWith("sign") && recipeID.length() == 5)
        {
            int pattern = recipeID.charAt(4) - 'A';
            ItemStack die = new ItemStack(MakersMark.getItems().coinDie);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(ItemCoin.TAG_SHAPE, pattern);
            die.setTagCompound(tag);
            ItemStack out = die.copy();
            ItemCoinDie.addPlayer(out, Minecraft.getMinecraft().thePlayer);
            recipe = new CustomShapedRecipe(2, 2, new Object[]{ die, new ItemStack(Items.flint), null, null }, out);
        }
        else if (recipeID.startsWith("die") && recipeID.length() == 4)
        {
            int pattern = recipeID.charAt(3) - 'A';
            ItemStack die = new ItemStack(MakersMark.getItems().coinDie);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(ItemCoin.TAG_SHAPE, pattern);
            die.setTagCompound(tag);
            recipe = new CustomShapedRecipe(3, 3, generatePositionRecipe(new ItemStack(MakersMark.getItems().makersMark), pattern, new ItemStack(Items.flint)), die);
        }
        else if ("mailboxColors".equals(recipeID))
        {
            ItemStack box = new ItemStack(MakersMark.getBlocks().mailBox);
            ItemStack[] dyes = new ItemStack[16];
            ItemStack[] boxes = new ItemStack[16];
            for (int color = 0; color < 16; ++color)
            {
                boxes[color] = new ItemStack(MakersMark.getBlocks().mailBox);
                dyes[color] = new ItemStack(Items.dye, 1, color);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger(BlockMailbox.TAG_MAILBOX_COLOR, color);
                boxes[color].setTagCompound(tag);
            }
            recipe = new CustomShapedRecipe(2, 2, new Object[]{ box, new NEICommon.PositionedPlaceholder(dyes, 25, 6, NEICommon.PositionedPlaceholder.PlaceholderType.BASIC_CYCLE), null, null }, new NEICommon.PositionedPlaceholder(boxes, 119, 24, NEICommon.PositionedPlaceholder.PlaceholderType.BASIC_CYCLE));
        }
        else if (recipeID.startsWith("coin") && recipeID.length() == 7)
        {
            int shape = recipeID.charAt(4) - 'A';
            int pattern = recipeID.charAt(5) - 'a';
            int variant = recipeID.charAt(6) - '1';
            ItemStack coin = new ItemStack(MakersMark.getItems().coin);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(ItemCoin.TAG_MATERIAL, metal);
            tag.setInteger(ItemCoin.TAG_SHAPE, shape);
            tag.setInteger(ItemCoin.TAG_STYLE, pattern);
            tag.setInteger(ItemCoin.TAG_PATTERN, variant);
            coin.setTagCompound(tag);
            ItemStack die = new ItemStack(MakersMark.getItems().coinDie);
            tag = new NBTTagCompound();
            tag.setInteger(ItemCoin.TAG_SHAPE, shape);
            die.setTagCompound(tag);
            ItemStack unfinishedCoin = new ItemStack(MakersMark.getItems().coin);
            tag = new NBTTagCompound();
            tag.setString(ItemCoin.TAG_MATERIAL, metal);
            unfinishedCoin.setTagCompound(tag);
            recipe = new CustomShapedRecipe(3, 3, generatePositionRecipe(unfinishedCoin, pattern, new NEICommon.PositionedPlaceholder(die, 0, 0, NEICommon.PositionedPlaceholder.PlaceholderType.TAKES_DAMAGE), (variant + pattern) % 7, new NEICommon.PositionedPlaceholder(new ItemStack(MakersMark.getItems().mintersMallet), 0, 0, NEICommon.PositionedPlaceholder.PlaceholderType.TAKES_DAMAGE)), coin);
        }
        if (recipe != null)
        {
            recipe.computeVisuals();
            arecipes.add(recipe);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        if (result.getItem() == MakersMark.getItems().bag)
        {
            addRecipe("bag");
        }
        if (result.getItem() == Item.getItemFromBlock(MakersMark.getBlocks().mailBox))
        {
            addRecipe("mailboxColors");
        }
        if (result.getItem() == MakersMark.getItems().coinDie)
        {
            if (result.hasTagCompound() && result.getTagCompound().hasKey(ItemCoin.TAG_SHAPE))
            {
                addRecipe("die" + (char) ('A' + result.getTagCompound().getInteger(ItemCoin.TAG_SHAPE)));
                addRecipe("sign" + (char) ('A' + result.getTagCompound().getInteger(ItemCoin.TAG_SHAPE)));
            }
            else
            {
                addRecipe("dieA");
                addRecipe("dieB");
                addRecipe("dieC");
                addRecipe("dieD");
                addRecipe("dieE");
                addRecipe("dieF");
                addRecipe("dieG");
                addRecipe("dieH");
                addRecipe("signA");
                addRecipe("signB");
                addRecipe("signC");
                addRecipe("signD");
                addRecipe("signE");
                addRecipe("signF");
                addRecipe("signG");
                addRecipe("signH");
            }
        }
        if (result.getItem() == MakersMark.getItems().coin)
        {
            if (result.getItemDamage() == 0 && result.hasTagCompound())
            {
                if (result.getTagCompound().hasKey(ItemCoin.TAG_MATERIAL) && result.getTagCompound().hasKey(ItemCoin.TAG_SHAPE))
                {
                    this.metal = result.getTagCompound().getString(ItemCoin.TAG_MATERIAL);
                    addCoins(result.getTagCompound().getInteger(ItemCoin.TAG_SHAPE));
                }
            }
            else if (result.getItemDamage() == 1)
            {
                ItemCoin.MaterialDefinition wood = ItemCoin.getMaterial(result);
                addTokens(wood);
            }
        }
    }

    @Override
    public IRecipeOverlayRenderer getOverlayRenderer(GuiContainer gui, int recipe)
    {
        IRecipeOverlayRenderer renderer = super.getOverlayRenderer(gui, recipe);
        if (renderer != null)
        { return renderer; }

        IStackPositioner positioner = RecipeInfo.getStackPositioner(gui, "crafting2x2");
        if (positioner == null)
        { return null; }
        return new DefaultOverlayRenderer(getIngredientStacks(recipe), positioner);
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        if (ingredient.getItem() == Items.leather || ingredient.getItem() == Items.string)
        {
            addRecipe("bag");
        }
        if (ingredient.getItem() == Items.dye || ingredient.getItem() == Item.getItemFromBlock(MakersMark.getBlocks().mailBox))
        {
            addRecipe("mailboxColors");
        }
        if (ingredient.getItem() == Items.flint || ingredient.getItem() == MakersMark.getItems().makersMark)
        {
            addRecipe("dieA");
            addRecipe("dieB");
            addRecipe("dieC");
            addRecipe("dieD");
            addRecipe("dieE");
            addRecipe("dieF");
            addRecipe("dieG");
            addRecipe("dieH");
        }
        if (ingredient.getItem() == Items.flint)
        {
            addRecipe("signA");
            addRecipe("signB");
            addRecipe("signC");
            addRecipe("signD");
            addRecipe("signE");
            addRecipe("signF");
            addRecipe("signG");
            addRecipe("signH");
        }
        if (ingredient.getItem() == MakersMark.getItems().coinDie)
        {
            this.metal = "unknown";
            if (ingredient.hasTagCompound() && ingredient.getTagCompound().hasKey(ItemCoin.TAG_SHAPE))
            {
                addCoins(ingredient.getTagCompound().getInteger(ItemCoin.TAG_SHAPE));
                addRecipe("sign" + (char) ('A' + ingredient.getTagCompound().getInteger(ItemCoin.TAG_SHAPE)));
            }
            else
            {
                addCoins(0);
            }
        }
        if (ingredient.getItem() == MakersMark.getItems().coin)
        {
            if (ingredient.getItemDamage() == 0 && ingredient.hasTagCompound())
            {
                if (ingredient.getTagCompound().hasKey(ItemCoin.TAG_MATERIAL) && !ingredient.getTagCompound().hasKey(ItemCoin.TAG_SHAPE) && !ingredient.getTagCompound().hasKey(ItemCoin.TAG_STYLE) && !ingredient.getTagCompound().hasKey(ItemCoin.TAG_PATTERN))
                {
                    this.metal = ingredient.getTagCompound().getString(ItemCoin.TAG_MATERIAL);
                    addCoins(0);
                }
            }
        }
        if (ingredient.getItem() == MakersMark.getItems().mintersMallet)
        {
            this.metal = "unknown";
            addCoins(0);
        }
        if (CraftingCoin.getWood(ingredient) != null)
        {
            ItemCoin.MaterialDefinition wood = ItemCoin.woods.get(CraftingCoin.getWood(ingredient));
            addTokens(wood);
        }
        if (ingredient.getItem() instanceof ItemSword)
        {
            ItemCoin.MaterialDefinition wood = ItemCoin.woods.values().iterator().next();
            addTokens(wood);
        }
    }

    @Override
    public String getRecipeName()
    {
        return NEIClientUtils.translate("makersmark.recipe.crafting");
    }

}
