package gigaherz.nattrees.generators;

import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Random;

public interface ITreeGenerator
{
    default InteractionResult generateTreeAt(Level worldIn, BlockPos startPos, Random rand)
    {
        return generateTreeAt(worldIn, startPos, rand, 2);
    }

    InteractionResult generateTreeAt(Level worldIn, BlockPos startPos, Random rand, int placeFlags);
}
