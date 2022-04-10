package gigaherz.nattrees.generators;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class OakBranchInfo extends BranchInfo<OakBranchInfo>
{
    protected OakBranchInfo(AbstractTreeGenerator<OakBranchInfo> owner, AbstractTreeGenerator.GenerationInfo gen, BlockPos pos, Direction facing, int thickness, int length)
    {
        super(owner, gen, pos, facing, thickness, length);
    }

    @Override
    protected int getRandomThicknessForFacing(OakBranchInfo info, Direction newFacing)
    {
        if (info.length < info.gen.tallness / 2 && newFacing != info.facing)
            return -1;

        int min = -1;
        int max = info.thickness + 2;

        if (newFacing == Direction.DOWN)
        {
            min = -20;
            max = 1;
        }
        else if (info.length < info.gen.tallness)
        {
            if (newFacing == info.facing)
                min = info.thickness;
            else
                min = -max;
        }

        double distance = computeDistanceFromCenter();
        max = (int) Math.min(max, Math.max(info.gen.tallness - info.length, info.gen.spreadness - distance) + 1);

        int thick = max;
        if (min < max)
            thick = info.gen.rand.nextInt(max - min) + min;
        return thick;
    }

    @Override
    protected OakBranchInfo makeBranch(BlockPos newPos, Direction newFacing, int newThickness, int newLength)
    {
        return new OakBranchInfo(owner, gen, newPos, newFacing, newThickness, newLength);
    }

    @Override
    protected OakBranchInfo getSelf()
    {
        return this;
    }
}
