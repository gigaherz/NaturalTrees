package gigaherz.nattrees;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
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

    @Mod.Instance(value = NaturalTrees.MODID)
    public static NaturalTrees instance;

    @SidedProxy(clientSide = "gigaherz.nattrees.client.ClientProxy", serverSide = "gigaherz.nattrees.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        branchOak = new BlockBranch(Material.wood).setHardness(1).setStepSound(Block.soundTypeWood).setUnlocalizedName("branchOak");
        GameRegistry.registerBlock(branchOak, "branchOak");

        GameRegistry.registerTileEntity(TileBranch.class, "branchTile");

        proxy.registerPreRenderers();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerRenderers();

        BlockFence f;
    }
}
