package gigaherz.nattrees.generators;

import gigaherz.nattrees.branch.BlockBranch;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Random;

public class OakTreeGenerator extends AbstractTreeGenerator<OakTreeGenerator.OakBranchInfo>
{
    private final float tallnessModifier;
    private final float spreadnessModifier;

    public OakTreeGenerator(BlockBranch branch, float tallnessModifier, float spreadnessModifier)
    {
        super(branch);
        this.tallnessModifier = tallnessModifier;
        this.spreadnessModifier = spreadnessModifier;
    }

    @Override
    public InteractionResult generateTreeAt(Level worldIn, BlockPos startPos, Random rand, int placeFlags)
    {
        if (!canSpawnTreeAt(worldIn, startPos))
            return InteractionResult.FAIL;

        int tallness = (int)((rand.nextInt(3) + 5) * tallnessModifier);
        int startThickness = tallness;
        double spreadness = tallness * 0.25 * (rand.nextDouble() * 2 + 2) * spreadnessModifier;

        BlockPos centerPos = startPos.above(tallness + 1);

        GenerationInfo gen = new GenerationInfo(worldIn, centerPos, tallness, spreadness, placeFlags, rand, startThickness);

        OakBranchInfo initialState = new OakBranchInfo(this, gen, startPos, Direction.UP, startThickness, 1);

        return processQueue(initialState);
    }

    @Override
    protected boolean getWillHaveLeaves(OakBranchInfo info)
    {
        return info.length >= 4 && info.thickness <= 1;
    }

    public static class OakBranchInfo extends BranchInfo<OakBranchInfo>
    {
        protected OakBranchInfo(AbstractTreeGenerator<OakBranchInfo> owner, GenerationInfo gen, BlockPos pos, Direction facing, int thickness, int length)
        {
            super(owner, gen, pos, facing, thickness, length);
        }

        @Nullable
        @Override
        protected OakBranchInfo getRandomBranchForFacing(Direction newFacing)
        {
            if (this.length < this.gen.tallness() / 2 && newFacing != this.facing)
                return null;

            int min = -1;
            int max = this.thickness + 2;

            if (newFacing == Direction.DOWN)
            {
                min = -20;
                max = 1;
            }
            else if (this.length < this.gen.tallness())
            {
                if (newFacing == this.facing)
                    min = this.thickness;
                else
                    min = -max;
            }

            double distance = computeDistanceFromCenter();
            max = (int) Math.min(max, Math.max(this.gen.tallness() - this.length, this.gen.spreadness() - distance) + 1);

            int thick = max;
            if (min < max)
                thick = this.gen.rand().nextInt(max - min) + min;
            return makeBranch(newFacing, thick);
        }

        protected OakBranchInfo makeBranch(Direction newFacing, int newThickness)
        {
            return new OakBranchInfo(owner, gen, pos.relative(newFacing), newFacing, newThickness, length+1);
        }
    }
}
