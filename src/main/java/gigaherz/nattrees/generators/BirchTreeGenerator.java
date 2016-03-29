package gigaherz.nattrees.generators;

import gigaherz.nattrees.NaturalTrees;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Random;

public class BirchTreeGenerator extends TreeGeneratorBase
{

    public BirchTreeGenerator()
    {
        super(NaturalTrees.branchBirch);
    }

    @Override
    public boolean generateTreeAt(World worldIn, BlockPos startPos, Random rand)
    {
        if (!canSpawnTreeAt(worldIn, startPos))
            return false;

        int tallness = rand.nextInt(4) + 5;
        int startThickness = tallness * 3 / 4;
        double spreadness = tallness * 0.25 * (rand.nextDouble() * 2 + 2);

        BlockPos centerPos = startPos.up(tallness + 1);

        BranchInfo initialState = new BranchInfo(startPos, EnumFacing.UP, startThickness, 1);

        return processQueue(worldIn, rand, initialState, tallness, spreadness, centerPos);
    }

    @Override
    protected boolean shouldSkipFacing(int length, int tallness, EnumFacing facing, EnumFacing newFacing)
    {
        return length < tallness * 4 / 5 && newFacing != facing;
    }

    @Override
    protected boolean getWillHaveLeaves(BranchInfo info)
    {
        return info.thickness <= 1;
    }

    @Override
    protected int getRandomThicknessForFacing(BlockPos pos, EnumFacing facing, Random rand, EnumFacing newFacing, int thickness, int length, int tallness, double spreadness, BlockPos centerPos)
    {
        int min = -5;
        int max = thickness + 5;

        if (length < tallness)
        {
            if (newFacing == facing)
                min = thickness;
            else
                min = -max;
        }
        else if (newFacing == EnumFacing.DOWN)
        {
            min = -20;
            max = 1;
        }

        double distance = computeDistanceFromCenter(pos, centerPos);
        max = (int) Math.min(max, Math.max(tallness - length, spreadness - distance) + 1);

        int thick = max;
        if (min < max)
            thick = rand.nextInt(max - min) + min;
        return thick;
    }

    protected double computeDistanceFromCenter(BlockPos centerPos, BlockPos pos)
    {
        double X0 = centerPos.getX();
        double Y0 = centerPos.getY();
        double Z0 = centerPos.getZ();
        double X1 = pos.getX();
        double Y1 = pos.getY();
        double Z1 = pos.getZ();

        double dx = (X1 - X0) * 1.0;
        double dy = (Y1 - Y0) * 0.5;
        double dz = (Z1 - Z0) * 1.0;

        double dd = (dx * dx + dy * dy + dz * dz);

        return Math.sqrt(dd);
    }
}
