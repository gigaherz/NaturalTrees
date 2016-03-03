package gigaherz.nattrees;

import gigaherz.nattrees.generators.BirchTreeGenerator;
import gigaherz.nattrees.generators.OakTreeGenerator;
import gigaherz.nattrees.generators.TreeGeneratorBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
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
public class NaturalTrees
{
    public static final String MODID = "NaturalTrees";
    public static final String MODNAME = "Natural Trees";
    public static final String VERSION = "1.0";

    public static BlockBranch branchOak;
    public static BlockBranch branchBirch;
    public static BlockBranch branchSpruce;
    public static BlockBranch branchJungle;
    public static BlockBranch branchDarkOak;
    public static BlockBranch branchAcacia;

    public static TreeGeneratorBase generatorOak;
    public static TreeGeneratorBase generatorBirch;
    public static TreeGeneratorBase generatorSpruce;
    public static TreeGeneratorBase generatorJungle;
    public static TreeGeneratorBase generatorDarkOak;
    public static TreeGeneratorBase generatorAcacia;

    @Mod.Instance(value = NaturalTrees.MODID)
    public static NaturalTrees instance;

    @SidedProxy(clientSide = "gigaherz.nattrees.client.ClientProxy", serverSide = "gigaherz.nattrees.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {

        MinecraftForge.TERRAIN_GEN_BUS.register(this);

        branchOak = new BlockBranch(Material.wood, BlockBranch.Variant.OAK, "branch_oak");
        GameRegistry.registerBlock(branchOak, "branch_oak");

        branchBirch = new BlockBranch(Material.wood, BlockBranch.Variant.BIRCH, "branch_birch");
        GameRegistry.registerBlock(branchBirch, "branch_birch");

        branchSpruce = new BlockBranch(Material.wood, BlockBranch.Variant.SPRUCE, "branch_spruce");
        GameRegistry.registerBlock(branchSpruce, "branch_spruce");

        branchJungle = new BlockBranch(Material.wood, BlockBranch.Variant.JUNGLE, "branch_jungle");
        GameRegistry.registerBlock(branchJungle, "branch_jungle");

        branchDarkOak = new BlockBranch(Material.wood, BlockBranch.Variant.DARK_OAK, "branch_dark_oak");
        GameRegistry.registerBlock(branchDarkOak, "branch_dark_oak");

        branchAcacia = new BlockBranch(Material.wood, BlockBranch.Variant.ACACIA, "branch_acacia");
        GameRegistry.registerBlock(branchAcacia, "branch_acacia");

        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();

        generatorOak = new OakTreeGenerator();
        generatorBirch = new BirchTreeGenerator();
        generatorSpruce = new OakTreeGenerator();
        generatorJungle = new OakTreeGenerator();
        generatorDarkOak = new OakTreeGenerator();
        generatorAcacia = new OakTreeGenerator();
    }

    @SubscribeEvent
    public void onDecorateBiome(DecorateBiomeEvent.Decorate ev)
    {
        if (ev.type == DecorateBiomeEvent.Decorate.EventType.TREE)
        {

            BiomeGenBase gen = ev.world.getBiomeGenForCoords(ev.pos);
            BiomeDecorator decorator = gen.theBiomeDecorator;
            int i = decorator.treesPerChunk;

            if (ev.rand.nextInt(10) == 0)
            {
                ++i;
            }


            TreeGeneratorBase treeGen = null;
            if (gen instanceof BiomeGenForest)
            {
                if (gen.biomeName.equals("Roofed Forest"))
                    treeGen = generatorDarkOak;
                else
                    treeGen = generatorOak.combineWith(generatorBirch, 0.1f);
            }
            else if (gen instanceof BiomeGenJungle)
            {
                treeGen = generatorJungle;
            }
            else if (gen instanceof BiomeGenPlains)
            {
                treeGen = generatorOak;
            }
            else if (gen instanceof BiomeGenSavanna)
            {
                treeGen = generatorAcacia;
            }
            else if (gen instanceof BiomeGenSnow)
            {
                treeGen = generatorOak;
            }
            else if (gen instanceof BiomeGenSwamp)
            {
                treeGen = generatorOak;
            }
            else if (gen instanceof BiomeGenTaiga)
            {
                treeGen = generatorSpruce;
            }

            if (treeGen != null)
            {
                ev.setResult(Event.Result.DENY);


                for (int j = 0; j < i; ++j)
                {
                    int k = ev.rand.nextInt(16) + 8;
                    int l = ev.rand.nextInt(16) + 8;

                    BlockPos blockpos = ev.world.getHeight(ev.pos.add(k, 0, l));

                    treeGen.generateTreeAt(ev.world, blockpos, ev.rand);
                }
            }
        }
    }

    @SubscribeEvent
    public void onSaplingGrow(SaplingGrowTreeEvent ev)
    {
        IBlockState state = ev.world.getBlockState(ev.pos);
        Block block = state.getBlock();
        if (block == Blocks.sapling)
        {
            BlockPlanks.EnumType type = state.getValue(BlockSapling.TYPE);

            switch (type)
            {
                case OAK:
                    ev.setResult(Event.Result.DENY);
                    new BirchTreeGenerator().generateTreeAt(ev.world, ev.pos, ev.rand);
                    break;
            }
        }
    }
}
