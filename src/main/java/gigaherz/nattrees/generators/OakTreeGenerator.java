package gigaherz.nattrees.generators;

import gigaherz.nattrees.BlockBranch;
import gigaherz.nattrees.NaturalTrees;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.*;

public class OakTreeGenerator extends TreeGeneratorBase {

    private class BranchInfo {
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

    @Override
    public void generateTreeAt(World worldIn, BlockPos pos, Random rand) {
        growBranch(worldIn, pos, rand, 7, EnumFacing.UP);
    }

    private void growBranch(World worldIn, BlockPos startPos, Random rand, int thickness, EnumFacing facing) {
        Queue<BranchInfo> pending = new ArrayDeque<BranchInfo>();

        BlockPos pos = startPos;
        int length = 1;

        int tallness = rand.nextInt(3) + 4;
        double spreadness = tallness * 0.25 * (rand.nextDouble() * 2 + 2);

        BlockPos centerPos = startPos.up(tallness + 1);

        pending.add(new BranchInfo(pos, facing, thickness, length));
        while (pending.size() > 0) {
            BranchInfo info = pending.remove();
            pos = info.pos;
            facing = info.facing;
            thickness = info.thickness;
            length = info.length;

            boolean leaves = thickness <= 1;

            if (!placeBranch(worldIn, pos, thickness, facing, leaves))
                continue;

            List<EnumFacing> newFacings = findGrowthDirections(worldIn, pos, rand);
            for (EnumFacing newFacing : newFacings) {

                int min = -1;
                int max = thickness + 2;

                if (newFacing == EnumFacing.DOWN) {
                    min = -20;
                    max = 1;
                }
                else if (length < tallness) {
                    if (newFacing == facing)
                        min = thickness;
                    else if (length > tallness/2)
                        min = -max;
                    else
                        continue;
                }

                double distance = Math.sqrt(centerPos.distanceSq(pos.getX(),pos.getY(),pos.getZ()));
                max = (int)Math.min(max, Math.max(tallness-length, spreadness - distance) + 1);

                int thick = max;
                if (min < max)
                    thick = rand.nextInt(max - min) + min;

                if (thick > thickness)
                    thick = thickness;

                if (thick >= 0) {
                    BlockPos newPos = pos.offset(newFacing);

                    boolean found = false;
                    for (BranchInfo p : pending) {
                        if (p.pos.equals(newPos) && p.thickness < thickness) {
                            System.out.println("Reusing " + p.pos);
                            p.thickness = thickness;
                            p.facing = facing;
                            p.length = Math.max(p.length, length);
                            found = true;
                        }
                    }
                    if (!found) {
                        System.out.println("Adding new " + newPos);
                        pending.add(new BranchInfo(newPos, newFacing, thick, length + 1));
                    }
                }
            }
        }
    }

    private List<EnumFacing> findGrowthDirections(World worldIn, BlockPos pos, Random rand) {

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

    private void testFacing(World worldIn, BlockPos pos, List<EnumFacing> newFacings, EnumFacing testFacing) {
        if (worldIn.canBlockBePlaced(NaturalTrees.branchOak, pos.offset(testFacing), false, testFacing, null, null))
            newFacings.add(testFacing);
    }

    private boolean placeBranch(World worldIn, BlockPos pos, int thickness, EnumFacing facing, boolean leaves) {
        if (!worldIn.canBlockBePlaced(NaturalTrees.branchOak, pos, false, facing, null, null))
            return false;

        int meta = BlockBranch.getMetaFromProperties(thickness, leaves);
        worldIn.setBlockState(pos, NaturalTrees.branchOak.onBlockPlaced(worldIn, pos, facing, pos.getX(), pos.getY(), pos.getZ(), meta, null));
        return true;
    }
}
