package gigaherz.nattrees.generators;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class SpruceBranchInfo extends BranchInfo<SpruceBranchInfo>
{
    SpruceBranchInfo parent;

    public SpruceBranchInfo(AbstractTreeGenerator<SpruceBranchInfo> owner, AbstractTreeGenerator.GenerationInfo gen, BlockPos newPos, Direction newFacing, int newThickness, int newLength)
    {
        super(owner, gen, newPos, newFacing, newThickness, newLength);
        this.parent = null;
    }
    public SpruceBranchInfo(AbstractTreeGenerator<SpruceBranchInfo> owner, SpruceBranchInfo parent, BlockPos newPos, Direction newFacing, int newThickness, int newLength)
    {
        super(owner, parent.gen, newPos, newFacing, newThickness, newLength);
        this.parent = parent;
    }

    @Override
    protected int getRandomThicknessForFacing(SpruceBranchInfo info, Direction newFacing)
    {
        if (newFacing == Direction.DOWN)
        {
            // never down.
            return -1;
        }

        if (newFacing == Direction.UP)
        {
            if (info.facing != Direction.UP)
            {
                // disallow growing branches up, only the main trunk can.
                return -1;
            }

            // chance to continue growing up with the same thickness
            float shrinkFactor = (info.gen.rand.nextFloat() * 0.5f + 0.75f) * 8.0f / info.gen.tallness;
            int shrink = Mth.floor(shrinkFactor);
            int newThickness = info.thickness - shrink;
            shrinkFactor -= shrink;
            if (info.gen.rand.nextFloat() < shrinkFactor)
            {
                newThickness--;
            }
            return newThickness;
        }

        if (info.length < 2)
        {
            // no branches at the bottom
            return -1;
        }

        if (info.thickness < 1)
        {
            // no side branches at the top
            return -1;
        }

        double height = info.pos.getY() - info.gen.root.getY();
        double distanceFromTrunk = info.horizontalDistanceFromCenter();
        double circleRadius = info.gen.spreadness * (1 - height / info.gen.tallness);

        //double approxTrunkThickness = 7 * (1 - height / info.gen.tallness);
        SpruceBranchInfo p = this.parent;
        while(p.facing != Direction.UP)
            p = p.parent;
        double trunkThickness = p.thickness;

        int maxThickness = (int) ((trunkThickness-1) * (1 - distanceFromTrunk / circleRadius));

        int minThickness = info.thickness - 1;

        if (maxThickness <= minThickness)
        {
            return maxThickness;
        }

        return info.gen.rand.nextInt(maxThickness - minThickness) + minThickness;
    }

    @Override
    protected SpruceBranchInfo makeBranch(BlockPos newPos, Direction newFacing, int newThickness, int newLength)
    {
        return new SpruceBranchInfo(owner, this, newPos, newFacing, newThickness, newLength);
    }

    @Override
    protected SpruceBranchInfo getSelf()
    {
        return this;
    }
}
