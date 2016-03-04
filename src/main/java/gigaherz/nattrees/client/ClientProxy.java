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
        registerBlockModelAsItem(NaturalTrees.branchOak, 0, "branch_oak", "facing=north,has_leaves=false,thickness=4");
        registerBlockModelAsItem(NaturalTrees.branchBirch, 0, "branch_birch", "facing=north,has_leaves=false,thickness=4");
        registerBlockModelAsItem(NaturalTrees.branchSpruce, 0, "branch_spruce", "facing=north,has_leaves=false,thickness=4");
        registerBlockModelAsItem(NaturalTrees.branchJungle, 0, "branch_jungle", "facing=north,has_leaves=false,thickness=4");
        registerBlockModelAsItem(NaturalTrees.branchDarkOak, 0, "branch_dark_oak", "facing=north,has_leaves=false,thickness=4");
        registerBlockModelAsItem(NaturalTrees.branchAcacia, 0, "branch_acacia", "facing=north,has_leaves=false,thickness=4");
    }

    @Override
    public void init()
    {
    }

    public void registerBlockModelAsItem(final Block block, int meta, final String blockName, final String variant)
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta,
                new ModelResourceLocation(NaturalTrees.MODID + ":" + blockName, variant));
    }

}
