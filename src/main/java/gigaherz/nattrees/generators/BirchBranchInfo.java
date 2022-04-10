package gigaherz.nattrees.generators;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class BirchBranchInfo extends BranchInfo<BirchBranchInfo>
{
    protected BirchBranchInfo(AbstractTreeGenerator<BirchBranchInfo> owner, AbstractTreeGenerator.GenerationInfo gen, BlockPos pos, Direction facing, int thickness, int length)
    {
        super(owner, gen, pos, facing, thickness, length);
    }

    @Override
    protected int getRandomThicknessForFacing(BirchBranchInfo info, Direction newFacing)
    {
        if (info.length < info.gen.tallness * 4 / 5 && newFacing != info.facing)
            return -1;

        /*
        int min = -5;
        int max = thickness + 5;

        if (length < tallness)
        {
            if (newFacing == facing)
                min = thickness;
            else
                min = -max;
        }
        else if (newFacing == Direction.DOWN)
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
        */

        float f = info.gen.rand.nextFloat();
        if (newFacing == Direction.UP)
        {
            if (f < (info.thickness > 0 ? 0.33 : 0.66))
                return info.thickness;
            return info.thickness - 1;
        }
        else
        {
            if (newFacing == info.facing)
            {
                if (f < 0.33)
                    return -1;
                if (f < 0.66)
                    return Math.min(Math.min(3, info.thickness * 3 - info.length), info.thickness);
                return Math.min(Math.min(3, info.thickness * 3 - info.length), info.thickness - 1);
            }
            else
            {
                if (f < (info.facing == Direction.UP ? 0.66 : 0.16))
                    return Math.min(Math.min(1, info.thickness * 3 - info.length), info.thickness);
                return -1;
            }
        }
    }

    @Override
    protected BirchBranchInfo makeBranch(BlockPos newPos, Direction newFacing, int newThickness, int newLength)
    {
        return new BirchBranchInfo(owner, gen, newPos, newFacing, newThickness, newLength);
    }

    @Override
    protected BirchBranchInfo getSelf()
    {
        return this;
    }
}
