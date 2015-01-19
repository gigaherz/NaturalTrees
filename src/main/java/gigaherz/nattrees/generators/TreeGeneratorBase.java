package gigaherz.nattrees.generators;

import gigaherz.nattrees.BlockBranch;
import gigaherz.nattrees.NaturalTrees;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import scala.reflect.internal.TreeGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public abstract class TreeGeneratorBase {

    Block whichBranch;

    protected TreeGeneratorBase(Block which) {
        whichBranch = which;
    }

    protected List<EnumFacing> findValidGrowthDirections(World worldIn, BlockPos pos, Random rand) {

        // Valid choices: East, West, North, South, Up, NEVER DOWN
        List<EnumFacing> newFacings = new ArrayList<EnumFacing>();

        testFacing(worldIn, pos, newFacings, EnumFacing.UP);
        testFacing(worldIn, pos, newFacings, EnumFacing.DOWN);
        testFacing(worldIn, pos, newFacings, EnumFacing.EAST);
        testFacing(worldIn, pos, newFacings, EnumFacing.WEST);
        testFacing(worldIn, pos, newFacings, EnumFacing.NORTH);
        testFacing(worldIn, pos, newFacings, EnumFacing.SOUTH);

        int c = newFacings.size();
        for(int i=c-1; i>0; i--) {
            int j = rand.nextInt(i+1);
            if(j != i) {
                EnumFacing t = newFacings.get(i);
                newFacings.set(i, newFacings.get(j));
                newFacings.set(j, t);
            }
        }

        return newFacings;
    }

    protected void testFacing(World worldIn, BlockPos pos, List<EnumFacing> newFacings, EnumFacing testFacing) {
        if (worldIn.canBlockBePlaced(whichBranch, pos.offset(testFacing), false, testFacing, null, null))
            newFacings.add(testFacing);
    }

    protected boolean placeBranch(World worldIn, BlockPos pos, int thickness, EnumFacing facing, boolean leaves) {
        if (!worldIn.canBlockBePlaced(whichBranch, pos, false, facing, null, null))
            return false;

        int meta = BlockBranch.getMetaFromProperties(thickness, leaves);
        worldIn.setBlockState(pos, whichBranch.onBlockPlaced(worldIn, pos, facing, pos.getX(), pos.getY(), pos.getZ(), meta, null));
        return true;
    }

    protected void addBranchToPending(Queue<BranchInfo> pending, BlockPos pos, EnumFacing facing, int length, int thickness) {
        boolean found = false;
        for (BranchInfo p : pending) {
            if (p.pos.equals(pos) && p.thickness < thickness) {
                p.thickness = thickness;
                p.facing = facing;
                p.length = Math.max(p.length, length);
                found = true;
            }
        }
        if (!found)
            pending.add(new BranchInfo(pos, facing, thickness, length + 1));
    }

    public abstract void generateTreeAt(World worldIn, BlockPos pos, Random rand);

    protected double computeDistanceFromCenter(BlockPos centerPos, BlockPos pos) {
        double X0 = centerPos.getX();
        double Y0 = centerPos.getY();
        double Z0 = centerPos.getZ();
        double X1 = pos.getX();
        double Y1 = pos.getY();
        double Z1 = pos.getZ();

        double dx = (X1-X0) * 1.0;
        double dy = (Y1-Y0) * 1.0;
        double dz = (Z1-Z0) * 1.0;

        double dd = (dx*dx+dy*dy+dz*dz);

        return Math.sqrt(dd);
    }

    protected class BranchInfo {
        public BlockPos pos;
        public EnumFacing facing;
        public int thickness;
        public int length;

        public BranchInfo(BlockPos pos, EnumFacing facing, int thickness, int length) {
            this.pos = pos;
            this.facing = facing;
            this.thickness = thickness;
            this.length = length;
        }
    }

}
