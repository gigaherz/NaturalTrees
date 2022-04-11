package gigaherz.nattrees.generators;

import gigaherz.nattrees.branch.BlockBranch;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public abstract class AbstractTreeGenerator<T extends AbstractTreeGenerator.BranchInfo<T>> implements ITreeGenerator
{
    final BlockBranch branchBlock;

    final Queue<T> pending = new ArrayDeque<>();

    protected AbstractTreeGenerator(@Nonnull BlockBranch which)
    {
        branchBlock = which;
        MinecraftForge.EVENT_BUS.addListener(this::tick);
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
            tickInternal();
    }

    protected boolean placeBranch(T info)
    {
        if (!branchBlock.canPlaceBlockOnSide(info.gen.world, info.pos, info.facing, info.thickness))
            return false;

        info.gen.world.setBlock(info.pos, branchBlock.defaultBlockState()
                .setValue(BlockBranch.THICKNESS, info.thickness)
                .setValue(BlockBranch.HAS_LEAVES, info.leaves)
                .setValue(BlockBranch.FACING, info.facing.getOpposite()), info.gen.placeFlags);
        return true;
    }

    protected void enqueueBranch(T newBranch)
    {
        boolean found = false;
        for (T p : pending)
        {
            if (p.pos.equals(newBranch.pos) && p.thickness < newBranch.thickness)
            {
                p.thickness = newBranch.thickness;
                p.facing = newBranch.facing;
                p.length = Math.max(p.length, newBranch.length);
                found = true;
                break;
            }
        }
        if (!found)
        {
            newBranch.leaves = getWillHaveLeaves(newBranch);
            pending.add(newBranch);
        }
    }

    protected InteractionResult processQueue(T initial)
    {
        pending.add(initial);

        return tickInternal() > 0 ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    private int tickInternal()
    {
        int placed = 0;

        while (pending.size() > 0 && placed < 30)
        {
            T info = pending.remove();

            if (!placeBranch(info))
                continue;

            info.grow();

            placed++;
        }

        return placed;
    }

    protected boolean testFacing(T info, Direction testFacing)
    {
        return branchBlock.canPlaceBlockOnSide(info.gen.world, info.pos.relative(testFacing), testFacing, info.thickness);
    }

    protected abstract boolean getWillHaveLeaves(T info);

    public boolean canPlaceBlockOnSide(Level worldIn, BlockPos pos, Direction side)
    {
        BlockPos npos = pos.relative(side.getOpposite());
        Block block = worldIn.getBlockState(npos).getBlock();
        if (block instanceof BlockBranch)
        {
            return block == branchBlock &&
                    branchBlock.getThickness(worldIn, npos) > 0 &&
                    !branchBlock.getHasLeaves(worldIn, npos);
        }
        if (side != Direction.UP)
            return false;
        if (block != Blocks.DIRT && block != Blocks.GRASS_BLOCK)
            return false;
        return true; // worldIn.isSideSolid(npos, side, true);
    }

    private final Direction[] placePreferences = {
            Direction.UP,
            Direction.EAST,
            Direction.NORTH,
            Direction.WEST,
            Direction.SOUTH,
            Direction.DOWN
    };
    public boolean canSpawnTreeAt(Level worldIn, BlockPos pos)
    {
        for (Direction facing : placePreferences)
        {
            if (canPlaceBlockOnSide(worldIn, pos, facing))
            {
                return true;
            }
        }

        return false;
    }

    protected record GenerationInfo(
            Level world,
            BlockPos root,
            int tallness,
            double spreadness,
            int placeFlags,
            Random rand,
            int startThickness)
    {
    }

    abstract static class BranchInfo<T extends BranchInfo<T>>
    {
        public final AbstractTreeGenerator<T> owner;
        public final GenerationInfo gen;
        public final BlockPos pos;
        public Direction facing;
        public int thickness;
        public boolean leaves;
        public int length;

        protected BranchInfo(AbstractTreeGenerator<T> owner, GenerationInfo gen, BlockPos pos, Direction facing, int thickness, int length)
        {
            this.owner = owner;
            this.gen = gen;
            this.pos = pos;
            this.facing = facing;
            this.thickness = thickness;
            this.length = length;
        }

        public double horizontalDistanceFromCenter()
        {
            double dx = pos.getX()-gen.root.getX();
            double dz = pos.getZ()-gen.root.getZ();
            return Math.sqrt(dx*dx+dz*dz);
        }

        public double computeDistanceFromCenter()
        {
            double dx = (double) pos.getX() - (double) gen.root.getX();
            double dy = (double) pos.getY() - (double) gen.root.getY();
            double dz = (double) pos.getZ() - (double) gen.root.getZ();
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }

        public void grow()
        {
            findValidGrowthDirections().forEach(offshoot ->
            {
                if (offshoot != null && offshoot.thickness >= 0)
                {
                    owner.enqueueBranch(offshoot);
                }
            });
        }

        @Nullable
        protected abstract T getRandomBranchForFacing(Direction newFacing);

        @SuppressWarnings("unchecked")
        protected final T getSelf()
        {
            return (T)this;
        }

        protected Stream<T> findValidGrowthDirections()
        {
            return Arrays.stream(Direction.values())
                    .filter(d -> owner.testFacing(getSelf(), d))
                    .map(this::getRandomBranchForFacing)
                    .filter(Objects::nonNull);
        }
    }
}
