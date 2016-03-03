package gigaherz.nattrees;

import gigaherz.nattrees.generators.TreeGeneratorBase;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Random;

public class ItemNewSapling extends Item
{

    final Block baseBlock;
    final TreeGeneratorBase treeGen;

    public ItemNewSapling(Block baseBlock, TreeGeneratorBase treeGen, String unlocName)
    {
        setCreativeTab(CreativeTabs.tabDecorations);
        this.baseBlock = baseBlock;
        this.treeGen = treeGen;
        this.setUnlocalizedName(unlocName);
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        pos = pos.offset(side);

        if (stack.stackSize == 0)
        {
            return false;
        }
        else if (!playerIn.canPlayerEdit(pos, side, stack))
        {
            return false;
        }
        else if (!NaturalTrees.branchOak.canPlaceBlockAt(worldIn, pos))
        {
            return false;
        }
        else if (worldIn.canBlockBePlaced(baseBlock, pos, false, side, null, stack))
        {
            if (worldIn.isRemote)
                return true;

            treeGen.generateTreeAt(worldIn, pos, new Random());
            return true;
        }
        else
        {
            return false;
        }
    }
}
