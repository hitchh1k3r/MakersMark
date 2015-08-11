package com.hitchh1k3rsguide.makersmark.blocks;

import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.blocks.tileentity.TEMailbox;
import com.hitchh1k3rsguide.makersmark.config.MakersConfig;
import com.hitchh1k3rsguide.makersmark.containers.ContainerMailbox;
import com.hitchh1k3rsguide.makersmark.containers.GUIMailbox;
import com.hitchh1k3rsguide.makersmark.containers.InventoryMailbox;
import com.hitchh1k3rsguide.makersmark.graphics.TileEntityRenderer;
import com.hitchh1k3rsguide.makersmark.items.ItemCoin;
import com.hitchh1k3rsguide.makersmark.items.ItemMailbox;
import com.hitchh1k3rsguide.makersmark.items.crafting.CraftingMailboxColors;
import com.hitchh1k3rsguide.makersmark.util.Utils;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.List;

public class BlockMailbox extends MakersBaseBlock implements ITileEntityProvider, IColoredBlock
{

    public static final String TAG_MAILBOX_COLOR = MakersMark.MODID + ".inventory";
    public static final String TAG_MAILBOX_WOOD  = MakersMark.MODID + ".wood";
    public static int    breakingColor;
    public static String breakingWood;

    public static final PropertyEnum      VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class);
    public static final PropertyDirection FACING  = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static ItemCoin.MaterialDefinition defaultWood;

    public BlockMailbox()
    {
        super(Material.wood);
        this.setUnlocalizedName(getUnlocalizedNameRaw());
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.OAK).withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public String getUnlocalizedNameRaw()
    {
        return "mailbox";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean isFullCube()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass)
    {
        TileEntity te = world.getTileEntity(pos);
        if (renderPass == 1 && te != null && te instanceof TEMailbox)
        {
            return ((TEMailbox) te).getColor();
        }
        return 0xFFFFFF;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        List<ItemStack> ret = new java.util.ArrayList<ItemStack>();

        ItemStack drop = new ItemStack(Item.getItemFromBlock(this));
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TEMailbox)
        {
            TEMailbox mailBox = (TEMailbox) te;
            breakingColor = mailBox.color;
            breakingWood = mailBox.getWood().name;
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(TAG_MAILBOX_COLOR, breakingColor);
        tag.setString(TAG_MAILBOX_WOOD, breakingWood);
        drop.setTagCompound(tag);
        ret.add(drop);

        return ret;
    }

    @Override
    // We override the lower level function to assure both entry points work!
    @SuppressWarnings("deprecation")
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos)
    {
        Item item = getItem(world, pos);

        if (item == null)
        {
            return null;
        }

        ItemStack stack = new ItemStack(item);

        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TEMailbox)
        {
            TEMailbox mailBox = (TEMailbox) te;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(TAG_MAILBOX_COLOR, mailBox.color);
            tag.setString(TAG_MAILBOX_WOOD, mailBox.getWood().name);
            stack.setTagCompound(tag);
        }

        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        BlockPlanks.EnumType[] aenumtype = BlockPlanks.EnumType.values();

        for (BlockPlanks.EnumType wood : aenumtype)
        {
            ItemStack stack;
            NBTTagCompound tag;
            if (MakersConfig.creativeMailboxes && tab == MakersMark.getCreativeTab())
            {
                for (EnumDyeColor color : EnumDyeColor.values())
                {
                    stack = new ItemStack(itemIn);
                    tag = new NBTTagCompound();
                    tag.setInteger(TAG_MAILBOX_COLOR, color.getMetadata());
                    tag.setString(TAG_MAILBOX_WOOD, enumToWood(wood).name);
                    stack.setTagCompound(tag);
                    list.add(stack);
                }
            }
            else
            {
                stack = new ItemStack(itemIn);
                tag = new NBTTagCompound();
                tag.setString(TAG_MAILBOX_WOOD, enumToWood(wood).name);
                stack.setTagCompound(tag);
                list.add(stack);
            }
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing) state.getValue(FACING)).getHorizontalIndex();
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, VARIANT, FACING);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                     float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getStateFromMeta(meta).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack
            stack)
    {
        if (!world.isRemote)
        {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TEMailbox)
            {
                TEMailbox mailBox = (TEMailbox) te;
                if (placer instanceof EntityPlayer)
                {
                    mailBox.owner = ((EntityPlayer) placer).getGameProfile();
                    if (!Utils.getPlayerPersistantData(((EntityPlayer) placer)).hasKey(InventoryMailbox.INVENTORY_NBT_TAG))
                    {
                        Utils.getPlayerPersistantData(((EntityPlayer) placer)).setTag(InventoryMailbox.INVENTORY_NBT_TAG, new NBTTagList());
                        ContainerMailbox.updateMailCount(mailBox.owner.getId(), 0);
                    }
                }
                mailBox.color = getColor(stack);
                mailBox.setWood(getWood(stack).name);
                if (stack.hasDisplayName())
                {
                    ((TEMailbox) te).setCustomInventoryName(stack.getDisplayName());
                }
            }
            world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
        }
    }

    private int getColor(ItemStack stack)
    {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(TAG_MAILBOX_COLOR))
        {
            return 15;
        }
        return tag.getInteger(TAG_MAILBOX_COLOR);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TEMailbox();
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TEMailbox)
        {
            TEMailbox mailBox = (TEMailbox) te;
            return state.withProperty(VARIANT, woodToEnum(mailBox.getWood()));
        }
        return state;
    }

    @Override
    public void commonRegister()
    {
        GameRegistry.registerBlock(this, ItemMailbox.class, getUnlocalizedNameRaw());
        GameRegistry.registerTileEntity(TEMailbox.class, MakersMark.MODID + ":mailbox_te");

        GameRegistry.addRecipe(new CraftingMailboxColors());
        RecipeSorter.register(MakersMark.MODID + ":mailbox_coloring", CraftingMailboxColors.class, RecipeSorter.Category.SHAPELESS, "");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientRegister()
    {
        Item item = Item.getItemFromBlock(this);

        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return new ModelResourceLocation(MakersMark.MODID + ":" + getWood(stack).name + "_mailbox", "inventory");
            }
        });

        TileEntityRenderer.register(TEMailbox.class);
    }

    public static ItemCoin.MaterialDefinition getWood(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag.hasKey(TAG_MAILBOX_WOOD))
            {
                ItemCoin.MaterialDefinition ret = ItemCoin.woods.get(tag.getString(TAG_MAILBOX_WOOD));
                if (ret != null)
                {
                    return ret;
                }
            }
        }
        return getDefaultWood();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            player.openGui(MakersMark.instance, GUIMailbox.GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public int getColor(ItemStack stack, int pass)
    {
        return pass != 1 ? 0xFFFFFF : TEMailbox.textColors[getColor(stack)];
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return 5;
    }

    public static BlockPlanks.EnumType woodToEnum(ItemCoin.MaterialDefinition inWood)
    {
        return BlockPlanks.EnumType.byMetadata(inWood.getIngot().getItemDamage());
    }

    public static ItemCoin.MaterialDefinition enumToWood(BlockPlanks.EnumType inType)
    {
        for (ItemCoin.MaterialDefinition def : ItemCoin.woods.values())
        {
            if (def.getIngot().getItemDamage() == inType.getMetadata())
            {
                return def;
            }
        }
        return getDefaultWood();
    }

    public static ItemCoin.MaterialDefinition getDefaultWood()
    {
        if (defaultWood == null)
        {
            for (ItemCoin.MaterialDefinition def : ItemCoin.woods.values())
            {
                if (def.getIngot().getItem() == Item.getItemFromBlock(Blocks.planks) && def.getIngot().getItemDamage() == 0)
                {
                    defaultWood = def;
                }
            }
        }
        return defaultWood;
    }

}
