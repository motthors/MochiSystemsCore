package jp.mochisystems.core._mc._core;

import jp.mochisystems.core._mc.block.*;
import jp.mochisystems.core._mc.entity.EntityBlockModelCollider;
import jp.mochisystems.core._mc.item.ItemMultiTab;
import jp.mochisystems.core._mc.renderer.RenderEntityFerrisCollider;
import jp.mochisystems.core.bufferedRenderer.SmartBufferManager;
import jp.mochisystems.core._mc.eventhandler.TickEventHandler;
import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core._mc.item.ItemPlacingBlockStick;
import jp.mochisystems.core._mc.item.itemBlockBlockModelTester;
import jp.mochisystems.core._mc.item.itemBlockRemoteController;
import jp.mochisystems.core._mc.message.PacketHandler;
import jp.mochisystems.core._mc.proxy.IProxy;
import jp.mochisystems.core._mc.renderer.renderTileEntityBlockModel;
import jp.mochisystems.core._mc.renderer.renderTileEntityLimitFrame;
import jp.mochisystems.core._mc.tileentity.TileEntityBlockModelCutter;
import jp.mochisystems.core._mc.tileentity.TileEntityModelBase;
import jp.mochisystems.core._mc.tileentity.tileEntityRemoteController;
import jp.mochisystems.core._mc.tileentity.tileEntityRemoteReceiver;
import jp.mochisystems.core.manager.EntityWearingModelManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.File;
import java.util.Arrays;

@Mod(
        modid = _Core.MODID,
        name = "MochiSystems Core",
        version = "1.0alpha3",
        useMetadata = true
)
public class _Core {
    public static final String MODID = "mochisystemscore";
    @Mod.Instance(_Core.MODID)
    public static _Core Instance;

    //proxy////////////////////////////////////////
    @SidedProxy(
            clientSide = "jp.mochisystems.core._mc.proxy.ClientProxy",
            serverSide = "jp.mochisystems.core._mc.proxy.ServerProxy")
    public static IProxy proxy;

    public SmartBufferManager smartBufferManager;

    public static final Block blockRemoteController = new BlockRemoteController();
    public static final Block blockRemoteReceiver = new BlockRemoteReceiver();
    public static final Block blockModelCutter = new BlockModelCutter();
    public static final Block blockBlockModelTester = new BlockModelCoreBase.Tester();
    public static final Block blockFileManager = new BlockFileManager();
//    private static final Block test = new Block(Material.GROUND);

    public static final ItemBlock ItemRemoteController = new itemBlockRemoteController(blockRemoteController);
    private static final ItemBlock ItemRemoteReceiver = new ItemMultiTab._ItemBlock(blockRemoteReceiver);
    private static final ItemBlock ItemFileManager = new ItemMultiTab._ItemBlock(blockFileManager);
    private static final ItemBlock ItemCutter = new ItemBlockCutter(blockModelCutter);
//    private static final ItemBlock ItemBlockTester = new itemBlockBlockModelTester(blockBlockModelTester);

    public static Item ItemStick = new ItemPlacingBlockStick();

    public static final int GUIID_FileManager = 4;
    public static final int GUIID_Cutter = 5;
    public static final int GUIID_BlockModel = 1;
    public static final int GUIID_OpenWearingModel = 9;

    public CreativeTabs[] TabGroup = new CreativeTabs[0];
    public void AddTab(CreativeTabs tab)
    {
        TabGroup = Arrays.copyOf(TabGroup, TabGroup.length+1);
        TabGroup[TabGroup.length-1] = tab;
    }

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new TickEventHandler());
        PacketHandler.init();
        proxy.PreInit();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GUIHandler());
        new File("./BlockModel/").mkdirs();
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {

        event.getRegistry().registerAll(
                blockRemoteController.setUnlocalizedName("RemoteController").setRegistryName(MODID, "remotecontroller"),
                blockRemoteReceiver.setUnlocalizedName("RemoteReceiver").setRegistryName(MODID, "RemoteReceiver"),
                blockModelCutter.setUnlocalizedName("Cutter").setRegistryName(MODID, "modelcutter"),
                blockFileManager.setUnlocalizedName("FileManager").setRegistryName(MODID, "filemanager")
        );

        RegisterTileEntity(TileEntityModelBase.BlockModelTester.class, MODID, "TileEntityBlockModelTest");
        RegisterTileEntity(tileEntityRemoteController.class, MODID, "TileEntityRemoteController");
        RegisterTileEntity(tileEntityRemoteReceiver.class, MODID, "TileEntityRemoteReceiver");
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                ItemRemoteController.setRegistryName(MODID, "remotecontroller"),
//                ItemRemoteReceiver.setRegistryName(MODID, "remotereceiver"),
                ItemFileManager.setRegistryName(MODID, "itemblockfilemanager"),
                ItemCutter.setRegistryName(MODID, "itemblockcutter")
//                new itemBlockBlockModelTester(blockBlockModelTester).setRegistryName(MODID, "tester"),
//                new ItemBlock(test).setRegistryName(MODID, "test")
        );
