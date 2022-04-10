package gigaherz.nattrees.generators;

import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Random;

public class RandomTreeGenerator implements ITreeGenerator
{
    private final ITreeGenerator[] gens;
    private final float[] chances;

    private RandomTreeGenerator(ITreeGenerator[] generators, float[] chances)
    {
        this.gens = generators;
        this.chances = chances;
    }

    public static ITreeGenerator of(AbstractTreeGenerator first)
    {
        return first;
    }

    public static ITreeGenerator of(float chanceFirst, ITreeGenerator first, float chanceSecond, ITreeGenerator second)
    {
        return new RandomTreeGenerator(new ITreeGenerator[]{first, second}, new float[]{chanceFirst, chanceSecond});
    }

    public static ITreeGenerator of(float chanceFirst, ITreeGenerator first, float chanceSecond, ITreeGenerator second, float chance3, ITreeGenerator gen3)
    {
        return new RandomTreeGenerator(
                new ITreeGenerator[]{first, second, gen3},
                new float[]{chanceFirst, chanceSecond, chance3});
    }

    public static ITreeGenerator of(float chanceFirst, ITreeGenerator first, float chanceSecond, ITreeGenerator second, float chance3, ITreeGenerator gen3, float chance4, ITreeGenerator gen4)
    {
        return new RandomTreeGenerator(
                new ITreeGenerator[]{first, second, gen3, gen4},
                new float[]{chanceFirst, chanceSecond, chance3, chance4});
    }

    public static ITreeGenerator of(Object... args)
    {
        if (args.length %2 != 0)
            throw new IllegalStateException("Args must be a sequence of float,ITreeGenerator pairs");
        ITreeGenerator[] gens = new ITreeGenerator[args.length/2];
        float[] chances = new float[args.length/2];
        for(int i=0;i<args.length;i+=2)
        {
            gens[i/2] = (ITreeGenerator)args[i];
            chances[i/2] = (Float)args[i+1];
        }
        return new RandomTreeGenerator(gens, chances);
    }

    @Override
    public InteractionResult generateTreeAt(Level worldIn, BlockPos startPos, Random rand, int placeFlags)
    {
        float choice = rand.nextFloat();
        for (int i = 0; i < chances.length; i++)
        {
            choice -= chances[i];
            if (choice <= 0)
            {
                return gens[i].generateTreeAt(worldIn, startPos, rand, placeFlags);
            }
        }
        return InteractionResult.PASS;
    }
}
