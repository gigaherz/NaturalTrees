package gigaherz.nattrees;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public class BlockBranch
        extends Block
        implements IGrowable
{

    public static int getMetaFromProperties(int thickness, boolean leaves)
    {
        if (leaves)
            thickness |= 8;
        return thickness;
    }

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool HAS_LEAVES = PropertyBool.create("has_leaves");
    public static final PropertyInteger THICKNESS = PropertyInteger.create("thickness", 0, 7);

    public final Variant variant;

    public BlockBranch(Material materialIn, Variant variant, String unlocName)
    {
        super(materialIn);
        this.variant = variant;
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FACING, EnumFacing.DOWN)
                .withProperty(HAS_LEAVES, false)
                .withProperty(THICKNESS, 0));
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setLightOpacity(1);
        this.setHardness(4);
        this.setStepSound(Block.soundTypeWood);
        this.setUnlocalizedName(unlocName);
        this.setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(THICKNESS, meta & 7).withProperty(HAS_LEAVES, (meta & 8) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = state.getValue(THICKNESS);

        if (state.getValue(HAS_LEAVES))
            i |= 8;

        return i;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        int thickness = state.getValue(THICKNESS);

        EnumFacing face = getPreferredConnectionSide(worldIn, pos, thickness);

        state = state.withProperty(FACING, face);

        return state;
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, FACING, HAS_LEAVES, THICKNESS);
    }

    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
        return ColorizerFoliage.getFoliageColor(0.5D, 1.0D);
    }

    @SideOnly(Side.CLIENT)
    public int getRenderColor(IBlockState state)
    {
        switch (variant)
        {
            case BIRCH:
                return ColorizerFoliage.getFoliageColorBirch();
            case SPRUCE:
                return ColorizerFoliage.getFoliageColorPine();
        }
        return ColorizerFoliage.getFoliageColorBasic();
    }

    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
    {
        return BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
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
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @Override
    public float getBlockHardness(World worldIn, BlockPos pos)
    {
        return this.blockHardness * (getThickness(worldIn, pos) + 1) / 8.0f;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        BlockPos npos = pos.offset(side.getOpposite());
        Block block = worldIn.getBlockState(npos).getBlock();
        if (block instanceof BlockBranch)
        {
            return block == this;
        }
        if (side != EnumFacing.UP)
            return false;
        if (block != Blocks.dirt && block != Blocks.grass)
            return false;
        return worldIn.isSideSolid(npos, side, true);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        for (EnumFacing facing : EnumFacing.values())
        {
            if (canPlaceBlockOnSide(worldIn, pos, facing))
            {
                return true;
            }
        }

        return false;
    }

    protected EnumFacing getPreferredConnectionSide(IBlockAccess worldIn, BlockPos pos, int thickness)
    {
        EnumFacing face = EnumFacing.DOWN;
        int preference = -1;
        int pref;

        pref = this.getConnectionValue(worldIn, pos, EnumFacing.DOWN, thickness * 2 + 1);
        if (pref > preference)
        {
            face = EnumFacing.DOWN;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, EnumFacing.UP, thickness * 2 + 1);
        if (pref > preference)
        {
            face = EnumFacing.UP;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, EnumFacing.WEST, thickness * 2 + 1);
        if (pref > preference)
        {
            face = EnumFacing.WEST;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, EnumFacing.EAST, thickness * 2 + 1);
        if (pref > preference)
        {
            face = EnumFacing.EAST;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, EnumFacing.NORTH, thickness * 2 + 1);
        if (pref > preference)
        {
            face = EnumFacing.NORTH;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, EnumFacing.SOUTH, thickness * 2 + 1);
        if (pref > preference)
        {
            face = EnumFacing.SOUTH;
            preference = pref;
        }

        if (preference < thickness * 2)
            return EnumFacing.DOWN;

        return face;
    }

    private int getConnectionValue(IBlockAccess worldIn, BlockPos thisPos, EnumFacing facing, int sideValue)
    {
        BlockPos pos = thisPos.offset(facing);

        Block block = worldIn.getBlockState(pos).getBlock();
        if (block == Blocks.barrier)
            return -1;

        if (block instanceof BlockBranch)
        {
            if (block.getUnlocalizedName().equals(getUnlocalizedName()))
                return ((BlockBranch) block).getThickness(worldIn, pos) * 2;
            else return -1;
        }

        if (block.isSideSolid(worldIn, pos, facing.getOpposite()))
            return sideValue;

        return -1;
    }


    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        int thickness = meta & 7;
        boolean hasLeaves = (meta & 8) != 0;

        return this.getDefaultState()
                .withProperty(THICKNESS, thickness)
                .withProperty(HAS_LEAVES, hasLeaves);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        IBlockState state = worldIn.getBlockState(pos);
        AxisAlignedBB aabb = getBB(0, 0, 0, state);
        setBlockBounds(
                (float)aabb.minX,
                (float)aabb.minY,
                (float)aabb.minZ,
                (float)aabb.maxX,
                (float)aabb.maxY,
                (float)aabb.maxZ);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos)
    {
        IBlockState state = worldIn.getBlockState(pos);
        return getBB(pos.getX(), pos.getY(), pos.getZ(), state);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        return getBB(pos.getX(), pos.getY(), pos.getZ(), state);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    {
        AxisAlignedBB aabb = getBB(pos.getX(), pos.getY(), pos.getZ(), state);

        if (aabb != null && mask.intersectsWith(aabb))
        {
            list.add(aabb);
        }
    }

    public AxisAlignedBB getBB(int x, int y, int z, IBlockState state)
    {
        boolean hasLeaves = state.getValue(HAS_LEAVES);

        float west, down, north, east, up, south;
        if (hasLeaves)
        {
            west = down = north = 0;
            east = up = south = 1;
        }
        else
        {
            //EnumFacing facing = state.getValue(FACING);
            int thickness = state.getValue(THICKNESS);

            float width = (thickness + 1) * 2 / 16.0f;

            north = (1 - width) / 2;
            south = 1 - north;
            west = (1 - width) / 2;
            east = 1 - west;
            down = (1 - width) / 2;
            up = 1 - down;

            /*if (facing == EnumFacing.DOWN)
                down = 0;
            else if (facing == EnumFacing.UP)
                up = 1;
            else if (facing == EnumFacing.WEST)
                west = 0;
            else if (facing == EnumFacing.EAST)
                east = 1;
            else if (facing == EnumFacing.NORTH)
                north = 0;
            else if (facing == EnumFacing.SOUTH)
                south = 1;*/
        }

        return new AxisAlignedBB(
                x+west, y+down, z+north,
                x+east, y+up, z+south);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        boolean hasLeaves = state.getValue(HAS_LEAVES);

        ItemStack stack = playerIn.getHeldItem();
        if (stack != null && stack.stackSize > 0 && stack.getItem().getToolClasses(stack).contains("sword"))
        {
            if (hasLeaves)
            {
                worldIn.setBlockState(pos, state.withProperty(HAS_LEAVES, false));
                // TODO: pretend break leaf block
                // BlockLeaves.dropBlockAsItem
            }
        }

        if (canHaveLeaves(worldIn, pos, state))
        {
            if (stack != null && stack.stackSize > 0 && stack.getItem() instanceof ItemBlock)
            {
                ItemBlock ib = (ItemBlock) stack.getItem();
                if (ib.getBlock() instanceof BlockLeavesBase && !stack.hasTagCompound())
                {
                    stack.stackSize--;
                    worldIn.setBlockState(pos, state.withProperty(HAS_LEAVES, true));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canHaveLeaves(World worldIn, BlockPos pos, IBlockState state)
    {
        if (state == null)
            state = worldIn.getBlockState(pos);

        return variant.canHaveLeaves() && state.getValue(THICKNESS) < 7;
    }

    public int getThickness(IBlockAccess worldIn, BlockPos pos)
    {
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() != this)
            return 0;
        return state.getValue(THICKNESS);
    }


    public boolean getHasLeaves(World worldIn, BlockPos pos)
    {
        IBlockState state = worldIn.getBlockState(pos);
        return state.getValue(HAS_LEAVES);
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return state.getValue(THICKNESS) < 7;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return state.getValue(THICKNESS) < 7;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        int thickness = state.getValue(THICKNESS);
        worldIn.setBlockState(pos, state.withProperty(THICKNESS, thickness + 1));
    }

    public enum Variant implements IStringSerializable
    {
        OAK("oak"),
        BIRCH("birch"),
        SPRUCE("spruce"),
        JUNGLE("jungle"),
        DARK_OAK("dark_oak"),
        ACACIA("acacia");

        // TODO: CACTUS?

        private final String name;
        private final boolean canHaveLeaves;

        Variant(String name)
        {
            this.name = name;
            this.canHaveLeaves = true;
        }

        // For future use with CACTUS and such
        Variant(String name, boolean canHaveLeaves)
        {
            this.name = name;
            this.canHaveLeaves = canHaveLeaves;
        }

        @Override
        public String toString()
        {
            return name;
        }

        @Override
        public String getName()
        {
            return name;
        }

        public boolean canHaveLeaves()
        {
            return canHaveLeaves;
        }

        public static Variant[] values = values();
    }
}
