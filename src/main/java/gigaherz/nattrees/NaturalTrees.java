package gigaherz.nattrees;

import gigaherz.nattrees.generators.BirchTreeGenerator;
import gigaherz.nattrees.generators.OakTreeGenerator;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
    public static Item saplingBirch;

    @Mod.Instance(value = NaturalTrees.MODID)
    public static NaturalTrees instance;

    @SidedProxy(clientSide = "gigaherz.nattrees.client.ClientProxy", serverSide = "gigaherz.nattrees.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        MinecraftForge.TERRAIN_GEN_BUS.register(this);

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

        saplingOak = new ItemNewSapling(branchOak, new OakTreeGenerator()).setUnlocalizedName("branch_birch_sapling");
        GameRegistry.registerItem(saplingOak, "branch_oak_sapling");

        saplingBirch = new ItemNewSapling(branchBirch, new BirchTreeGenerator()).setUnlocalizedName("branch_birch_sapling");
        GameRegistry.registerItem(saplingBirch, "branch_birch_sapling");

        proxy.registerPreRenderers();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerRenderers();

        BlockFence f;
    }

    @SubscribeEvent
    public void onDecorateBiome(DecorateBiomeEvent.Decorate ev) {
        if (ev.type == DecorateBiomeEvent.Decorate.EventType.TREE) {
            ev.setResult(Event.Result.DENY);
            new OakTreeGenerator().generateTreeAt(ev.world, ev.pos, ev.rand);
        }
    }

    @SubscribeEvent
    public void onSaplingGrow(SaplingGrowTreeEvent ev) {
        IBlockState state = ev.world.getBlockState(ev.pos);
        Block block = state.getBlock();
        if(block == Blocks.sapling) {
            BlockPlanks.EnumType type = (BlockPlanks.EnumType) state.getValue(BlockSapling.TYPE);

            switch(type) {
                case OAK :
                    ev.setResult(Event.Result.DENY);
                    new OakTreeGenerator().generateTreeAt(ev.world, ev.pos, ev.rand);
                    break;
            }
        }
    }
}
