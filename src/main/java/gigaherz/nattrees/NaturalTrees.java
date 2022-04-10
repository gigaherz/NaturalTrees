package gigaherz.nattrees;

import gigaherz.nattrees.branch.BlockBranch;
import gigaherz.nattrees.branch.ItemNewSapling;
import gigaherz.nattrees.generators.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

import java.util.logging.Logger;

@Mod(NaturalTrees.MODID)
public class NaturalTrees
{
    public static final String MODID = "naturaltrees";

    @ObjectHolder(MODID + ":branch_oak")
    public static BlockBranch branchOak;
    @ObjectHolder(MODID + ":branch_birch")
    public static BlockBranch branchBirch;
    @ObjectHolder(MODID + ":branch_spruce")
    public static BlockBranch branchSpruce;
    @ObjectHolder(MODID + ":branch_jungle")
    public static BlockBranch branchJungle;
    @ObjectHolder(MODID + ":branch_dark_oak")
    public static BlockBranch branchDarkOak;
    @ObjectHolder(MODID + ":branch_acacia")
    public static BlockBranch branchAcacia;

    @ObjectHolder(MODID + ":sapling_oak")
    public static ItemNewSapling saplingOak;
    @ObjectHolder(MODID + ":sapling_birch")
    public static ItemNewSapling saplingBirch;
    @ObjectHolder(MODID + ":sapling_spruce")
    public static ItemNewSapling saplingSpruce;
    @ObjectHolder(MODID + ":sapling_jungle")
    public static ItemNewSapling saplingJungle;
    @ObjectHolder(MODID + ":sapling_dark_oak")
    public static ItemNewSapling saplingDarkOak;
    @ObjectHolder(MODID + ":sapling_acacia")
    public static ItemNewSapling saplingAcacia;

    public static ITreeGenerator generatorOak;
    public static ITreeGenerator generatorBirch;
    public static ITreeGenerator generatorSpruce;
    public static ITreeGenerator generatorJungle;
    public static ITreeGenerator generatorDarkOak;
    public static ITreeGenerator generatorAcacia;

    public static NaturalTrees instance;

    public static final Logger LOGGER = Logger.getLogger(MODID);

