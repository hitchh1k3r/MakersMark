package com.hitchh1k3rsguide.makersmark.items;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.config.MakersConfig;
import com.hitchh1k3rsguide.makersmark.items.crafting.CraftingCoin;
import com.hitchh1k3rsguide.makersmark.util.GraphicUtils;
import com.hitchh1k3rsguide.makersmark.util.TweenLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ItemCoin extends MakersBaseItem
{

    public static final String TAG_MATERIAL = MakersMark.MODID + ".material";
    public static final String TAG_SHAPE    = MakersMark.MODID + ".shape";
    public static final String TAG_STYLE    = MakersMark.MODID + ".style";
    public static final String TAG_PATTERN  = MakersMark.MODID + ".pattern";
    public static final String TAG_NAME     = MakersMark.MODID + ".name";
    public static final String TAG_MINTERS  = MakersMark.MODID + ".minters";

    public static final String[] SHAPES   = new String[]{ "A", "B", "C", "D", "E", "F", "G", "H" };
    public static final String[] STYLES   = new String[]{ "a", "b", "c", "d", "e", "f", "g", "h" };
    public static final int      PATTERNS = 7;

    public static final String[] TOKENS = new String[]{ "a", "b", "c", "d", "e", "f", "g", "h" };

    public static Map<String, MaterialDefinition> metals = new HashMap<String, MaterialDefinition>();
    public static Map<String, MaterialDefinition> woods  = new HashMap<String, MaterialDefinition>();

    public ItemCoin()
    {
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedNameRaw()
    {
        return "coin";
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag.hasKey("display") && tag.getCompoundTag("display").hasKey("Name"))
            {
                return super.getItemStackDisplayName(stack);
            }
            String name = tag.getString(TAG_NAME);
            if (!StringUtils.isNullOrEmpty(name))
            {
                return (name);
            }
            String material = tag.getString(TAG_MATERIAL);
            if (!StringUtils.isNullOrEmpty(material))
            {
                String state;
                if (stack.getItemDamage() == 1)
                {
                    state = "token";
                    if (!woods.containsKey(material) || !StatCollector.canTranslate("item." + MakersMark.MODID + "." + state + "." + material.toLowerCase() + ".name"))
                    {
                        material = "unknown";
                    }
                }
                else
                {
                    if (tag.hasKey(TAG_SHAPE) && tag.hasKey(TAG_STYLE) && tag.hasKey(TAG_PATTERN))
                    {
                        state = "coin";
                    }
                    else
                    {
                        state = "unfinished_coin";
                    }
                    if (!metals.containsKey(material) || !StatCollector.canTranslate("item." + MakersMark.MODID + "." + state + "." + material.toLowerCase() + ".name"))
                    {
                        material = "unknown";
                    }
                }
                return StatCollector.translateToLocal("item." + MakersMark.MODID + "." + state + "." + material.toLowerCase() + ".name").trim();
            }
        }
        if (stack.getItemDamage() == 1)
        {
            return StatCollector.translateToLocal("item." + MakersMark.MODID + ".token.unknown.name").trim();
        }
        else
        {
            return StatCollector.translateToLocal("item." + MakersMark.MODID + ".unfinished_coin.unknown.name").trim();
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        MaterialDefinition material = getMaterial(stack);
        if (material != null)
        {
            return material.color.getRGB();
        }
        return super.getColorFromItemStack(stack, renderPass);
    }

    public static MaterialDefinition getMaterial(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            String material = tag.getString(TAG_MATERIAL);
            if (!StringUtils.isNullOrEmpty(material))
            {
                if (stack.getItemDamage() == 1)
                {
                    return woods.get(material);
                }
                else
                {
                    return metals.get(material);
                }
            }
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List list)
    {
        for (MaterialDefinition metal : metals.values())
        {
            ItemStack stack = new ItemStack(itemIn);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(TAG_MATERIAL, metal.name);
            stack.setTagCompound(tag);
            list.add(stack);
            if (MakersConfig.creativeCoins)
            {
                for (int p = 0; p < SHAPES.length; ++p)
                {
                    for (int s = 0; s < STYLES.length; ++s)
                    {
                        for (int n = 0; n < PATTERNS; ++n)
                        {
                            stack = new ItemStack(itemIn);
                            tag = new NBTTagCompound();
                            tag.setString(TAG_MATERIAL, metal.name);

                            tag.setInteger(TAG_SHAPE, p);
                            tag.setInteger(TAG_STYLE, s);
                            tag.setInteger(TAG_PATTERN, n);

                            stack.setTagCompound(tag);
                            list.add(stack);
                        }
                    }
                }
            }
        }
        for (MaterialDefinition wood : woods.values())
        {
            for (int p = 0; p < TOKENS.length; ++p)
            {
                ItemStack stack = new ItemStack(itemIn, 1, 1);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString(TAG_MATERIAL, wood.name);

                tag.setInteger(TAG_SHAPE, p);

                stack.setTagCompound(tag);
                list.add(stack);
            }
        }
    }

    public List<ItemStack> getNEIItems()
    {
        List<ItemStack> list = new ArrayList<ItemStack>();
        for (MaterialDefinition metal : metals.values())
        {
            ItemStack stack = new ItemStack(this);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(TAG_MATERIAL, metal.name);
            stack.setTagCompound(tag);
            list.add(stack);
            for (int p = 0; p < SHAPES.length; ++p)
            {
                stack = new ItemStack(this);
                tag = new NBTTagCompound();
                tag.setString(TAG_MATERIAL, metal.name);

                tag.setInteger(TAG_SHAPE, p);
                tag.setInteger(TAG_STYLE, 2);
                tag.setInteger(TAG_PATTERN, 0);

                stack.setTagCompound(tag);
                list.add(stack);
            }
        }
        for (MaterialDefinition wood : woods.values())
        {
            for (int p = 0; p < TOKENS.length; ++p)
            {
                ItemStack stack = new ItemStack(this, 1, 1);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString(TAG_MATERIAL, wood.name);

                tag.setInteger(TAG_SHAPE, p);

                stack.setTagCompound(tag);
                list.add(stack);
            }
        }
        return list;
    }

    @Override
    public String getTextureName()
    {
        return MakersMark.MODID + ":unfinished_coin";
    }

    @Override
    public void commonRegister()
    {
        super.commonRegister();

        GameRegistry.addRecipe(new CraftingCoin());
        RecipeSorter.register(MakersMark.MODID + ":coin_crafting", CraftingCoin.class, RecipeSorter.Category.SHAPELESS, "");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientRegister()
    {
        String[] names = new String[1 + (SHAPES.length * STYLES.length * PATTERNS) + TOKENS.length];
        names[0] = getTextureName();
        int i = 0;
        for (String shape : SHAPES)
        {
            for (String style : STYLES)
            {
                for (int num = 1; num <= PATTERNS; ++num)
                {
                    names[++i] = MakersMark.MODID + ":coin_" + shape + style + num;
                }
            }
        }
        for (String token : TOKENS)
        {
            names[++i] = MakersMark.MODID + ":token_" + token;
        }
        ModelBakery.addVariantName(this, names);

        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return getModel(stack, null, 0);
            }
        });
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            if (stack.getItemDamage() == 1)
            {
                int p = tag.getInteger(TAG_SHAPE);
                if (p >= TOKENS.length)
                {
                    p = 0;
                }
                return new ModelResourceLocation(MakersMark.MODID + ":token_" + TOKENS[p], "inventory");
            }
            else if (tag.hasKey(TAG_SHAPE) && tag.hasKey(TAG_STYLE))
            {
                int p = tag.getInteger(TAG_SHAPE);
                int s = tag.getInteger(TAG_STYLE);
                int n = tag.getInteger(TAG_PATTERN);
                if (p >= 0 && p < SHAPES.length && s >= 0 && s < STYLES.length && n >= 0 && n < PATTERNS)
                {
                    return new ModelResourceLocation(MakersMark.MODID + ":coin_" + SHAPES[p] + STYLES[s] + (n + 1), "inventory");
                }
            }
        }
        return new ModelResourceLocation(getTextureName(), "inventory");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            /*
            if (CoreConfig.debugMode && tag.hasKey(TAG_MATERIAL))
            {
                tooltip.add(EnumChatFormatting.DARK_GREEN + tag.getString(TAG_MATERIAL));
            }
            */
            if (stack.getItemDamage() == 1)
            {
                if (tag.hasKey(TAG_MINTERS))
                {
                    NBTTagCompound s = tag.getCompoundTag(TAG_MINTERS);

                    if (s != null)
                    {
                        tooltip.add(EnumChatFormatting.DARK_BLUE + StatCollector.translateToLocalFormatted(MakersMark.MODID + ".report.token", NBTUtil.readGameProfileFromNBT(s).getName()));
                    }
                }
            }
        }
    }

    public static void registerOreRecipes()
    {
        // TODO (hitch) in a future version add coin -> nugget recipes if either of these are pulled:
        // Added Furnace Hooks - https://github.com/MinecraftForge/MinecraftForge/pull/1693
        // Crafting and Smelting Hooks - https://github.com/MinecraftForge/MinecraftForge/pull/1697
        // or via ASM? (might be tricky to pull off with a minimal patch)
        //
        // to work it will need an NBT sensitive furnace recipe or an event to manage a "recipe"
        // ideally it will search for nuggets and add the recipe if one is found, for metals with no nugget
        // there should be no furnace recipe

        for (MaterialDefinition metal : metals.values())
        {
            ItemStack metalCoin = new ItemStack(MakersMark.getItems().coin, 8);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(TAG_MATERIAL, metal.name);
            metalCoin.setTagCompound(tag);
            GameRegistry.addRecipe(new ShapelessOreRecipe(metalCoin, new ItemStack(MakersMark.getItems().mintersMallet, 1, OreDictionary.WILDCARD_VALUE), "ingot" + metal.name));
        }
    }

    //////////////////////////////////////////////////////////////////////////////

    public static class MaterialDefinition
    {

        public        String               name;
        public        Color                color;
        public final  ArrayList<ItemStack> variantStacks;
        private final ItemStack            stack;

        public MaterialDefinition(String name, ItemStack stack)
        {
            this.name = name;
            this.stack = stack;
            variantStacks = new ArrayList<ItemStack>();
            variantStacks.add(stack);
        }

        public ItemStack getIngot()
        {
            return stack;
        }

        @SideOnly(Side.CLIENT)
        public static void calculateColors(TextureMap map, Collection<MaterialDefinition> materials, boolean lighten)
        {
            if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().getRenderItem() != null && Minecraft.getMinecraft().getRenderItem().getItemModelMesher() != null)
            {
                ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
                TextureAtlasSprite[] icons = new TextureAtlasSprite[materials.size()];
                int i = 0;
                for (MaterialDefinition material : materials)
                {
                    icons[i] = map.getAtlasSprite(mesher.getItemModel(material.getIngot()).getTexture().getIconName());
                    ++i;
                }
                Color[] colors = GraphicUtils.getIconColors(icons);
                i = 0;
                for (MaterialDefinition material : materials)
                {
                    double red = colors[i].getRed() / 255.0;
                    double green = colors[i].getGreen() / 255.0;
                    double blue = colors[i].getBlue() / 255.0;

                    double rF = red * red * 0.299;
                    double gF = green * green * 0.587;
                    double bF = blue * blue * 0.114;

                    double P = Math.sqrt(rF + gF + bF);
                    double S = 1 - Math.min(red, Math.min(green, blue)) / Math.max(red, Math.max(green, blue));
                    if (S > 0)
                    {
                        double S2 = TweenLib.quadInOut(0, 1, S);
                        double c = S2 / S;
                        red = P + (red - P) * c;
                        green = P + (green - P) * c;
                        blue = P + (blue - P) * c;

                        rF = red * red * 0.299;
                        gF = green * green * 0.587;
                        bF = blue * blue * 0.114;

                        P = Math.sqrt(rF + gF + bF);
                    }

                    if (lighten)
                    {
                        double v = (P + 1) / (2 * P);

                        red *= v; //(rF + red) / 2;
                        green *= v; // (gF + green) / 2;
                        blue *= v; // (bF + blue) / 2;
                    }
                    if (red > 1)
                    {
                        double v = 1 / red;
                        red *= v;
                        green *= v;
                        blue *= v;
                    }
                    if (green > 1)
                    {
                        double v = 1 / green;
                        red *= v;
                        green *= v;
                        blue *= v;
                    }
                    if (blue > 1)
                    {
                        double v = 1 / blue;
                        red *= v;
                        green *= v;
                        blue *= v;
                    }
                    material.color = new Color((int) (red * 255), (int) (green * 255), (int) (blue * 255));
                    ++i;
                }
            }
        }

        public void addStack(ItemStack stack)
        {
            variantStacks.add(stack);
        }

    }

}
