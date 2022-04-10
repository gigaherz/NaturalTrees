package gigaherz.nattrees.generators;

import gigaherz.nattrees.branch.BlockBranch;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BirchTreeGenerator extends AbstractTreeGenerator<BirchBranchInfo>
{
    public BirchTreeGenerator(BlockBranch branch)
    {
        super(branch);
    }

    @Override
    public ActionResultType generateTreeAt(World worldIn, BlockPos startPos, Random rand, int placeFlags)
    {
        if (!canSpawnTreeAt(worldIn, startPos))
            return ActionResultType.FAIL;

        int tallness = rand.nextInt(4) + 5;
        int startThickness = tallness * 3 / 4;
        double spreadness = tallness * 0.25 * (rand.nextDouble() * 2 + 2);

        BlockPos centerPos = startPos.up(tallness + 1);

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
