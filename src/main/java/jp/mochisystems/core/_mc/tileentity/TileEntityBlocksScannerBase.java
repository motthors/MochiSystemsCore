package jp.mochisystems.core._mc.tileentity;

import jp.mochisystems.core.blockcopier.*;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.ClampValue;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class TileEntityBlocksScannerBase extends TileEntity
        implements IInventory, IBLockCopyHandler, ILimitFrameHolder, ITickable, BlockModelSender.IHandler {

    // Settings
    protected final BlocksScanner scanner = InstantiateBlocksCopier(this);
    private final BlockModelSender sender = new BlockModelSender(this);
    protected String modelName;
    private boolean isDrawCore = true;
    public boolean isDrawEntity = false;
    public boolean isCoreConnector = false;
    public boolean TrueCopy = false;
    public final Vec3d scale = new Vec3d(1, 1, 1);
    public ClampValue.Int copyNum = ClampValue.Int.of(1, 1, 1000);

    protected EnumFacing side = EnumFacing.UP;
    public EnumFacing GetSide(){return side;}

    // LimitFrame
    protected final LimitFrame limitFrame = new LimitFrame();

    // model item slot inventory
    protected ItemStack stackSlot = ItemStack.EMPTY;

    public static EntityPlayer ChangeUser;


    protected abstract BlocksScanner InstantiateBlocksCopier(IBLockCopyHandler handler);
    public abstract ItemStack InstantiateModelItem();

    public TileEntityBlocksScannerBase()
    {
        super();
        modelName = "";
    }

    public void Init(EnumFacing side)
    {
        this.side = side;
    }

    protected boolean isExistCore()
    {
        if(stackSlot.isEmpty()) return false;
        return stackSlot.getItem() instanceof IItemBlockModelHolder;
    }



    public LimitFrame GetLimitFrame()
    {
        return limitFrame;
    }



    @Override
    public void update()
    {
        if (!world.isRemote) return;
    }

    private boolean canCreateCore()
    {
        return isExistCore() || Minecraft.getMinecraft().player.capabilities.isCreativeMode;
    }

    @SideOnly(Side.CLIENT)
    public void startScanning()
    {
        if(!canCreateCore()) return;
        scanner.RegisterSettings(pos.getX(), pos.getY(), pos.getZ(), limitFrame, isDrawEntity, TrueCopy);
        scanner.StartParallelScan();
        this.markDirty();
    }

    @SideOnly(Side.CLIENT)
    public float getCookProgress()
    {
        return scanner.GetProgress();
    }


    @Override
    public SPacketUpdateTileEntity getUpdatePacket(){
        NBTTagCompound nbtTag = new NBTTagCompound();
        writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 9, nbtTag);
    }

    @Override
    public void onDataPacket(@Nonnull NetworkManager net, SPacketUpdateTileEntity pkt){
        readFromNBT( pkt.getNbtCompound());
    }

    @Nonnull
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }

    public final void readFromNBT(@Nonnull NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        side = EnumFacing.getFront(nbt.getInteger("side"));
        if(nbt.hasKey("slotItem"))
            stackSlot = new ItemStack(nbt.getCompoundTag("slotItem"));
        ReadParamFromNBT(nbt);
    }

    protected void ReadParamFromNBT(NBTTagCompound nbt)
    {
        limitFrame.ReadFromNBT(nbt);
        scale.ReadFromNBT("srcScale", nbt);
        isDrawCore = nbt.getBoolean("isDrawCore");
        isDrawEntity = nbt.getBoolean("isDrawEntity");
        modelName = nbt.getString("ModelName");
        TrueCopy = nbt.getBoolean("TrueCopy");
        isCoreConnector = nbt.getBoolean("isCoreConnector");
        copyNum.Set(nbt.getInteger("copyNum"));
    }

    @Nonnull
    public final NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("side", side.getIndex());

        NBTTagCompound stackNbtTag = new NBTTagCompound();
        stackSlot.writeToNBT(stackNbtTag);
        nbt.setTag("slotItem", stackNbtTag);

        WriteParamToNBT(nbt);
        return nbt;
    }

    protected NBTTagCompound WriteParamToNBT(NBTTagCompound nbt)
    {
        limitFrame.WriteToNBT(nbt);
        scale.WriteToNBT("srcScale", nbt);
        nbt.setInteger("copyNum", copyNum.Get());
        nbt.setBoolean("isDrawCore", isDrawCore);
        nbt.setBoolean("isDrawEntity", isDrawEntity);
        nbt.setString("ModelName", modelName);
        nbt.setBoolean("TrueCopy", TrueCopy);
        nbt.setBoolean("isCoreConnector", isCoreConnector);
        return nbt;
    }

    //////////// for Scan : IBLockCopyHandler ///////////

    @Override
    public BlockPos GetHandlerPos()
    {
        return pos;
    }

    @Override
    public IBlockState GetBlockState(BlockPos pos)
    {
        return world.getBlockState(pos);
    }
    @Override
    public TileEntity getTileEntity(BlockPos pos)
    {
        return world.getTileEntity(new BlockPos(pos));
    }
    @Override
    public List<Entity> getEntities(float mx, float my, float mz, float xx, float xy, float xz)
    {
        return world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(mx, my, mz, xx, xy, xz));
    }


    @Override
    public void OnCompleteReceive(NBTTagCompound nbt)
    {
        boolean isCreative = Minecraft.getMinecraft().player.capabilities.isCreativeMode;
        if(stackSlot.isEmpty()) {
            if(isCreative){
                stackSlot = InstantiateModelItem();
                stackSlot.setTagCompound(nbt);
            }
            else {
                return;
            }
        }
        else{
            ItemStack newStack = InstantiateModelItem();
            newStack.setTagCompound(nbt);

//            if(stackSlot.hasTagCompound()) Logger.debugInfo(newStack.getTagCompound().hashCode() + " : " + stackSlot.getTagCompound().hashCode());
            if(isCreative &&
                    stackSlot.hasTagCompound() &&
                    newStack.getTagCompound().hashCode() == stackSlot.getTagCompound().hashCode())
            {
                stackSlot.grow(1);
            }
            else {
                int num = stackSlot.getCount();
                stackSlot = newStack;
                stackSlot.setCount(num);
            }
        }

        world.playSound(null,pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0F, 0.9F);
    }


    @Override
    public void registerExternalParam(NBTTagCompound model, NBTTagCompound nbt)
    {
        // => FerrisPartBase
        model.setString("ModelName", modelName.isEmpty()?"-NoName-":modelName);
        scale.WriteToNBT("scale", model);
        model.setInteger("copyNum", copyNum.Get());
        model.setBoolean("isActive", true);

        // => BlockReplicator
        model.setBoolean("isDrawCore", isDrawCore);
        model.setByte("constructorside", (byte)side.getIndex());
        model.setBoolean("TrueCopy", TrueCopy);
        model.setInteger("copiedPosX", pos.getX()); // CTM
        model.setInteger("copiedPosY", pos.getY());
        model.setInteger("copiedPosZ", pos.getZ());
        Vec3d originLocal = GetOriginLocalOffset();
        model.setInteger("originlocalx", (int)originLocal.x);
        model.setInteger("originlocaly", (int)originLocal.y);
        model.setInteger("originlocalz", (int)originLocal.z);
    }
    public Vec3d GetOriginLocalOffset(){
        double d = TrueCopy ? 1 : 0;
        return new Vec3d(
                -limitFrame.getmx() - d,
                -limitFrame.getmy() - d,
                -limitFrame.getmz() - d);
    }





    @Override
    public BlocksScanner GetScanner() {
        return scanner;
    }
    @Override
    public BlockModelSender GetSender() {
        return sender;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }
    //INFINITE_EXTENT_AABBの説明を見ると何かヒントがあるかもしれない？

    /////////////////////////IInventry///////////////////////

    @Override
    public boolean isEmpty() {
        return stackSlot.isEmpty();
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        stackSlot = ItemStack.EMPTY;
        return stackSlot;
    }

    @Override
    public int getSizeInventory(){return 1;}

    @Override
    public ItemStack getStackInSlot(int idx) {return this.stackSlot;}

    @Override
    public ItemStack decrStackSize(int slotIdx, int decrAmount)
    {
        if(slotIdx != 0) return ItemStack.EMPTY;
        return !stackSlot.isEmpty() ? stackSlot.splitStack(decrAmount) : ItemStack.EMPTY;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

//    @Override
//    public ItemStack getStackInSlotOnClosing(int slotIdx)
//    {
//        if(slotIdx != 0) return null;
//        if (this.stackSlot != null)
//        {
//            ItemStack itemstack = this.stackSlot;
//            this.stackSlot = null;
//            return itemstack;
//        }
//        else
//        {
//            return null;
//        }
//    }
    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
    }

    @Override
    public void setInventorySlotContents(int slotIdx, ItemStack itemstack)
    {
        if(slotIdx != 0) return;
        if( !(itemstack.getItem() instanceof IItemBlockModelHolder))return;
        this.stackSlot = itemstack;
        if (stackSlot.getCount() > this.getInventoryStackLimit())
        {
            stackSlot.setCount(this.getInventoryStackLimit());
        }
    }

    @Nonnull
    @Override
    public String getName(){return "container.mochisystems.modelconstructor";}

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {return 64;}

    @Override
    public boolean isUsableByPlayer(@Nonnull EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory(@Nonnull EntityPlayer player){}

    @Override
    public void closeInventory(@Nonnull EntityPlayer player) {}

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    @Override
    public boolean isItemValidForSlot(int slotIdx, @Nonnull ItemStack itemstack)
    {
        return false;
    }

}
