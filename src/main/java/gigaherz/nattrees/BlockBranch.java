package gigaherz.nattrees;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockBranch
        extends Block
        implements ITileEntityProvider {

    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");
    public static final PropertyBool HAS_LEAVES = PropertyBool.create("has_leaves");
    public static final PropertyBool THICKNESS = PropertyBool.create("thickness");

    public BlockBranch(Material materialIn)
    {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(EAST, false)
                .withProperty(WEST, false)
                .withProperty(NORTH, false)
                .withProperty(SOUTH, false)
                .withProperty(UP, false)
                .withProperty(DOWN, false)
                .withProperty(HAS_LEAVES, false)
                .withProperty(THICKNESS, 0));
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileBranch();
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return worldIn.isSideSolid(pos.offset(side.getOpposite()), side, true);
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (canPlaceBlockOnSide(worldIn, pos, facing)) {
                return true;
            }
        }

        return false;
    }

    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        int thickness = meta&7;
        boolean hasLeaves = (meta&8) != 0;

        boolean w = this.canConnectTo(worldIn, pos, EnumFacing.WEST);
        boolean e = this.canConnectTo(worldIn, pos, EnumFacing.EAST);
        boolean n = this.canConnectTo(worldIn, pos, EnumFacing.NORTH);
        boolean s = this.canConnectTo(worldIn, pos, EnumFacing.SOUTH);
        boolean u = this.canConnectTo(worldIn, pos, EnumFacing.UP);
        boolean d = this.canConnectTo(worldIn, pos, EnumFacing.DOWN);

        return this.getDefaultState()
                .withProperty(WEST, w)
                .withProperty(EAST, e)
                .withProperty(NORTH, n)
                .withProperty(SOUTH, s)
                .withProperty(UP, u)
                .withProperty(DOWN, d)
                .withProperty(THICKNESS, thickness)
                .withProperty(HAS_LEAVES, hasLeaves);
    }

    private boolean canConnectTo(IBlockAccess worldIn, BlockPos thisPos, EnumFacing facing) {
        BlockPos pos = thisPos.offset(facing);

        Block block = worldIn.getBlockState(pos).getBlock();
        if (block == Blocks.barrier)
            return false;

        if (block instanceof BlockBranch)
            return ((BlockBranch)block).getThickness(worldIn, pos) >= getThickness(worldIn, thisPos);

        return block.isSideSolid(worldIn, pos, facing.getOpposite());
    }

    private int getThickness(IBlockAccess worldIn, BlockPos pos) {
        IBlockState state = worldIn.getBlockState(pos);
        return (Integer)state.getValue(THICKNESS);
    }

    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        int thickness = (Integer)state.getValue(THICKNESS);
        boolean hasLeaves = (Boolean)state.getValue(HAS_LEAVES);

        boolean w = this.canConnectTo(worldIn, pos, EnumFacing.WEST);
        boolean e = this.canConnectTo(worldIn, pos, EnumFacing.EAST);
        boolean n = this.canConnectTo(worldIn, pos, EnumFacing.NORTH);
        boolean s = this.canConnectTo(worldIn, pos, EnumFacing.SOUTH);
        boolean u = this.canConnectTo(worldIn, pos, EnumFacing.UP);
        boolean d = this.canConnectTo(worldIn, pos, EnumFacing.DOWN);

        worldIn.setBlockState(pos, state
                .withProperty(WEST, w)
                .withProperty(EAST, e)
                .withProperty(NORTH, n)
                .withProperty(SOUTH, s)
                .withProperty(UP, u)
                .withProperty(DOWN, d)
                .withProperty(THICKNESS, thickness)
                .withProperty(HAS_LEAVES, hasLeaves));
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        IBlockState state = worldIn.getBlockState(pos);
        boolean w = (Boolean)state.getValue(WEST);
        boolean e = (Boolean)state.getValue(EAST);
        boolean n = (Boolean)state.getValue(NORTH);
        boolean s = (Boolean)state.getValue(SOUTH);
        boolean u = (Boolean)state.getValue(UP);
        boolean d = (Boolean)state.getValue(DOWN);
        boolean hasLeaves = (Boolean) state.getValue(HAS_LEAVES);
        int thickness = (Integer) state.getValue(THICKNESS);

        if (hasLeaves)
            thickness = 7;

        float width = (thickness + 1) * 2 / 16.0f;
        float height = (thickness + 1) * 2 / 16.0f;
        float length = (thickness + 1) * 2 / 16.0f;

        float north = (1 - length)/2;
        float south = 1 - north;
        float west = (1 - width) / 2;
        float east = 1 - west;
        float bottom = (1 - height) / 2;
        float top = 1 - bottom;

        // TODO: account for connections

        this.setBlockBounds(west, bottom, north, east, top, south);
    }

    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity) {
        // TODO: add collision boxes for connections
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        boolean hasLeaves = (Boolean) state.getValue(HAS_LEAVES);
        if (hasLeaves) {
            ItemStack stack = playerIn.getHeldItem();
            if (stack != null && stack.stackSize > 0 && stack.getItem().getToolClasses(stack).contains("sword")) {
                worldIn.setBlockState(pos, state.withProperty(HAS_LEAVES, false));
                // TODO: pretend break leaf block
            }
        }
        return true;
    }

    private void notifyNeighbors(World worldIn, BlockPos pos, EnumFacing facing) {
        worldIn.notifyNeighborsOfStateChange(pos, this);
        worldIn.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this);
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(THICKNESS, meta & 7).withProperty(HAS_LEAVES, (meta & 8) > 0);
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        boolean w = this.canConnectTo(worldIn, pos, EnumFacing.WEST);
        boolean e = this.canConnectTo(worldIn, pos, EnumFacing.EAST);
        boolean n = this.canConnectTo(worldIn, pos, EnumFacing.NORTH);
        boolean s = this.canConnectTo(worldIn, pos, EnumFacing.SOUTH);
        boolean u = this.canConnectTo(worldIn, pos, EnumFacing.UP);
        boolean d = this.canConnectTo(worldIn, pos, EnumFacing.DOWN);

        return state
                .withProperty(WEST, w)
                .withProperty(EAST, e)
                .withProperty(NORTH, n)
                .withProperty(SOUTH, s)
                .withProperty(UP, u)
                .withProperty(DOWN, d);
    }

    public int getMetaFromState(IBlockState state) {

        int i = (Integer)state.getValue(THICKNESS);

        if ((Boolean) state.getValue(HAS_LEAVES)) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, NORTH, SOUTH, EAST, WEST, UP, DOWN, HAS_LEAVES, THICKNESS);
    }
}
