package gigaherz.nattrees.generators;

import gigaherz.nattrees.NaturalTrees;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.*;

public class OakTreeGenerator extends TreeGeneratorBase {

    public OakTreeGenerator() {
        super(NaturalTrees.branchOak);
    }

    @Override
    public void generateTreeAt(World worldIn, BlockPos pos, Random rand) {
        growBranch(worldIn, pos, rand, 7, EnumFacing.UP);
    }

    private void growBranch(World worldIn, BlockPos startPos, Random rand, int thickness, EnumFacing facing) {
        Queue<BranchInfo> pending = new ArrayDeque<BranchInfo>();

        BlockPos pos = startPos;
        int length = 1;

        int tallness = rand.nextInt(3) + 5;
        double spreadness = tallness * 0.25 * (rand.nextDouble() * 2 + 2);

        BlockPos centerPos = startPos.up(tallness + 1);

        pending.add(new BranchInfo(pos, facing, thickness, length));
        while (pending.size() > 0) {
            BranchInfo info = pending.remove();
            pos = info.pos;
            facing = info.facing;
            thickness = info.thickness;
            length = info.length;

            boolean leaves = length >= 4 && thickness <= 1;

            if (!placeBranch(worldIn, pos, thickness, facing, leaves))
                continue;

            List<EnumFacing> newFacings = findValidGrowthDirections(worldIn, pos, rand);
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

                double distance = computeDistanceFromCenter(pos, centerPos);
                max = (int)Math.min(max, Math.max(tallness-length, spreadness - distance) + 1);

                int thick = max;
                if (min < max)
                    thick = rand.nextInt(max - min) + min;

                if (thick > thickness)
                    thick = thickness;

                if (thick >= 0) {
                    BlockPos newPos = pos.offset(newFacing);

                    addBranchToPending(pending, newPos, newFacing, length, thick);
                }
            }
        }
    }

    @Override
    protected double computeDistanceFromCenter(BlockPos centerPos, BlockPos pos) {
        double X0 = centerPos.getX();
        double Y0 = centerPos.getY();
        double Z0 = centerPos.getZ();
        double X1 = pos.getX();
        double Y1 = pos.getY();
        double Z1 = pos.getZ();

        double dx = (X1-X0) * 1.0;
        double dy = (Y1-Y0) * 2.0;
        double dz = (Z1-Z0) * 1.0;

        double dd = (dx*dx+dy*dy+dz*dz);

        return Math.sqrt(dd);
    }

}
