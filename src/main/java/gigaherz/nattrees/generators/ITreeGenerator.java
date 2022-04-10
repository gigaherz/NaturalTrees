package gigaherz.nattrees.generators;

import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public interface ITreeGenerator
{
    default ActionResultType generateTreeAt(World worldIn, BlockPos startPos, Random rand)
    {
        return generateTreeAt(worldIn, startPos, rand, 2);
    }

    ActionResultType generateTreeAt(World worldIn, BlockPos startPos, Random rand, int placeFlags);
}