    public NaturalTrees()
    {
        instance = this;

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addGenericListener(Block.class, this::registerBlocks);
        modEventBus.addGenericListener(Item.class, this::registerItems);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::blockColors);
        modEventBus.addListener(this::itemColors);
    }

    public void registerBlocks(RegistryEvent.Register<Block> ev)
    {
        ev.getRegistry().registerAll(
                new BlockBranch(Block.Properties.create(Material.WOOD).notSolid().hardnessAndResistance(4, 4)).setRegistryName(location("branch_oak")),
                new BlockBranch(Block.Properties.create(Material.WOOD).notSolid().hardnessAndResistance(4, 4)).setRegistryName(location("branch_birch")),
                new BlockBranch(Block.Properties.create(Material.WOOD).notSolid().hardnessAndResistance(4, 4)).setRegistryName(location("branch_spruce")),
                new BlockBranch(Block.Properties.create(Material.WOOD).notSolid().hardnessAndResistance(4, 4)).setRegistryName(location("branch_jungle")),
                new BlockBranch(Block.Properties.create(Material.WOOD).notSolid().hardnessAndResistance(4, 4)).setRegistryName(location("branch_dark_oak")),
                new BlockBranch(Block.Properties.create(Material.WOOD).notSolid().hardnessAndResistance(4, 4)).setRegistryName(location("branch_acacia"))
        );
    }

    public void registerItems(RegistryEvent.Register<Item> ev)
    {
        generatorOak = new OakTreeGenerator(NaturalTrees.branchOak, 1.0f, 1.0f);
        generatorBirch = new BirchTreeGenerator(NaturalTrees.branchBirch);
        generatorSpruce = new SpruceTreeGenerator(NaturalTrees.branchSpruce);
        // TODO: custom generators
        generatorJungle = new OakTreeGenerator(NaturalTrees.branchJungle, 3.0f, 0.5f);
        generatorDarkOak = new OakTreeGenerator(NaturalTrees.branchDarkOak, 2.0f, 1.1f);
        generatorAcacia = new OakTreeGenerator(NaturalTrees.branchAcacia, 1.0f, 2.0f);

        ev.getRegistry().registerAll(
                branchOak.createItemBlock(),
                branchBirch.createItemBlock(),
                branchSpruce.createItemBlock(),
                branchJungle.createItemBlock(),
                branchDarkOak.createItemBlock(),
                branchAcacia.createItemBlock(),

                new ItemNewSapling(branchOak, generatorOak, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(location("sapling_oak")),
                new ItemNewSapling(branchBirch, generatorBirch, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(location("sapling_birch")),
                new ItemNewSapling(branchSpruce, generatorSpruce, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(location("sapling_spruce")),
                new ItemNewSapling(branchJungle, generatorJungle, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(location("sapling_jungle")),
                new ItemNewSapling(branchDarkOak, generatorDarkOak, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(location("sapling_dark_oak")),
                new ItemNewSapling(branchAcacia, generatorAcacia, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(location("sapling_acacia")));
    }

    public void clientSetup(FMLClientSetupEvent event)
    {
        RenderTypeLookup.setRenderLayer(branchOak, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(branchBirch, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(branchSpruce, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(branchJungle, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(branchDarkOak, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(branchAcacia, RenderType.getCutout());
    }

    public void blockColors(ColorHandlerEvent.Block event)
    {
        event.getBlockColors().register((state, world, pos, tintIndex) -> FoliageColors.getSpruce(), NaturalTrees.branchSpruce);
        event.getBlockColors().register((state, world, pos, tintIndex) -> FoliageColors.getBirch(), NaturalTrees.branchBirch);
        event.getBlockColors().register( (state, world, pos, tintIndex) -> (world == null || pos == null)
                        ? FoliageColors.getDefault()
                        : BiomeColors.getFoliageColor(world, pos),
                NaturalTrees.branchOak, NaturalTrees.branchJungle, NaturalTrees.branchDarkOak, NaturalTrees.branchAcacia);
    }

    public void itemColors(ColorHandlerEvent.Item event)
    {
        event.getItemColors().register((itemstack, tintIndex) -> FoliageColors.getSpruce(),  NaturalTrees.branchSpruce);
        event.getItemColors().register((itemstack, tintIndex) -> FoliageColors.getBirch(), NaturalTrees.branchBirch);
        event.getItemColors().register((itemstack, tintIndex) -> FoliageColors.getDefault(),
                NaturalTrees.branchOak, NaturalTrees.branchJungle, NaturalTrees.branchDarkOak, NaturalTrees.branchAcacia);
    }

    /*@SubscribeEvent
    public void onDecorateBiome(DecorateBiomeEvent.Decorate ev)
    {
        if (ev.getType() == DecorateBiomeEvent.Decorate.EventType.TREE)
        {
            Biome gen = ev.getWorld().getBiome(ev.getPlacementPos());
            BiomeDecorator decorator = gen.decorator;
            int i = decorator.treesPerChunk;
            int i = 10;

            if (ev.getRand().nextInt(10) == 0)
            {
                ++i;
            }

            TreeGeneratorBase treeGen = null;
            if (gen instanceof ForestBiome)
            {
                treeGen = generatorOak.combineWith(generatorBirch, 0.1f);
            }
            else if (gen instanceof DarkForestBiome)
            {
                treeGen = generatorDarkOak;
            }
            else if (gen instanceof JungleBiome)
            {
                treeGen = generatorJungle;
            }
            else if (gen instanceof PlainsBiome)
            {
                treeGen = generatorOak;
            }
            else if (gen instanceof SavannaBiome)
            {
                treeGen = generatorAcacia;
            }
            else if (gen instanceof SnowyTundraBiome)
            {
                treeGen = generatorOak;
            }
            else if (gen instanceof SwampBiome)
            {
                treeGen = generatorOak;
            }
            else if (gen instanceof TaigaBiome)
            {
                treeGen = generatorSpruce;
            }

            if (treeGen != null)
            {
                ev.setResult(Event.Result.DENY);


                for (int j = 0; j < i; ++j)
                {
                    int k = ev.getRand().nextInt(16) + 8;
                    int l = ev.getRand().nextInt(16) + 8;

                    BlockPos blockpos = ev.getWorld().getHeight(Heightmap.Type.WORLD_SURFACE, ev.getPlacementPos().add(k, 0, l));

                    treeGen.generateTreeAt(ev.getWorld(), blockpos, ev.getRand());
                }
            }
        }
    }

    @SubscribeEvent
    public void onSaplingGrow(SaplingGrowTreeEvent ev)
    {
        IBlockState state = ev.getWorld().getBlockState(ev.getPos());
        Block block = state.getBlock();
        if (block == Blocks.SAPLING)
        {
            BlockPlanks.EnumType type = state.getValue(BlockSapling.TYPE);

            switch (type)
            {
                case OAK:
                    ev.setResult(Event.Result.DENY);
                    new BirchTreeGenerator().generateTreeAt(ev.getWorld(), ev.getPos(), ev.getRand());
                    break;
            }
        }
    }*/

    public static ResourceLocation location(String path)
    {
        return new ResourceLocation(MODID, path);
    }
}
