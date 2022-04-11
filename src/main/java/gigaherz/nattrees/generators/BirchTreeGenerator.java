package gigaherz.nattrees.generators;

import com.mojang.datafixers.util.Pair;
import gigaherz.nattrees.branch.BlockBranch;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BirchTreeGenerator extends AbstractTreeGenerator<BirchTreeGenerator.BirchBranchInfo>
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

        BirchBranchInfo initialState = new BirchBranchInfo(this, gen, startPos, Direction.UP, startThickness, 1, 1, startThickness*startThickness);

        return processQueue(initialState);
    }

    @Override
    protected boolean getWillHaveLeaves(BirchBranchInfo info)
    {
        return info.thickness <= 2;
    }

    public static class BirchBranchInfo extends BranchInfo<BirchBranchInfo>
    {
        private final int sameLength;
        private final int energy;

        protected BirchBranchInfo(AbstractTreeGenerator<BirchBranchInfo> owner, GenerationInfo gen, BlockPos pos, Direction facing, int thickness, int length, int sameLength, int energy)
        {
            super(owner, gen, pos, facing, thickness, length);
            this.sameLength = sameLength;
            this.energy = energy;
        }

        @Override
        protected Stream<BirchBranchInfo> findValidGrowthDirections()
        {
            List<Pair<Direction, Integer>> directions = new ArrayList<>();

            var nSides = 0;
            if (this.thickness < this.gen.startThickness() && this.thickness > 1)
            {
                var nMax = Math.min(6, this.thickness-1);
                var sSides = this.gen.rand().nextInt(nMax*nMax);
                nSides = Mth.ceil(Mth.sqrt(sSides));
            }
            var hDistance = Mth.abs(pos.getX()-gen.root().getX())+Mth.abs(pos.getZ()-gen.root().getZ());
            nSides /= (hDistance+1);
            var maxThickness = thickness == 1 ? (1-sameLength/5) : (thickness - sameLength/2);
            var nThickness = Math.max(1, Mth.floor(Math.sqrt(Math.pow(this.thickness,2)-nSides)));
            var dirs = Stream.of(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH)
                    .filter(d -> owner.testFacing(getSelf(), d)).collect(Collectors.toList()) ;
            if (this.facing == Direction.UP || this.gen.rand().nextFloat() < 0.66f)
            {
                if (this.length < this.gen.tallness()*2)
                {
                    var upThickness = Math.min(maxThickness,  (nThickness <= 1 || this.gen.rand().nextFloat() < 0.75f) ? nThickness : nThickness - 1);
                    directions.add(Pair.of(Direction.UP, upThickness));
                    nSides--;
                }
            }
            else if (this.gen.rand().nextFloat() < 0.66f)
            {
                var sideThickness = Math.min(maxThickness,(nThickness <= 1 || this.gen.rand().nextFloat() < 0.75f) ? nThickness : nThickness - 1);
                directions.add(Pair.of(this.facing,sideThickness));
                dirs.remove(this.facing);
                nSides--;
            }
            if (nSides > 0)
            {
                for(int i=0;i<nSides;i++)
                {
                    var iSide = this.gen.rand().nextInt(dirs.size());
                    var side = dirs.remove(iSide);

                    var sideThickness = Math.min(maxThickness,(nThickness <= 1 || this.gen.rand().nextFloat() < 0.75f) ? nThickness : nThickness - 1);
                    directions.add(Pair.of(side,sideThickness));
                }
            }
            return directions.stream()
                    .filter(p -> p.getSecond() > 0)
                    .map(p -> makeBranch(p.getFirst(), p.getSecond()));
        }

        @Nullable
        @Override
        protected BirchBranchInfo getRandomBranchForFacing(Direction newFacing)
        {
            return null;
        }

        protected BirchBranchInfo makeBranch(Direction newFacing, int newThickness)
        {
            return new BirchBranchInfo(owner, gen, pos.relative(newFacing), newFacing, newThickness, length+1, newThickness == thickness ? sameLength + 1 : sameLength, energy);
        }
    }
}
