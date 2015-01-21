package gigaherz.nattrees.generators;

import gigaherz.nattrees.NaturalTrees;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Random;

public class OakTreeGenerator extends TreeGeneratorBase {

    public OakTreeGenerator() {
        super(NaturalTrees.branchOak);
    }

    @Override
    public boolean generateTreeAt(World worldIn, BlockPos startPos, Random rand) {
        if (!canSpawnTreeAt(worldIn, startPos))
            return false;

        int tallness = rand.nextInt(3) + 5;
        double spreadness = tallness * 0.25 * (rand.nextDouble() * 2 + 2);

        BlockPos centerPos = startPos.up(tallness + 1);

        BranchInfo initialState = new BranchInfo(startPos, EnumFacing.UP, tallness, 1);

        return processQueue(worldIn, rand, initialState, tallness, spreadness, centerPos);
    }

    @Override
    protected boolean shouldSkipFacing(int length, int tallness, EnumFacing facing, EnumFacing newFacing) {
        return length < tallness / 2 && newFacing != facing;
    }

    @Override
    protected boolean getWillHaveLeaves(BranchInfo info) {
        return info.length >= 4 && info.thickness <= 1;
    }

    @Override
    protected int getRandomThicknessForFacing(BlockPos pos, EnumFacing facing, Random rand, EnumFacing newFacing, int thickness, int length, int tallness, double spreadness, BlockPos centerPos) {
        int min = -1;
        int max = thickness + 2;

        if (newFacing == EnumFacing.DOWN) {
            min = -20;
            max = 1;
        } else if (length < tallness) {
            if (newFacing == facing)
                min = thickness;
            else
                min = -max;
        }

        double distance = computeDistanceFromCenter(pos, centerPos);
        max = (int) Math.min(max, Math.max(tallness - length, spreadness - distance) + 1);

        int thick = max;
        if (min < max)
            thick = rand.nextInt(max - min) + min;
        return thick;
    }

    @Override
    protected double computeDistanceFromCenter(BlockPos centerPos, BlockPos pos) {
        double X0 = centerPos.getX();
        double Y0 = centerPos.getY();
        double Z0 = centerPos.getZ();
        double X1 = pos.getX();
        double Y1 = pos.getY();
        double Z1 = pos.getZ();

        double dx = (X1 - X0) * 1.0;
        double dy = (Y1 - Y0) * 2.0;
        double dz = (Z1 - Z0) * 1.0;

        double dd = (dx * dx + dy * dy + dz * dz);

        return Math.sqrt(dd);
    }

}
