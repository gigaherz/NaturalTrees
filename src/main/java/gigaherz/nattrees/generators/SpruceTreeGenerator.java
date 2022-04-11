package gigaherz.nattrees.generators;

import gigaherz.nattrees.branch.BlockBranch;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.Random;

public class SpruceTreeGenerator extends AbstractTreeGenerator<SpruceTreeGenerator.SpruceBranchInfo>
{
    public SpruceTreeGenerator(BlockBranch branch)
    {
        super(branch);
    }

    @Override
    public InteractionResult generateTreeAt(Level worldIn, BlockPos startPos, Random rand, int placeFlags)
    {
        if (!canSpawnTreeAt(worldIn, startPos))
            return InteractionResult.FAIL;

        int tallness = rand.nextInt(10) + 8;
        int startThickness = Mth.ceil(Math.min(7,tallness * 0.6f));
        double spreadness = tallness * 0.2 * (rand.nextDouble() * 0.5 + 0.75);

        BlockPos centerPos = startPos.above(1);

        GenerationInfo gen = new GenerationInfo(worldIn, centerPos, tallness, spreadness, placeFlags, rand, startThickness);

        SpruceBranchInfo initialState = new SpruceBranchInfo(this, gen, startPos, Direction.UP, startThickness, 1);

        return processQueue(initialState);
    }

    @Override
    protected boolean getWillHaveLeaves(SpruceBranchInfo info)
    {
        return info.thickness <= 1;
    }

    public static class SpruceBranchInfo extends BranchInfo<SpruceBranchInfo>
    {
        SpruceBranchInfo parent;

        public SpruceBranchInfo(AbstractTreeGenerator<SpruceBranchInfo> owner, GenerationInfo gen, BlockPos newPos, Direction newFacing, int newThickness, int newLength)
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
        protected SpruceBranchInfo getRandomBranchForFacing(Direction newFacing)
        {
            if (newFacing == Direction.DOWN)
            {
                // never down.
                return null;
            }

            if (newFacing == Direction.UP)
            {
                if (this.facing != Direction.UP)
                {
                    // disallow growing branches up, only the main trunk can.
                    return null;
                }

                // chance to continue growing up with the same thickness
                float shrinkFactor = (this.gen.rand().nextFloat() * 0.5f + 0.75f) * 8.0f / this.gen.tallness();
                int shrink = Mth.floor(shrinkFactor);
                int newThickness = this.thickness - shrink;
                shrinkFactor -= shrink;
                if (this.gen.rand().nextFloat() < shrinkFactor)
                {
                    newThickness--;
                }
                return makeBranch(newFacing,newThickness);
            }

            if (this.length < 2)
            {
                // no branches at the bottom
                return null;
            }

            if (this.thickness < 1)
            {
                // no side branches at the top
                return null;
            }

            double height = this.pos.getY() - this.gen.root().getY();
            double distanceFromTrunk = this.horizontalDistanceFromCenter();
            double circleRadius = this.gen.spreadness() * (1 - height / this.gen.tallness());

            //double approxTrunkThickness = 7 * (1 - height / this.gen.tallness);
            SpruceBranchInfo p = this;
            while(p.parent != null && p.facing != Direction.UP)
                p = p.parent;
            double trunkThickness = p.thickness;

            int maxThickness = (int) ((trunkThickness-1) * (1 - distanceFromTrunk / circleRadius));

            int minThickness = this.thickness - 1;

            if (maxThickness <= minThickness)
            {
                return makeBranch(newFacing, maxThickness);
            }

            return makeBranch(newFacing,this.gen.rand().nextInt(maxThickness - minThickness) + minThickness);
        }

        protected SpruceBranchInfo makeBranch(Direction newFacing, int newThickness)
        {
            return new SpruceBranchInfo(owner, gen, pos.relative(newFacing), newFacing, newThickness, length+1);
        }
    }
}
