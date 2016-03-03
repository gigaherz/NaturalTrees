package gigaherz.nattrees.client;

import gigaherz.nattrees.CommonProxy;
import gigaherz.nattrees.NaturalTrees;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy
{
    public void preInit()
    {
        registerBlockModelAsItem(NaturalTrees.branchOak, "branch_oak");
        registerBlockModelAsItem(NaturalTrees.branchBirch, "branch_birch");
        registerBlockModelAsItem(NaturalTrees.branchSpruce, "branch_spruce");
        registerBlockModelAsItem(NaturalTrees.branchJungle, "branch_jungle");
        registerBlockModelAsItem(NaturalTrees.branchDarkOak, "branch_dark_oak");
        registerBlockModelAsItem(NaturalTrees.branchAcacia, "branch_acacia");
    }

    @Override
    public void init()
    {
    }

    public void registerBlockModelAsItem(final Block block, final String blockName)
    {
        registerBlockModelAsItem(block, 0, blockName);
    }

    public void registerBlockModelAsItem(final Block block, int meta, final String blockName)
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta,
                new ModelResourceLocation(NaturalTrees.MODID + ":" + blockName, "inventory"));
    }

}
