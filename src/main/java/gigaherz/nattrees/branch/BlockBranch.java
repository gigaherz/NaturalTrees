package gigaherz.nattrees.branch;

import com.google.common.collect.Maps;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraftforge.common.ToolActions;

public class BlockBranch extends Block implements BonemealableBlock
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
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
    public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos)
    {
        return super.getDestroyProgress(state, player, worldIn, pos)
                * (getThickness(worldIn, pos) + 1) / 8.0f;
    }

    /*@Override*/
    public boolean canPlaceBlockOnSide(Level worldIn, BlockPos pos, Direction side, int thickness)
    {
        if (!worldIn.getBlockState(pos).isAir())
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
    public boolean canPlaceBlockAt(Level worldIn, BlockPos pos, int thickness)
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

    protected Direction getPreferredConnectionSide(BlockGetter worldIn, BlockPos pos, int thickness)
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

    private int getConnectionValue(BlockGetter worldIn, BlockPos thisPos, Direction facing, int sideValue)
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
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();
        return state.setValue(FACING, context.getClickedFace().getOpposite());
    }

    private static Map<BlockState, VoxelShape> SHAPE_CACHE = Maps.newHashMap();

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
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

            return Shapes.create(new AABB(
                    west, down, north,
                    east, up, south));
        });
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        boolean hasLeaves = state.getValue(HAS_LEAVES);

        ItemStack stack = player.getItemInHand(handIn);
        if (stack.getCount() > 0 && stack.getItem().canPerformAction(stack, ToolActions.SWORD_DIG))
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
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.FAIL;
    }

    private boolean canHaveLeaves(Level worldIn, BlockPos pos, @Nullable BlockState state)
    {
        if (state == null)
            state = worldIn.getBlockState(pos);

        return canHaveLeaves && state.getValue(THICKNESS) < 6;
    }

    public int getThickness(BlockGetter worldIn, BlockPos pos)
    {
        BlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() != this)
            return 0;
        return state.getValue(THICKNESS);
    }


    public boolean getHasLeaves(Level worldIn, BlockPos pos)
    {
        BlockState state = worldIn.getBlockState(pos);
        return state.getValue(HAS_LEAVES);
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return state.getValue(THICKNESS) < 7;
    }

    @Override
    public boolean isBonemealSuccess(Level worldIn, Random rand, BlockPos pos, BlockState state)
    {
        return state.getValue(THICKNESS) < 7;
    }

    @Override
    public void performBonemeal(ServerLevel world, Random random, BlockPos pos, BlockState state)
    {
        int thickness = state.getValue(THICKNESS);
        world.setBlockAndUpdate(pos, state.setValue(THICKNESS, thickness + 1));
    }

    public Item createItemBlock()
    {
        return new BlockItem(this, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)).setRegistryName(getRegistryName());
    }
}
