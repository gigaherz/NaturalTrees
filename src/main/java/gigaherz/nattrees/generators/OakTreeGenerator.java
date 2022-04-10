package gigaherz.nattrees.generators;

import gigaherz.nattrees.branch.BlockBranch;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class OakTreeGenerator extends AbstractTreeGenerator<OakBranchInfo>
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
    public ActionResultType generateTreeAt(World worldIn, BlockPos startPos, Random rand, int placeFlags)
    {
        if (!canSpawnTreeAt(worldIn, startPos))
            return ActionResultType.FAIL;

        int tallness = (int)((rand.nextInt(3) + 5) * tallnessModifier);
        int startThickness = tallness;
        double spreadness = tallness * 0.25 * (rand.nextDouble() * 2 + 2) * spreadnessModifier;

        BlockPos centerPos = startPos.up(tallness + 1);

        GenerationInfo gen = new GenerationInfo(worldIn, centerPos, tallness, spreadness, placeFlags, rand);

        OakBranchInfo initialState = new OakBranchInfo(this, gen, startPos, Direction.UP, startThickness, 1);

        return processQueue(initialState);
    }

    @Override
    protected boolean getWillHaveLeaves(OakBranchInfo info)
    {
        return info.length >= 4 && info.thickness <= 1;
    }
}
