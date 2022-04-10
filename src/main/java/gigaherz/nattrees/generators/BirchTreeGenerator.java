package gigaherz.nattrees.generators;

import gigaherz.nattrees.branch.BlockBranch;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Random;

public class BirchTreeGenerator extends AbstractTreeGenerator<BirchBranchInfo>
{
    public BirchTreeGenerator(BlockBranch branch)
    {
        super(branch);
    }

    @Override
    public InteractionResult generateTreeAt(Level worldIn, BlockPos startPos, Random rand, int placeFlags)
    {
        if (!canSpawnTreeAt(worldIn, startPos))
            return InteractionResult.FAIL;

        int tallness = rand.nextInt(4) + 5;
        int startThickness = tallness * 3 / 4;
        double spreadness = tallness * 0.25 * (rand.nextDouble() * 2 + 2);

        BlockPos centerPos = startPos.above(tallness + 1);

        GenerationInfo gen = new GenerationInfo(worldIn, centerPos, tallness, spreadness, placeFlags, rand, startThickness);

        BirchBranchInfo initialState = new BirchBranchInfo(this, gen, startPos, Direction.UP, startThickness, 1);

        return processQueue(initialState);
    }

    @Override
    protected boolean getWillHaveLeaves(BirchBranchInfo info)
    {
        return info.thickness <= 1;
    }
}
