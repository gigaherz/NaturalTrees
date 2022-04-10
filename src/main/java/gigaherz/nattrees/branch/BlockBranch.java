package gigaherz.nattrees.branch;

import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockBranch extends Block implements IGrowable
{
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());
    public static final BooleanProperty HAS_LEAVES = BooleanProperty.create("has_leaves");
    public static final IntegerProperty THICKNESS = IntegerProperty.create("thickness", 0, 7);

    private boolean canHaveLeaves = true;

    public BlockBranch(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.DOWN)
                .setValue(HAS_LEAVES, false)
                .setValue(THICKNESS, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, HAS_LEAVES, THICKNESS);
    }

    @Deprecated
    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side)
    {
        return false;
    }


    @Override
    public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos)
    {
        return super.getDestroyProgress(state, player, worldIn, pos)
                * (getThickness(worldIn, pos) + 1) / 8.0f;
    }

    /*@Override*/
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, Direction side, int thickness)
    {
        if (!worldIn.getBlockState(pos).isAir(worldIn, pos))
            return false;

        BlockPos npos = pos.relative(side.getOpposite());
        BlockState state = worldIn.getBlockState(npos);
        Block block = state.getBlock();
        if (block instanceof BlockBranch)
        {
            return block == this && state.getValue(THICKNESS) >= thickness;
        }
        if (side != Direction.UP)
            return false;
        if (block != Blocks.DIRT && block != Blocks.GRASS_BLOCK)
            return false;
        return state.isFaceSturdy(worldIn, npos, side);
    }

    /*@Override*/
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos, int thickness)
    {
        for (Direction facing : Direction.values())
        {
            if (canPlaceBlockOnSide(worldIn, pos, facing, thickness))
            {
                return true;
            }
        }

        return false;
    }

    protected Direction getPreferredConnectionSide(IBlockReader worldIn, BlockPos pos, int thickness)
    {
        Direction face = Direction.DOWN;
        int preference = -1;
        int pref;

        pref = this.getConnectionValue(worldIn, pos, Direction.DOWN, thickness * 2 + 1);
        if (pref > preference)
        {
            face = Direction.DOWN;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, Direction.UP, thickness * 2 + 1);
        if (pref > preference)
        {
            face = Direction.UP;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, Direction.WEST, thickness * 2 + 1);
        if (pref > preference)
        {
            face = Direction.WEST;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, Direction.EAST, thickness * 2 + 1);
        if (pref > preference)
        {
            face = Direction.EAST;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, Direction.NORTH, thickness * 2 + 1);
        if (pref > preference)
        {
            face = Direction.NORTH;
            preference = pref;
        }

        pref = this.getConnectionValue(worldIn, pos, Direction.SOUTH, thickness * 2 + 1);
        if (pref > preference)
        {
            face = Direction.SOUTH;
            preference = pref;
        }

        if (preference < thickness * 2)
            return Direction.DOWN;

        return face;
    }

    private int getConnectionValue(IBlockReader worldIn, BlockPos thisPos, Direction facing, int sideValue)
    {
        BlockPos pos = thisPos.relative(facing);

        BlockState state = worldIn.getBlockState(pos);

        Block block = state.getBlock();
        if (block == Blocks.BARRIER)
            return -1;

        if (block instanceof BlockBranch)
        {
            if (block == this)
                return getThickness(worldIn, pos) * 2;
            else return -1;
        }

        if (state.isFaceSturdy(worldIn, pos, facing))
            return sideValue;

        return -1;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState();
        return state.setValue(FACING, context.getClickedFace().getOpposite());
    }

    private static Map<BlockState, VoxelShape> SHAPE_CACHE = Maps.newHashMap();

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE_CACHE.computeIfAbsent(state, (blockState) -> {
            boolean hasLeaves = blockState.getValue(HAS_LEAVES);

            float west, down, north, east, up, south;
            if (hasLeaves)
            {
                west = down = north = 0;
                east = up = south = 1;
            }
            else
            {
                int thickness = blockState.getValue(THICKNESS);

                float width = (thickness + 1) * 2 / 16.0f;

                north = (1 - width) / 2;
                south = 1 - north;
                west = (1 - width) / 2;
                east = 1 - west;
                down = (1 - width) / 2;
                up = 1 - down;
            }

            switch (state.getValue(FACING))
            {
                case EAST:
                    east = 1;
                    break;
                case WEST:
                    west = 0;
                    break;
                case NORTH:
                    north = 0;
                    break;
                case SOUTH:
                    south = 1;
                    break;
                case UP:
                    up = 1;
                    break;
                case DOWN:
                    down = 0;
                    break;
            }

            return VoxelShapes.create(new AxisAlignedBB(
                    west, down, north,
                    east, up, south));
        });
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        boolean hasLeaves = state.getValue(HAS_LEAVES);

        ItemStack stack = player.getItemInHand(handIn);
        if (stack.getCount() > 0 && stack.getItem().getToolTypes(stack).contains("sword"))
        {
            if (hasLeaves)
            {
                worldIn.setBlockAndUpdate(pos, state.setValue(HAS_LEAVES, false));
                // TODO: pretend break leaf block
                // BlockLeaves.dropBlockAsItem
            }
        }

        if (canHaveLeaves(worldIn, pos, state))
        {
            if (stack.getCount() > 0 && stack.getItem() instanceof BlockItem)
            {
                BlockItem ib = (BlockItem) stack.getItem();
                if (ib.getBlock() instanceof LeavesBlock && !stack.hasTag())
                {
                    stack.shrink(1);
                    worldIn.setBlockAndUpdate(pos, state.setValue(HAS_LEAVES, true));
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.FAIL;
    }

    private boolean canHaveLeaves(World worldIn, BlockPos pos, @Nullable BlockState state)
    {
        if (state == null)
            state = worldIn.getBlockState(pos);

        return canHaveLeaves && state.getValue(THICKNESS) < 6;
    }

    public int getThickness(IBlockReader worldIn, BlockPos pos)
    {
        BlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() != this)
            return 0;
        return state.getValue(THICKNESS);
    }


    public boolean getHasLeaves(World worldIn, BlockPos pos)
    {
        BlockState state = worldIn.getBlockState(pos);
        return state.getValue(HAS_LEAVES);
    }

    @Override
    public boolean isValidBonemealTarget(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return state.getValue(THICKNESS) < 7;
    }

    @Override
    public boolean isBonemealSuccess(World worldIn, Random rand, BlockPos pos, BlockState state)
    {
        return state.getValue(THICKNESS) < 7;
    }

    @Override
    public void performBonemeal(ServerWorld world, Random random, BlockPos pos, BlockState state)
    {
        int thickness = state.getValue(THICKNESS);
        world.setBlockAndUpdate(pos, state.setValue(THICKNESS, thickness + 1));
    }

    public Item createItemBlock()
    {
        return new BlockItem(this, new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)).setRegistryName(getRegistryName());
    }
}
