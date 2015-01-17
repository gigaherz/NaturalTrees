package gigaherz.nattrees.client;

import gigaherz.nattrees.CommonProxy;
import gigaherz.nattrees.NaturalTrees;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class ClientProxy extends CommonProxy {

    public void registerPreRenderers() {
    }

    @Override
    public void registerRenderers() {
        registerBlockTexture(NaturalTrees.branchOak, "branchOak");
    }

    public void registerBlockTexture(final Block block, final String blockName) {
        registerBlockTexture(block, 0, blockName);
    }

    public void registerBlockTexture(final Block block, int meta, final String blockName) {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        renderItem.getItemModelMesher().register(Item.getItemFromBlock(block), meta, new ModelResourceLocation(NaturalTrees.MODID + ":" + blockName, "inventory"));
    }

    public void registerItemTexture(final Item item, final String itemName) {
        registerItemTexture(item, 0, itemName);
    }

    public void registerItemTexture(final Item item, int meta, final String itemName) {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        renderItem.getItemModelMesher().register(item, meta, new ModelResourceLocation(NaturalTrees.MODID + ":" + itemName, "inventory"));
        ModelBakery.addVariantName(item, NaturalTrees.MODID + ":" + itemName);
    }
}
