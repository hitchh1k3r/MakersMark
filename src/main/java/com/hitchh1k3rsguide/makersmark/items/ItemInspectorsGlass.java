package com.hitchh1k3rsguide.makersmark.items;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.config.MakersConfig;
import com.hitchh1k3rsguide.makersmark.containers.GUIInspection;
import com.hitchh1k3rsguide.makersmark.items.crafting.DynamicShapedOreRecipe;
import com.hitchh1k3rsguide.makersmark.util.ICondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ItemInspectorsGlass extends MakersBaseItem
{

    private boolean isAdvanced;

    public ItemInspectorsGlass(boolean isAdvanced)
    {
        this.isAdvanced = isAdvanced;
        this.setUnlocalizedName(getUnlocalizedNameRaw());
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public void commonRegister()
    {
        super.commonRegister();

        if (isAdvanced)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this), " L", "s ", 'L', "gemDiamond", 's', "stickWood"));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this), "L ", " s", 'L', "gemDiamond", 's', "stickWood"));
        }
        else
        {
            GameRegistry.addRecipe(new DynamicShapedOreRecipe(new ItemStack(this), new ICondition<InventoryCrafting>()
            {
                @Override
                public boolean test(InventoryCrafting subject)
                {
                    return !MakersConfig.ServerConfig.limitedInspection;
                }
            }, "L ", " s", 'L', "paneGlassColorless", 's', "stickWood"));
            GameRegistry.addRecipe(new DynamicShapedOreRecipe(new ItemStack(this), new ICondition<InventoryCrafting>()
            {
                @Override
                public boolean test(InventoryCrafting subject)
                {
                    return !MakersConfig.ServerConfig.limitedInspection;
                }
            }, " L", "s ", 'L', "paneGlassColorless", 's', "stickWood"));
        }
    }

    @Override
    public String getUnlocalizedNameRaw()
    {
        return isAdvanced ? "advanced_glass" : "inspectors_glass";
    }

    @Override
    public String getTextureName()
    {
        return MakersMark.MODID + ":" + getUnlocalizedNameRaw();
    }

    private static Object advancedModel = null;
    private static Object basicModel    = null;

    @Override
    @SideOnly(Side.CLIENT)
    public void clientRegister()
    {
        if (advancedModel == null)
        {
            advancedModel = new ModelResourceLocation(MakersMark.MODID + ":advanced_glass", "inventory");
            basicModel = new ModelResourceLocation(MakersMark.MODID + ":inspectors_glass", "inventory");
        }
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return (ModelResourceLocation) (MakersConfig.ServerConfig.limitedInspection ? advancedModel : (isAdvanced ? advancedModel : basicModel));
            }
        });
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return ("" + StatCollector.translateToLocal((MakersConfig.ServerConfig.limitedInspection ? "item." + MakersMark.MODID + ".inspectors_glass" : this.getUnlocalizedNameInefficiently(stack)) + ".name")).trim();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        player.openGui(MakersMark.instance, GUIInspection.GUI_ID, world, 0, 0, 0);
        return itemStack;
    }

    public void updateMode()
    {
        if (isAdvanced || !MakersConfig.ServerConfig.limitedInspection)
        {
            this.setCreativeTab(CreativeTabs.tabTools);
        }
        else
        {
            this.setCreativeTab(null);
        }
    }

}
