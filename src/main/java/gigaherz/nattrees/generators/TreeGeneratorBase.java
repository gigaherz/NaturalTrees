package gigaherz.nattrees.generators;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public abstract class TreeGeneratorBase {

    public abstract void generateTreeAt(World worldIn, BlockPos pos, Random rand);

}
