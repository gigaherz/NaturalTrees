package gigaherz.nattrees.generators;

import gigaherz.nattrees.BlockBranch;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class TreeGeneratorBase
{

    final Block whichBranch;

    protected TreeGeneratorBase(@Nonnull Block which)
    {
        whichBranch = which;
    }

    protected List<EnumFacing> findValidGrowthDirections(World worldIn, BlockPos pos, Random rand)
    {

        // Valid choices: East, West, North, South, Up, NEVER DOWN
        List<EnumFacing> newFacings = new ArrayList<EnumFacing>();

        testFacing(worldIn, pos, newFacings, EnumFacing.UP);
        testFacing(worldIn, pos, newFacings, EnumFacing.DOWN);
        testFacing(worldIn, pos, newFacings, EnumFacing.EAST);
        testFacing(worldIn, pos, newFacings, EnumFacing.WEST);
        testFacing(worldIn, pos, newFacings, EnumFacing.NORTH);
        testFacing(worldIn, pos, newFacings, EnumFacing.SOUTH);

        int c = newFacings.size();
        for (int i = c - 1; i > 0; i--)
        {
            int j = rand.nextInt(i + 1);
            if (j != i)
            {
                EnumFacing t = newFacings.get(i);
                newFacings.set(i, newFacings.get(j));
                newFacings.set(j, t);
            }
        }

        return newFacings;
    }

    protected void testFacing(World worldIn, BlockPos pos, List<EnumFacing> newFacings, EnumFacing testFacing)
    {
        if (worldIn.canBlockBePlaced(whichBranch, pos.offset(testFacing), false, testFacing, null, null))
            newFacings.add(testFacing);
    }

    protected boolean placeBranch(World worldIn, BranchInfo info, boolean leaves)
    {
        if (!worldIn.canBlockBePlaced(whichBranch, info.pos, false, info.facing, null, null))
            return false;

        int meta = BlockBranch.getMetaFromProperties(info.thickness, leaves);
        worldIn.setBlockState(info.pos, whichBranch.onBlockPlaced(worldIn, info.pos, info.facing, info.pos.getX(), info.pos.getY(), info.pos.getZ(), meta, null), 2);
        return true;
    }

    protected void addBranchToPending(Queue<BranchInfo> pending, BlockPos pos, EnumFacing facing, int length, int thickness)
    {
        boolean found = false;
        for (BranchInfo p : pending)
        {
            if (p.pos.equals(pos) && p.thickness < thickness)
            {
                p.thickness = thickness;
                p.facing = facing;
                p.length = Math.max(p.length, length);
                found = true;
            }
        }
        if (!found)
            pending.add(new BranchInfo(pos, facing, thickness, length + 1));
    }

    public abstract boolean generateTreeAt(World worldIn, BlockPos startPos, Random rand);

    protected boolean processQueue(World worldIn, Random rand, BranchInfo initial, int tallness, double spreadness, BlockPos centerPos)
    {
        Queue<BranchInfo> pending = new ArrayDeque<BranchInfo>();

        int placed = 0;

        pending.add(initial);
        while (pending.size() > 0)
        {
            BranchInfo info = pending.remove();

            boolean leaves = getWillHaveLeaves(info);

            if (!placeBranch(worldIn, info, leaves))
                continue;

            placed++;

            BlockPos pos = info.pos;
            EnumFacing facing = info.facing;
            int thickness = info.thickness;
            int length = info.length;
            List<EnumFacing> newFacings = findValidGrowthDirections(worldIn, pos, rand);
            for (EnumFacing newFacing : newFacings)
            {

                if (shouldSkipFacing(length, tallness, facing, newFacing)) continue;


                int thick = getRandomThicknessForFacing(pos, facing, rand, newFacing, thickness, length, tallness, spreadness, centerPos);

                if (thick > thickness)
                    thick = thickness;

                if (thick >= 0)
                {
                    BlockPos newPos = pos.offset(newFacing);

                    addBranchToPending(pending, newPos, newFacing, length, thick);
                }
            }
        }
        return placed > 0;
    }

    protected abstract boolean shouldSkipFacing(int length, int tallness, EnumFacing facing, EnumFacing newFacing);

    protected abstract boolean getWillHaveLeaves(BranchInfo info);

    protected abstract int getRandomThicknessForFacing(BlockPos pos, EnumFacing facing, Random rand, EnumFacing newFacing, int thickness, int length, int tallness, double spreadness, BlockPos centerPos);

    protected double computeDistanceFromCenter(BlockPos centerPos, BlockPos pos)
    {
        double X0 = centerPos.getX();
        double Y0 = centerPos.getY();
        double Z0 = centerPos.getZ();
        double X1 = pos.getX();
        double Y1 = pos.getY();
        double Z1 = pos.getZ();

        double dx = (X1 - X0) * 1.0;
        double dy = (Y1 - Y0) * 1.0;
        double dz = (Z1 - Z0) * 1.0;

        double dd = (dx * dx + dy * dy + dz * dz);

        return Math.sqrt(dd);
    }

    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        BlockPos npos = pos.offset(side.getOpposite());
        Block block = worldIn.getBlockState(npos).getBlock();
        if (block instanceof BlockBranch)
        {
            return block.getUnlocalizedName().equals(whichBranch.getUnlocalizedName()) &&
                    ((BlockBranch) block).getThickness(worldIn, npos) > 0 &&
                    !((BlockBranch) block).getHasLeaves(worldIn, npos);
        }
        if (side != EnumFacing.UP)
            return false;
        if (block != Blocks.dirt && block != Blocks.grass)
            return false;
        return worldIn.isSideSolid(npos, side, true);
    }

    public boolean canSpawnTreeAt(World worldIn, BlockPos pos)
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

    public TreeGeneratorBase combineWith(TreeGeneratorBase other, float chanceAlternative)
    {
        return new RandomTreeGenerator(this, other, chanceAlternative);
    }

    protected class BranchInfo
    {
        public BlockPos pos;
        public EnumFacing facing;
        public int thickness;
        public int length;

        public BranchInfo(BlockPos pos, EnumFacing facing, int thickness, int length)
        {
            this.pos = pos;
            this.facing = facing;
            this.thickness = thickness;
            this.length = length;
        }
    }
}
