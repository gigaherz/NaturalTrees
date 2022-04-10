package gigaherz.nattrees.generators;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class BirchBranchInfo extends BranchInfo<BirchBranchInfo>
{
    protected BirchBranchInfo(AbstractTreeGenerator<BirchBranchInfo> owner, AbstractTreeGenerator.GenerationInfo gen, BlockPos pos, Direction facing, int thickness, int length)
    {
        super(owner, gen, pos, facing, thickness, length);
    }

    @Override
    protected int getRandomThicknessForFacing(BirchBranchInfo info, Direction newFacing)
    {
        if (info.length < info.gen.tallness * 4f / 5 && newFacing != info.facing)
            return -1;

        if (newFacing == Direction.DOWN)
            return -1;

        float f = info.gen.rand.nextFloat();
        if (info.facing == Direction.UP && newFacing != Direction.UP)
        {
            if (info.thickness < info.gen.startThickness && info.thickness > 1)
            {
                if (f < 0.66)
                    return info.gen.rand.nextInt(info.thickness) - 1;
                return -1;
            }
        }
        else if (newFacing == Direction.UP)
        {
            if (f < (info.thickness > 0 ? 0.33 : 0.66))
                return info.thickness;
            return info.thickness - 1;
        }
        else
        {
            if (newFacing == info.facing)
            {
                if (f < 0.2)
                    return -1;
                if (f < 0.6)
                    return info.thickness - 1;
                return info.thickness;
            }
            else
            {
                if (f < (info.facing == Direction.UP ? 0.66 : 0.16))
                    return info.thickness;
                return -1;
            }
        }

        return -1;
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
