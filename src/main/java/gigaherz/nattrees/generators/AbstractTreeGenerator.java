package gigaherz.nattrees.generators;

import gigaherz.nattrees.branch.BlockBranch;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class AbstractTreeGenerator<T extends BranchInfo<T>> implements ITreeGenerator
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

        info.gen.world.setBlockState(info.pos, branchBlock.getDefaultState()
                .with(BlockBranch.THICKNESS, info.thickness)
                .with(BlockBranch.HAS_LEAVES, info.leaves)
                .with(BlockBranch.FACING, info.facing.getOpposite()), info.gen.placeFlags);
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

    protected ActionResultType processQueue(T initial)
    {
        pending.add(initial);

        return tickInternal() > 0 ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }

    private int tickInternal()
    {
        int placed = 0;

        while (pending.size() > 0 && placed < 5)
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
        return branchBlock.canPlaceBlockOnSide(info.gen.world, info.pos.offset(testFacing), testFacing, info.thickness);
    }

    protected abstract boolean getWillHaveLeaves(T info);

    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, Direction side)
    {
        BlockPos npos = pos.offset(side.getOpposite());
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

    public boolean canSpawnTreeAt(World worldIn, BlockPos pos)
    {
        for (Direction facing : Direction.values())
        {
            if (canPlaceBlockOnSide(worldIn, pos.offset(facing.getOpposite()), facing))
            {
                return true;
            }
        }

        return false;
    }

    protected static class GenerationInfo
    {
        public final World world;
        public final Random rand;
        public final int tallness;
        public final double spreadness;
        public final BlockPos root;
        public final int placeFlags;

        public GenerationInfo(World world, BlockPos root, int tallness, double spreadness, int placeFlags, Random rand)
        {
            this.world = world;
            this.rand = rand;
            this.tallness = tallness;
            this.spreadness = spreadness;
            this.root = root;
            this.placeFlags = placeFlags;
        }
    }
}
