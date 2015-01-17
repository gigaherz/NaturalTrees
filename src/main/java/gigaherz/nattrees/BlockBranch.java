package gigaherz.nattrees;

import net.minecraft.block.Block;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public class BlockBranch
        extends Block
        implements IGrowable {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool HAS_LEAVES = PropertyBool.create("has_leaves");
    public static final PropertyInteger THICKNESS = PropertyInteger.create("thickness", 0, 7);

    public BlockBranch(Material materialIn) {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FACING, EnumFacing.DOWN)
                .withProperty(HAS_LEAVES, false)
                .withProperty(THICKNESS, 0));
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        BlockPos npos = pos.offset(side.getOpposite());
        Block block = worldIn.getBlockState(npos).getBlock();
        if (block instanceof BlockBranch)
            return block.getUnlocalizedName().equals(getUnlocalizedName());
        return worldIn.isSideSolid(npos, side, true);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (canPlaceBlockOnSide(worldIn, pos, facing)) {
                return true;
            }
        }

        return false;
    }

    protected EnumFacing getPreferredConnectionSide(IBlockAccess worldIn, BlockPos pos, int thickness){

        EnumFacing face = EnumFacing.DOWN;
        int preference = -1;

        int pref = this.getConnectionValue(worldIn, pos, EnumFacing.WEST);
        if (pref > preference) {
            face = EnumFacing.WEST;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, EnumFacing.EAST);
        if (pref > preference) {
            face = EnumFacing.EAST;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, EnumFacing.NORTH);
        if (pref > preference) {
            face = EnumFacing.NORTH;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, EnumFacing.SOUTH);
        if (pref > preference) {
            face = EnumFacing.SOUTH;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, EnumFacing.UP);
        if (pref > preference) {
            face = EnumFacing.UP;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, EnumFacing.DOWN);
        if (pref > preference) {
            face = EnumFacing.DOWN;
            preference = pref;
        }

        if(preference < thickness)
            return EnumFacing.DOWN;

        return face;
    }


    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        int thickness = meta & 7;
        boolean hasLeaves = (meta & 8) != 0;

        EnumFacing face = getPreferredConnectionSide(worldIn, pos, thickness);

        return this.getDefaultState()
                .withProperty(FACING, face)
                .withProperty(THICKNESS, thickness)
                .withProperty(HAS_LEAVES, hasLeaves);
    }

    private boolean canConnectTo(IBlockAccess worldIn, BlockPos thisPos, EnumFacing facing, int thickness) {
        return canConnectTo(worldIn, thisPos, facing, thickness, false);
    }
    private boolean canConnectTo(IBlockAccess worldIn, BlockPos thisPos, EnumFacing facing, int thickness, boolean branchOnly) {
        BlockPos pos = thisPos.offset(facing);

        Block block = worldIn.getBlockState(pos).getBlock();
        if (block == Blocks.barrier)
            return false;

        if (block instanceof BlockBranch)
            return block.getUnlocalizedName().equals(getUnlocalizedName())
                    && ((BlockBranch) block).getThickness(worldIn, pos) >= thickness;

        if(branchOnly)
            return false;

        return block.isSideSolid(worldIn, pos, facing.getOpposite());
    }

    private int getConnectionValue(IBlockAccess worldIn, BlockPos thisPos, EnumFacing facing) {
        BlockPos pos = thisPos.offset(facing);

        Block block = worldIn.getBlockState(pos).getBlock();
        if (block == Blocks.barrier)
            return -1;

        if (block instanceof BlockBranch
                && block.getUnlocalizedName().equals(getUnlocalizedName()))
        {
            return ((BlockBranch) block).getThickness(worldIn, pos);
        }

        if (block.isSideSolid(worldIn, pos, facing.getOpposite()))
            return 0;

        return -1;
    }

    private int getThickness(IBlockAccess worldIn, BlockPos pos) {
        IBlockState state = worldIn.getBlockState(pos);
        return (Integer) state.getValue(THICKNESS);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        int thickness = (Integer) state.getValue(THICKNESS);
        boolean hasLeaves = (Boolean) state.getValue(HAS_LEAVES);

        EnumFacing face = getPreferredConnectionSide(worldIn, pos, thickness);

        worldIn.setBlockState(pos, state
                .withProperty(FACING, face)
                .withProperty(THICKNESS, thickness)
                .withProperty(HAS_LEAVES, hasLeaves));
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        IBlockState state = worldIn.getBlockState(pos);
        EnumFacing facing = (EnumFacing) state.getValue(FACING);
        boolean hasLeaves = (Boolean) state.getValue(HAS_LEAVES);
        int thickness = (Integer) state.getValue(THICKNESS);

        if (hasLeaves)
            thickness = 7;

        float width = (thickness + 1) * 2 / 16.0f;

        float north = (1 - width) / 2;
        float south = 1 - north;
        float west = (1 - width) / 2;
        float east = 1 - west;
        float down = (1 - width) / 2;
        float up = 1 - down;

        if (facing == EnumFacing.DOWN)
            down = -down;
        else if (facing == EnumFacing.UP)
            up = (1 - up) + 1;
        else if (facing == EnumFacing.WEST)
            west = -west;
        else if (facing == EnumFacing.EAST)
            east = (1 - east) + 1;
        else if (facing == EnumFacing.NORTH)
            north = -north;
        else if (facing == EnumFacing.SOUTH)
            south = (1 - south) + 1;

        this.setBlockBounds(west, down, north, east, up, south);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        boolean hasLeaves = (Boolean) state.getValue(HAS_LEAVES);

        ItemStack stack = playerIn.getHeldItem();
        if (stack != null && stack.stackSize > 0 && stack.getItem().getToolClasses(stack).contains("sword")) {
            if (hasLeaves) {
                worldIn.setBlockState(pos, state.withProperty(HAS_LEAVES, false));
                // TODO: pretend break leaf block
            }
        }
        ;
        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(THICKNESS, meta & 7).withProperty(HAS_LEAVES, (meta & 8) > 0);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        int thickness = (Integer) state.getValue(THICKNESS);

        EnumFacing face = getPreferredConnectionSide(worldIn, pos, thickness);

        state = state.withProperty(FACING, face);

        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {

        int i = (Integer) state.getValue(THICKNESS);

        if ((Boolean) state.getValue(HAS_LEAVES)) {
            i |= 8;
        }

        return i;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, FACING, HAS_LEAVES, THICKNESS);
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return getThickness(worldIn, pos) < 7;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return getThickness(worldIn, pos) < 7;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        worldIn.setBlockState(pos, state.withProperty(THICKNESS, getThickness(worldIn, pos) + 1), 3);
    }
}
