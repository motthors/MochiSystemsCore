package mochisystems._mc.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mochisystems._core.Logger;
import mochisystems._core._Core;
import mochisystems.blockcopier.*;
import mochisystems.blockcopier.message.MessageSendModelData;
import mochisystems.blockcopier.message.PacketHandler;
import mochisystems.math.Math;
import mochisystems.math.Quaternion;
import mochisystems.math.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import scala.tools.nsc.doc.base.comment.Body;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public abstract class TileEntityBlocksScannerBase extends TileEntity
        implements IInventory, IBLockCopyHandler, ILimitLine {

    // Settings
    protected final BlocksScanner scanner = InstantiateBlocksCopier();
    public String modelName;
    public int copyNum = 1;
    public int copyMode = 0;
    public int BodyGuide = 0; // 1:head, 2:left arm, 3, right arm, 4:body, 5:left reg, 6:right leg
    public boolean FlagDrawCore = true;
    public boolean FlagDrawEntity = false;
    public boolean isCoreConnector = false;
    public float scale = 1f;
    public float guideScale = 1f;
    protected boolean isOdd = false;

    protected int side;

    // LimitFrame
    protected final LimitFrame limitFrame = new LimitFrame();
    protected int LimitFrameLength;
    protected int LimitFrameWidth;
    protected int LimitFrameHeight;

    // model item slot inventory
    protected ItemStack stackSlot;


    protected abstract BlocksScanner InstantiateBlocksCopier();

    public TileEntityBlocksScannerBase()
    {
        super();
        modelName = "";
    }

    public void Init(int side)
    {
        this.side = side;
        resetFrameLength();
        setRotAxis();
    }

    protected boolean isExistCore()
    {
        if(stackSlot==null)return false;
        return stackSlot.getItem() instanceof IItemBlockModelHolder;
    }

    public void setCopyNum(int flag)
    {
        copyNum += flag;
        if(copyNum > 100)copyNum = 100;
        if(copyNum < 1)copyNum = 1;
    }

    public void setBodyGuide(int idx)
    {
        BodyGuide = idx;
        setScale(idx==0 ? 1 : 0.25f);
        switch(idx)
        {
            case 2:
            case 5: modelName = "L"; break;
            case 3:
            case 6: modelName = "R"; break;
            case 1:
            case 4: modelName = "Body"; break;
        }
    }

    public void setScale(float s)
    {
        scale = s;
        scale = Math.Clamp(scale, 0, 100);
        guideScale = 1 / s;
    }

    public LimitFrame GetLimitFrame()
    {
        return limitFrame;
    }

    public int getLimitFrameLength() {
        return LimitFrameLength;
    }

    public int getLimitFrameWidth() {
        return LimitFrameWidth;
    }

    public int getLimitFrameHeight() {
        return LimitFrameHeight;
    }

    public abstract void resetFrameLength();

    protected abstract void createVertex();


    public void toggleFlagDrawCore()
    {
        FlagDrawCore = !FlagDrawCore;
    }

    public void toggleFlagDrawEntity()
    {
        FlagDrawEntity = !FlagDrawEntity;
    }

    public void toggleFlagCoreIsConnector()
    {
        isCoreConnector = !isCoreConnector;
    }

    public void toggleFlagFrameLenIsOdd()
    {
        isOdd = !isOdd;
    }

    public void rotateCopyMode()
    {
        copyMode = (copyMode+1)%2;
    }

    @Override
    public void updateEntity()
    {
        if (!worldObj.isRemote) return;
        scanner.UpdateProgressStatus();
    }

    private boolean canCreateCore()
    {
        return isExistCore() || Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode;
    }

    @SideOnly(Side.CLIENT)
    public void startConstructWheel()
    {
        if(!canCreateCore())return;
        scanner.StartCopy(this, xCoord, yCoord, zCoord, limitFrame, FlagDrawEntity);
        this.markDirty();
    }

    @SideOnly(Side.CLIENT)
    public float getCookProgress()
    {
        return scanner.GetProgress();
    }

    public boolean isCrafting()
    {
        return scanner.isCrafting();
    }

    public void render(Tessellator tess)
    {
        limitFrame.render(tess);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
        createVertex();
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        FlagDrawCore = nbt.getBoolean("flagdrawcore");
        FlagDrawEntity = nbt.getBoolean("flagdrawentity");
        isOdd = nbt.getBoolean("flagidodd");
        modelName = nbt.getString("wheelname");
        copyNum = nbt.getInteger("copynum");
        copyMode = nbt.getInteger("copyMode");
        side = nbt.getInteger("side");
        LimitFrameLength = nbt.getInteger("framelen");
        LimitFrameWidth = nbt.getInteger("framewid");
        LimitFrameHeight = nbt.getInteger("frameheight");
        BodyGuide = nbt.getInteger("BodyGuide");
        scale = nbt.getFloat("scale");
        guideScale = nbt.getFloat("guideScale");
        createVertex();
        setRotAxis();
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("flagdrawcore", FlagDrawCore);
        nbt.setBoolean("flagdrawentity", FlagDrawEntity);
        nbt.setBoolean("flagidodd", isOdd);
        nbt.setString("wheelname", modelName);
        nbt.setInteger("copynum", copyNum);
        nbt.setInteger("copyMode", copyMode);
        nbt.setInteger("side", side);
        nbt.setInteger("framelen", LimitFrameLength);
        nbt.setInteger("framewid", LimitFrameWidth);
        nbt.setInteger("frameheight", LimitFrameHeight);
        nbt.setInteger("BodyGuide", BodyGuide);
        nbt.setFloat("scale", scale);
        nbt.setFloat("guideScale", guideScale);
    }

    //////////// IBLockCopyHandler ///////////

    @Override
    public boolean isExceptBlock(Block b)
    {
        return false;
    }

    @Override
    public void OnComplete(NBTTagCompound nbt)
    {
        sendNBTToServer(nbt);
    }

    @Override
    public void OnFailure()
    {
        Logger.warn("Coping blocks is abnormal end.");
    }

    @Override
    public Block getBlock(int x, int y, int z)
    {
        return worldObj.getBlock(x, y, z);
    }
    @Override
    public int getBlockMetadata(int x, int y, int z)
    {
        return worldObj.getBlockMetadata(x, y, z);
    }
    @Override
    public TileEntity getTileEntity(int x, int y, int z)
    {
        return worldObj.getTileEntity(x, y, z);
    }
    @Override
    @SuppressWarnings("unchecked")
    public List<Entity> getEntities(AxisAlignedBB aabb)
    {
        return worldObj.getEntitiesWithinAABB(Entity.class, aabb);
    }

    private void sendNBTToServer(NBTTagCompound nbt)
    {
        byte[] bytearray = null;
        try {
            bytearray = CompressedStreamTools.compress(nbt);
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //�������M
        int bytenum = bytearray.length;
        int divnum = bytenum / (20*1024) + 1;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytearray);
        for(int i=0; i<divnum; ++i)
        {
            byte[] divarray = new byte[(20*1024)];
            inputStream.read(divarray, 0, (20*1024));
            MessageSendModelData packet = new MessageSendModelData(xCoord, yCoord, zCoord, i, divnum, divarray);
            PacketHandler.INSTANCE.sendToServer(packet);
        }
    }


    private byte arrayDataIndex[][];
    public final void RecieveBlockData(EntityPlayer player, int idx, int total, byte[] arrayByte)
    {
        if(arrayDataIndex == null || arrayDataIndex.length != total) arrayDataIndex = new byte[total][];
        arrayDataIndex[idx] = arrayByte;

        int i=0;
        for( ; i<total; ++i)
        {
            if(arrayDataIndex[i] == null)break;
        }
        if(i != total)return;

        ByteArrayOutputStream allbytearray = new ByteArrayOutputStream();
        for(i=0; i<total; ++i)
        {
            allbytearray.write(arrayDataIndex[i], 0, arrayDataIndex[i].length);
        }
        arrayByte = allbytearray.toByteArray();

        ByteArrayInputStream stream = new ByteArrayInputStream(arrayByte);
        NBTTagCompound nbt = null;
        try { nbt = CompressedStreamTools.readCompressed(stream); } catch (IOException e) { e.printStackTrace(); }
        if(nbt == null)
        {
            arrayDataIndex = null;
            return;
        }

        //�H��t���O
//        NBTTagCompound base = new NBTTagCompound();
//        base.setTag("base", nbt);
        ItemStack tempStack = InstantiateModelItem();

        this.modelName = ((NBTTagCompound)nbt.getTag("model")).getString("ModelName");

        String orgname = tempStack.getItem().getItemStackDisplayName(tempStack);
        tempStack.setStackDisplayName(orgname+" : "+modelName);

        RecieveExtBlockData(nbt);

        arrayDataIndex = null;
        worldObj.playSoundEffect(xCoord+0.5, yCoord+0.5, zCoord+0.5, _Core.MODID+":complete", 1.0F, 0.9F);
        tempStack.setTagCompound(nbt);


        if(stackSlot==null) stackSlot = tempStack;
        else
        {
//            if(stackSlot.hasTagCompound()) Logger.debugInfo( nbt.hashCode() + " : " + stackSlot.getTagCompound().hashCode());
            if(Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode &&
                    stackSlot.hasTagCompound() &&
                    tempStack.getTagCompound().hashCode() == stackSlot.getTagCompound().hashCode())
            {
                stackSlot.stackSize = java.lang.Math.min(stackSlot.stackSize+1, stackSlot.getMaxStackSize());
            }
            stackSlot.setTagCompound(nbt);
        }

    }


    protected void RecieveExtBlockData(NBTTagCompound nbt){}



    public float rotConst_meta2 = 0, rotMeta2_side = 0;
    public Vec3d rotvecMeta2_side = new Vec3d(0, 0, 0);
    private void setRotAxis()
    {
        switch(side)
        {
            case -1 : rotMeta2_side = 0;
            case 0 : /*rotationAxis.y = 1;*/ rotvecMeta2_side.x = 1; rotMeta2_side = -90; break;
            case 1 : /*rotationAxis.y =-1;*/ rotvecMeta2_side.x = 1; rotMeta2_side = 90; break;
            case 2 : /*rotationAxis.z = 1;*/ rotvecMeta2_side.y = 1; rotMeta2_side = 0;  break;
            case 3 : /*rotationAxis.z =-1;*/ rotvecMeta2_side.y = 1; rotMeta2_side = 180; break;
            case 4 : /*rotationAxis.x = 1;*/ rotvecMeta2_side.y = 1; rotMeta2_side = 90; break;
            case 5 : /*rotationAxis.x =-1;*/ rotvecMeta2_side.y = 1; rotMeta2_side = -90; break;
        }
    }


    /////////////////////////SidedInventry///////////////////////

    @Override
    public int getSizeInventory(){return 1;}

    @Override
    public ItemStack getStackInSlot(int idx) {return this.stackSlot;}

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int slotIdx, int decrAmount)
    {
        if(slotIdx != 0) return null;

        if (this.stackSlot != null)
        {
            ItemStack itemstack;

            if (this.stackSlot.stackSize <= decrAmount)
            {
                itemstack = this.stackSlot;
                this.stackSlot = null;
                return itemstack;
            }
            else
            {
                itemstack = this.stackSlot.splitStack(decrAmount);
                if (this.stackSlot.stackSize == 0)
                {
                    this.stackSlot = null;
                }
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotIdx)
    {
        if(slotIdx != 0) return null;
        if (this.stackSlot != null)
        {
            ItemStack itemstack = this.stackSlot;
            this.stackSlot = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slotIdx, ItemStack itemstack)
    {
        if(slotIdx != 0) return;
        if((itemstack!=null) && !(itemstack.getItem() instanceof IItemBlockModelHolder))return;
        this.stackSlot = itemstack;

        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
        {
            itemstack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName(){return "container.mochisystems.modelconstructor";}

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {return 64;}

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
    {
        return true;
//		return this.worldObj.getTileEntity(this.CorePosX, this.CorePosY, this.CorePosZ) != this ? false : p_70300_1_.getDistanceSq((double)this.CorePosX + 0.5D, (double)this.CorePosY + 0.5D, (double)this.CorePosZ + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int slotidx, ItemStack itemstack)
    {
        return false;
    }

}
