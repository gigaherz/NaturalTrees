package gigaherz.nattrees.branch;

import gigaherz.nattrees.generators.ITreeGenerator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
    public InteractionResult useOn(UseOnContext context)
    {
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        pos = pos.relative(side);

        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        Level world = context.getLevel();
        if (stack.getCount() == 0)
        {
            return InteractionResult.FAIL;
        }
        else if (!player.mayUseItemAt(pos, side, stack))
        {
            return InteractionResult.FAIL;
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
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public BlockState getPlant(BlockGetter world, BlockPos pos)
    {
        return baseBlock.defaultBlockState();
    }
}
