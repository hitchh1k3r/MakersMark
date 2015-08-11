package com.hitchh1k3rsguide.makersmark.sided;

import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.CoreConfig;
import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.HitchCore;
import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.blocks.BlockMailbox;
import com.hitchh1k3rsguide.makersmark.blocks.MyBlocks;
import com.hitchh1k3rsguide.makersmark.config.MakersConfig;
import com.hitchh1k3rsguide.makersmark.containers.GUIHandler;
import com.hitchh1k3rsguide.makersmark.entities.EntityCustomPotion;
import com.hitchh1k3rsguide.makersmark.events.CommonEvents;
import com.hitchh1k3rsguide.makersmark.events.FuelHandler;
import com.hitchh1k3rsguide.makersmark.items.ItemCoin;
import com.hitchh1k3rsguide.makersmark.items.ItemCustomPotion;
import com.hitchh1k3rsguide.makersmark.items.MyItems;
import com.hitchh1k3rsguide.makersmark.items.crafting.DynamicShapedOreRecipe;
import com.hitchh1k3rsguide.makersmark.items.crafting.ShapedRecipeNoContainers;
import com.hitchh1k3rsguide.makersmark.network.MyMessages;
import com.hitchh1k3rsguide.makersmark.potions.MyPotions;
import com.hitchh1k3rsguide.makersmark.util.Utils;
import net.minecraft.block.BlockPlanks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.*;

public class Proxy
{

    public interface IProxy
    {

        public void construction(FMLConstructionEvent event);

        public void postInit(FMLPostInitializationEvent event);

        public void init(FMLInitializationEvent event);

        public void preInit(FMLPreInitializationEvent event);

        public void loadComplete(FMLLoadCompleteEvent event);

        public void serverPreStart(FMLServerAboutToStartEvent event);

        public void serverStart(FMLServerStartingEvent event);

        public void serverPostStart(FMLServerStartedEvent event);

        public void serverPreStop(FMLServerStoppingEvent event);

        public void serverPostStop(FMLServerStoppedEvent event);

        public void registerItem(IMakersBase item);

        public void registerBlock(IMakersBase block);

    }

    @SidedProxy(modId = MakersMark.MODID, clientSide = "com.hitchh1k3rsguide.makersmark.sided.ClientSide", serverSide = "com.hitchh1k3rsguide.makersmark.sided.ServerSide")
    public static IProxy sidedProxy;

    public MyBlocks   blocks   = new MyBlocks();
    public MyItems    items    = new MyItems();
    public MyPotions  potions  = new MyPotions();
    public MyMessages messages = new MyMessages();

    public CreativeTabs creativeTab = new CreativeTabs(MakersMark.MODID)
    {

        @Override
        public Item getTabIconItem()
        {
            return items.makersMark;
        }

        @SideOnly(Side.CLIENT)
        public void displayAllReleventItems(List list)
        {
            Iterator iterator = Item.itemRegistry.iterator();

            while (iterator.hasNext())
            {
                Item item = (Item) iterator.next();
                if (item == null)
                {
                    continue;
                }

                if ((item instanceof IMakersBase && (!MakersConfig.ServerConfig.limitedInspection || item != MakersMark.getItems().inspectorsGlass)) || (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof IMakersBase) || ItemCustomPotion.isIngredient(item))
                {
                    item.getSubItems(item, this, list);
                }
            }
        }

    };

    public void construction(FMLConstructionEvent event)
    {
        FMLInterModComms.sendMessage(HitchCore.MODID, HitchCore.MESSAGE_MOD_VERSION, MakersMark.VERSION);
        FMLInterModComms.sendMessage(HitchCore.MODID, HitchCore.MESSAGE_MOD_HANDLER, "com.hitchh1k3rsguide.makersmark.MakersMarkHandler");

        sidedProxy.construction(event);
    }

    public void preInit(FMLPreInitializationEvent event)
    {
        MakersConfig.init();

        potions.createPotions();
        blocks.initialize();
        items.initialize();
        sidedProxy.preInit(event);
    }

    public void init(FMLInitializationEvent event)
    {
        blocks.registerBlocks();
        items.registerItems();
        blocks.registerOres();
        items.registerOres();
        messages.registerMessages();

        EntityRegistry.registerModEntity(EntityCustomPotion.class, "customPotion", EntityRegistry.findGlobalUniqueEntityId(), MakersMark.instance, 80, 20, true);

        sidedProxy.init(event);

        NetworkRegistry.INSTANCE.registerGuiHandler(MakersMark.instance, new GUIHandler());

        RecipeSorter.register(MakersMark.MODID + ":dynamic_shaped_ore", DynamicShapedOreRecipe.class, RecipeSorter.Category.SHAPED, "");
        RecipeSorter.register(MakersMark.MODID + ":shaped_no_containers", ShapedRecipeNoContainers.class, RecipeSorter.Category.SHAPED, "");
    }

