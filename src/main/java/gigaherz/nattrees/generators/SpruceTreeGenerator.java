package gigaherz.nattrees.generators;

import gigaherz.nattrees.branch.BlockBranch;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.Random;

public class SpruceTreeGenerator extends AbstractTreeGenerator<SpruceBranchInfo>
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
}
