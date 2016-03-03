package gigaherz.nattrees.generators;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomTreeGenerator
        extends TreeGeneratorBase
{

    final List<TreeGeneratorBase> gens = new ArrayList<TreeGeneratorBase>();
    final List<Float> chances = new ArrayList<Float>();

    public RandomTreeGenerator(TreeGeneratorBase primary, TreeGeneratorBase other, float chanceAlternative)
    {
        super(primary.whichBranch);

        gens.add(primary);
        chances.add(1.0f);

        gens.add(other);
        chances.add(chanceAlternative);
    }

    @Override
    public TreeGeneratorBase combineWith(TreeGeneratorBase other, float chanceAlternative)
    {
        // TODO: make immutable?

        gens.add(other);
        chances.add(chanceAlternative);

        return this;
    }


    @Override
    public boolean generateTreeAt(World worldIn, BlockPos startPos, Random rand)
    {

        float totalChance = 0;
        for (float f : chances) { totalChance += f; }

        float choice = rand.nextFloat() * totalChance;
        for (int i = 0; i < chances.size(); i++)
        {
            choice -= chances.get(i);
            if (choice <= 0)
            {
                return gens.get(i).generateTreeAt(worldIn, startPos, rand);
            }
        }
        return false;
    }

    @Override
    protected boolean shouldSkipFacing(int length, int tallness, EnumFacing facing, EnumFacing newFacing)
    {
        return false;
    }

    @Override
    protected boolean getWillHaveLeaves(BranchInfo info)
    {
        return false;
    }

    @Override
    protected int getRandomThicknessForFacing(BlockPos pos, EnumFacing facing, Random rand, EnumFacing newFacing, int thickness, int length, int tallness, double spreadness, BlockPos centerPos)
    {
        return 0;
    }
}