//        ItemStick.setCreativeTab(Tab);
        ItemStick.setUnlocalizedName("BlockPlacer");
        ItemStick.setRegistryName("ItemPlacingBlockStick");
        ItemStick.setMaxStackSize(1);

        event.getRegistry().registerAll(ItemStick);
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        EntityEntry[] entities = new EntityEntry[]{
                EntityEntryBuilder.create().entity(EntityBlockModelCollider.class)
                        .id(MODID+":blockCollider", 1).name(MODID+":blockCollider")
                        .tracker(64, 1000, false)
                        .build(),
        };
        event.getRegistry().registerAll(entities);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {

//        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockRemoteController), 0,
//                new ModelResourceLocation(blockRemoteController.getRegistryName(), "inventory"));
//        ModelLoader.setCustomStateMapper(blockRemoteController, new StateMap.Builder().build());
//        ModelLoader.setCustomStateMapper(blockRemoteReceiver, new StateMap.Builder().build());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlockModelCutter.class, new renderTileEntityLimitFrame());

        ModelLoader.setCustomStateMapper(blockFileManager,  new StateMap.Builder().build());
        ModelLoader.setCustomStateMapper(blockModelCutter,  new StateMap.Builder().build());

        ModelLoader.setCustomModelResourceLocation(ItemStick, 0, new ModelResourceLocation(ItemStick.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockRemoteController), 0, new ModelResourceLocation(blockRemoteController.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockRemoteReceiver), 0, new ModelResourceLocation(blockRemoteReceiver.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockModelCutter), 0, new ModelResourceLocation(blockModelCutter.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockFileManager), 0, new ModelResourceLocation(blockFileManager.getRegistryName(), "inventory"));
//        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockBlockModelTester), 0, new ModelResourceLocation(blockBlockModelTester.getRegistryName(), "inventory"));
//        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(test), 0, new ModelResourceLocation(test.getRegistryName(), "inventory"));

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityModelBase.class, new renderTileEntityBlockModel());

        RenderingRegistry.registerEntityRenderingHandler(EntityBlockModelCollider.class, RenderEntityFerrisCollider::new);
    }



    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event)
    {

    }

    public static void BindBlocksTextureMap()
    {
        TextureManager texturemanager = TileEntityRendererDispatcher.instance.renderEngine;
        texturemanager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    public static String I18n(String uri)
    {
        return I18n.format(uri);
    }

    public static void RegisterBlock(IForgeRegistry<Block> registry, Block block, String name, String modId, String registryName, CreativeTabs tab)
    {
        block.setUnlocalizedName(name).setRegistryName(modId+":"+registryName).setCreativeTab(tab);
        registry.register(block);
    }

    public static void RegisterTileEntity(Class<? extends TileEntity> clazz, String modId, String key)
    {
        //1.7.10, 1.12.2
        GameRegistry.registerTileEntity(clazz, new ResourceLocation(modId+":"+key));
    }

    public static String GetBlockName(Block block)
    {
        //1.7.10
//        return Block.blockRegistry.getNameForObject(block);
        //1.12.2
        return Block.REGISTRY.getNameForObject(block).toString();
    }

    public static int GetBlockID(Block block)
    {
        //1.7.10
//        return Block.blockRegistry.getIDForObject(b);
        //1.12.2
        return Block.REGISTRY.getIDForObject(block);
    }

    public static Block AIR = Blocks.AIR;

//    @SideOnly(Side.CLIENT)
//    public static RegionRenderCacheBuilder BlockModelBuffer = new RegionRenderCacheBuilder();
//    public static BufferBuilder BlocKModelBuffer = new BufferBuilder(262144);

    @Config.LangKey("config.client")
    @Config(modid = MODID, category = "client")
    public static class CONFIG_ANNOTATIONS
    {
        @Config.Comment("is pro mode")
        public static boolean isProGui = false;
    }

    public static void Check()
    {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages("Injected text");
    }
}
