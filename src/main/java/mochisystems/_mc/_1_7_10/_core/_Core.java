package mochisystems._mc._1_7_10._core;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mochisystems._mc._1_7_10.block.BlockRemoteController;
import mochisystems._mc._1_7_10.block.BlockRemoteReceiver;
import mochisystems._mc._1_7_10.block.itemBlockRemoteController;
import mochisystems._mc._1_7_10.item.ItemWrenchPlaceBlock;
import mochisystems._mc._1_7_10.tileentity.tileEntityRemoteController;
import mochisystems._mc._1_7_10.tileentity.tileEntityRemoteReceiver;
import mochisystems._mc._1_7_10.message.PacketHandler;
import mochisystems._mc._1_7_10.proxy.IProxy;
import mochisystems._mc._1_7_10.bufferedrenderer.SmartBufferManager;
import mochisystems._mc._1_7_10.eventhandler.TickEventHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

@Mod(
        modid = _Core.MODID,
        name = "Mochisystems Core",
        version = "1.0",
        dependencies = "required-after:Forge@[10.12.1.1090,)",
        useMetadata = true
)
public class _Core {
    public static final String MODID = "mochisystemscore";
    @Mod.Instance(_Core.MODID)
    public static _Core Instance;

    //proxy////////////////////////////////////////
    @SidedProxy(clientSide = "mochisystems._mc._1_7_10.proxy.ClientProxy", serverSide = "mochisystems._mc._1_7_10.proxy.ServerProxy")
    public static IProxy proxy;

    public SmartBufferManager smartBufferManager;

    public static Block blockRemoteController = new BlockRemoteController();
    public static Block blockRemoteReceiver = new BlockRemoteReceiver();
    public static ItemBlock ItemRemote = new ItemBlock(blockRemoteController);

    public static Item ItemStick = new ItemWrenchPlaceBlock();

    public static CreativeTabs Tab = new CreativeTabs("MochiSystemsCore"){
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return ItemRemote;
        }
        @Override
        @SideOnly(Side.CLIENT)
        public String getTranslatedTabLabel()
        {
            return "MochiSystemsCore";
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) throws Exception
    {
        PacketHandler.init();
        proxy.PreInit();
        FMLCommonHandler.instance().bus().register(new TickEventHandler());
        GameRegistry.registerTileEntity(tileEntityRemoteController.class, "mochisystemscore:TileEntityRemoteController");
        GameRegistry.registerTileEntity(tileEntityRemoteReceiver.class, "mochisystemscore:TileEntityRemoteReceiver");

    }

    @Mod.EventHandler
    public void Init(FMLInitializationEvent e)
    {
        blockRemoteController
                .setBlockName("RemoteController")
                .setBlockTextureName(_Core.MODID+":remoteController")
                .setCreativeTab(Tab);
        GameRegistry.registerBlock(blockRemoteController, itemBlockRemoteController.class, "Ferris.RemoteController");

        ItemStick.setCreativeTab(Tab);
        ItemStick.setUnlocalizedName("BlockPlacer");
        ItemStick.setTextureName(MODID+":wrench_p");
        ItemStick.setMaxStackSize(1);
        GameRegistry.registerItem(ItemStick, "ItemWrenchPlaceBlock");
        GameRegistry.addRecipe(new ItemStack(ItemStick,1,0),
                "D  ",
                " I ",
                "  I",
                'D',Blocks.dirt,
                'I',Items.stick
        );

        GameRegistry.addRecipe(new ItemStack(blockRemoteController),
                "OTO",
                "ORO",
                "OOO",
                'O', Blocks.stone,
                'T',Blocks.redstone_torch,
                'R', Items.redstone
        );

        blockRemoteReceiver
                .setBlockName("RemoteReceiver")
                .setBlockTextureName(_Core.MODID+":RemoteReceiver")
                .setCreativeTab(Tab);
        GameRegistry.registerBlock(blockRemoteReceiver, "Ferris.RemoteReceiver");

        GameRegistry.addRecipe(new ItemStack(blockRemoteReceiver),
                "OTO",
                "OEO",
                "OOO",
                'O', Blocks.stone,
                'T',Blocks.redstone_torch,
                'E', Items.ender_eye
        );
    }

//    @Mod.EventHandler
//    public void postInit(FMLPostInitializationEvent e)
//    {
//        GuiDragController.ResetSpecialCamera();
//    }

    public static void BindBlocksTextureMap()
    {
        TextureManager texturemanager = TileEntityRendererDispatcher.instance.field_147553_e;
        texturemanager.bindTexture(TextureMap.locationBlocksTexture);
    }

    public String I18n(String uri)
    {
        return StatCollector.translateToLocal(uri);
    }
}