    public void postInit(FMLPostInitializationEvent event)
    {
        String[] ores = OreDictionary.getOreNames();
        List<String> blackList = Arrays.asList("ingotBrick", "ingotBrickNether", "ingotMercury");
        for (String ore : ores)
        {
            if (ore.startsWith("ingot") && OreDictionary.getOres(ore).size() > 0 && !blackList.contains(ore))
            {
                String metalName = ore.substring(5);
                if (!ItemCoin.metals.containsKey(metalName))
                {
                    ItemCoin.metals.put(metalName, new ItemCoin.MaterialDefinition(metalName, OreDictionary.getOres(ore).get(0)));
                }
            }
        }

        if (CoreConfig.debugMode)
        {
            Utils.debugMsg("Adding Metals: " + String.join(", ", ItemCoin.metals.keySet()));
        }

        Map<String, String> woodModOverrides = new HashMap<String, String>();
        woodModOverrides.put("bush.NPlanks", "hopseed");

        Map<String, String> woodOverrides = new HashMap<String, String>();
        woodOverrides.put("bamboo_thatching", "bamboo");
        woodOverrides.put("purpleheart", "amaranth");
        woodOverrides.put("dark", "darkwood");
        for (ItemStack woodStack : OreDictionary.getOres("plankWood"))
        {
            if (woodStack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
            {
                for (int i = 0; i < 16; ++i)
                {
                    try
                    {
                        ItemStack stack = woodStack.copy();
                        stack.setItemDamage(i);
                        String woodName = stack.getUnlocalizedName().substring(5);
                        if (woodModOverrides.containsKey(woodName))
                        {
                            woodName = woodModOverrides.get(woodName);
                        }
                        if (woodName.startsWith("wood."))
                        {
                            woodName = woodName.substring(5);
                        }
                        if (woodName.startsWith("planks."))
                        {
                            woodName = woodName.substring(7);
                        }
                        if (woodName.endsWith(".NPlanks"))
                        {
                            woodName = woodName.substring(0, woodName.length() - 8);
                        }
                        if (woodOverrides.containsKey(woodName))
                        {
                            woodName = woodOverrides.get(woodName);
                        }
                        if (!ItemCoin.woods.containsKey(woodName))
                        {
                            ItemCoin.woods.put(woodName, new ItemCoin.MaterialDefinition(woodName, stack));
                        }
                        else
                        {
                            ItemCoin.woods.get(woodName).addStack(stack);
                        }
                    }
                    catch (Exception ignored) {}
                }
            }
            else
            {
                String woodName = woodStack.getUnlocalizedName().substring(5);
                if (woodName.startsWith("wood."))
                {
                    woodName = woodName.substring(5);
                }
                if (!ItemCoin.woods.containsKey(woodName))
                {
                    ItemCoin.woods.put(woodName, new ItemCoin.MaterialDefinition(woodName, woodStack));
                }
            }
        }

        if (CoreConfig.debugMode)
        {
            Utils.debugMsg("Adding Planks: " + String.join(", ", ItemCoin.woods.keySet()));
        }

        ItemCoin.registerOreRecipes();

        sidedProxy.postInit(event);

        GameRegistry.registerFuelHandler(new FuelHandler());
        CommonEvents eventHooks = new CommonEvents();
        MinecraftForge.EVENT_BUS.register(eventHooks);
        FMLCommonHandler.instance().bus().register(eventHooks);

        for (BlockPlanks.EnumType wood : BlockPlanks.EnumType.values())
        {
            ItemStack plank = new ItemStack(Blocks.planks, 1, wood.getMetadata());
            ItemStack feather = new ItemStack(MakersMark.getItems().goldenFeather);
            ItemStack out = new ItemStack(MakersMark.getBlocks().mailBox);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(BlockMailbox.TAG_MAILBOX_WOOD, BlockMailbox.enumToWood(wood).name);
            out.setTagCompound(tag);
            GameRegistry.addRecipe(new ShapedRecipeNoContainers(3, 3, new ItemStack[]{ plank, plank, plank, plank, feather, plank, plank, plank, plank }, out));
        }
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
        sidedProxy.loadComplete(event);
    }

    public void serverPreStart(FMLServerAboutToStartEvent event)
    {
        sidedProxy.serverPreStart(event);
    }

    public void serverStart(FMLServerStartingEvent event)
    {
        Utils.reloadPlayerFolder();
        sidedProxy.serverStart(event);
    }

    public void serverPostStart(FMLServerStartedEvent event)
    {
        sidedProxy.serverPostStart(event);
    }

    public void serverPreStop(FMLServerStoppingEvent event)
    {
        sidedProxy.serverPreStop(event);
    }

    public void serverPostStop(FMLServerStoppedEvent event)
    {
        sidedProxy.serverPostStop(event);
    }

    public void registerItem(IMakersBase item)
    {
        item.commonRegister();
        sidedProxy.registerItem(item);
    }

    public void registerBlock(IMakersBase block)
    {
        block.commonRegister();
        sidedProxy.registerBlock(block);
    }

}
