package gigaherz.nattrees.branch;

import gigaherz.nattrees.generators.ITreeGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.Random;

public class ItemNewSapling extends Item implements IPlantable
{
    final BlockBranch baseBlock;
    final ITreeGenerator treeGen;

    public ItemNewSapling(BlockBranch baseBlock, ITreeGenerator treeGen, Item.Properties properties)
    {
        super(properties);
        this.baseBlock = baseBlock;
        this.treeGen = treeGen;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        pos = pos.relative(side);

        ItemStack stack = context.getItemInHand();
        PlayerEntity player = context.getPlayer();
        World world = context.getLevel();
        if (stack.getCount() == 0)
        {
            return ActionResultType.FAIL;
        }
        else if (!player.mayUseItemAt(pos, side, stack))
        {
            return ActionResultType.FAIL;
        }
        /*else if (!world.getBlockState(pos).canPlaceBlockAt(world, pos, side, this))
        {
            return EnumActionResult.FAIL;
        }*/
        /*else if (!worldIn.mayPlace(baseBlock, pos, false, side, null))
        {
            return EnumActionResult.FAIL;
        }*/
        else
        {
            if (!world.isClientSide)
                return treeGen.generateTreeAt(world, pos, new Random(), 3);
            return ActionResultType.SUCCESS;
        }
    }

    @Override
    public BlockState getPlant(IBlockReader world, BlockPos pos)
    {
        return baseBlock.defaultBlockState();
    }
}
