package gigaherz.nattrees;

import gigaherz.nattrees.generators.OakTreeGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;


@Mod(modid = NaturalTrees.MODID, name = NaturalTrees.MODNAME, version = NaturalTrees.VERSION)
public class NaturalTrees {
    public static final String MODID = "NaturalTrees";
    public static final String MODNAME = "Natural Trees";
    public static final String VERSION = "1.0";

    public static Block branchOak;
    public static Block branchBirch;
    public static Block branchSpruce;
    public static Block branchJungle;
    public static Block branchDarkOak;
    public static Block branchAcacia;

    public static Item saplingOak;

    @Mod.Instance(value = NaturalTrees.MODID)
    public static NaturalTrees instance;

    @SidedProxy(clientSide = "gigaherz.nattrees.client.ClientProxy", serverSide = "gigaherz.nattrees.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        branchOak = new BlockBranch(Material.wood, BlockBranch.Variant.OAK).setStepSound(Block.soundTypeWood).setUnlocalizedName("branch_oak");
        GameRegistry.registerBlock(branchOak, "branch_oak");

        branchBirch = new BlockBranch(Material.wood, BlockBranch.Variant.BIRCH).setStepSound(Block.soundTypeWood).setUnlocalizedName("branch_birch");
        GameRegistry.registerBlock(branchBirch, "branch_birch");

        branchSpruce = new BlockBranch(Material.wood, BlockBranch.Variant.SPRUCE).setStepSound(Block.soundTypeWood).setUnlocalizedName("branch_spruce");
        GameRegistry.registerBlock(branchSpruce, "branch_spruce");

        branchJungle = new BlockBranch(Material.wood, BlockBranch.Variant.JUNGLE).setStepSound(Block.soundTypeWood).setUnlocalizedName("branch_jungle");
        GameRegistry.registerBlock(branchJungle, "branch_jungle");

        branchDarkOak = new BlockBranch(Material.wood, BlockBranch.Variant.DARK_OAK).setStepSound(Block.soundTypeWood).setUnlocalizedName("branch_dark_oak");
        GameRegistry.registerBlock(branchDarkOak, "branch_dark_oak");

        branchAcacia = new BlockBranch(Material.wood, BlockBranch.Variant.ACACIA).setStepSound(Block.soundTypeWood).setUnlocalizedName("branch_acacia");
        GameRegistry.registerBlock(branchAcacia, "branch_acacia");

        saplingOak = new ItemNewSapling(branchOak, new OakTreeGenerator()).setUnlocalizedName("branch_oak_sapling");
        GameRegistry.registerItem(saplingOak, "branch_oak_sapling");

        proxy.registerPreRenderers();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerRenderers();

        BlockFence f;
    }
}
