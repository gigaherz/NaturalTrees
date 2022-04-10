package gigaherz.nattrees.generators;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.stream.Stream;

abstract class BranchInfo<T extends BranchInfo<T>>
{
    public final AbstractTreeGenerator<T> owner;
    public final AbstractTreeGenerator.GenerationInfo gen;
    public final BlockPos pos;
    public Direction facing;
    public int thickness;
    public boolean leaves;

    public int length;

    protected BranchInfo(AbstractTreeGenerator<T> owner, AbstractTreeGenerator.GenerationInfo gen, BlockPos pos, Direction facing, int thickness, int length)
    {
        this.owner = owner;
        this.gen = gen;
        this.pos = pos;
        this.facing = facing;
        this.thickness = thickness;
        this.length = length;
    }

    public double horizontalDistanceFromCenter()
    {
        double dx = pos.getX()-gen.root.getX();
        double dz = pos.getZ()-gen.root.getZ();
        return Math.sqrt(dx*dx+dz*dz);
    }

    public double computeDistanceFromCenter()
    {
        double dx = (double) pos.getX() - (double) gen.root.getX();
        double dy = (double) pos.getY() - (double) gen.root.getY();
        double dz = (double) pos.getZ() - (double) gen.root.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public void grow()
    {
        findValidGrowthDirections().forEach(offshoot ->
        {
            Direction newFacing = offshoot.getLeft();
            int newThickness = offshoot.getRight();
            if (newThickness >= 0)
            {
                BlockPos newPos = this.pos.relative(newFacing);
                T newInfo = makeBranch(newPos, newFacing, newThickness, length + 1);
                owner.enqueueBranch(newInfo);
            }
        });
    }

    protected abstract int getRandomThicknessForFacing(T info, Direction newFacing);

    protected abstract T makeBranch(BlockPos newPos, Direction newFacing, int newThickness, int newLength);

    protected abstract T getSelf();

    protected Stream<Pair<Direction,Integer>> findValidGrowthDirections()
    {
        return Arrays.stream(Direction.values())
                .filter(d -> owner.testFacing(getSelf(), d))
                .map(d -> {
                    int newThickness = getRandomThicknessForFacing(getSelf(), d);
                    return Pair.of(d,newThickness);
                })
                .filter(p -> p.getRight() >= 0);
    }
}
