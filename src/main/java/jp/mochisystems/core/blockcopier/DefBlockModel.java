package jp.mochisystems.core.blockcopier;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.entity.EntityBlockModelCollider;
import jp.mochisystems.core._mc.renderer.BlocksRenderer;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.util.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class DefBlockModel implements IBlockModel {

    public final IModelController controller;

    public final MTYBlockAccess blockAccess;

    public BlockModelColliderModule colliderModule;

    private final BlocksRenderer renderer;
    public BlocksRenderer GetRenderer(){return renderer;}

    protected NBTTagCompound partNbtOnConstruct;
    private boolean isValidated;
    private Connector connector = new Connector("");

    public DefBlockModel(IModelController controller)
    {
        this.controller = controller;
        blockAccess = new MTYBlockAccess();
        renderer = _Core.proxy.GetConstructorBlocksVertex(blockAccess);
        colliderModule = new BlockModelColliderModule(blockAccess, this, controller);
    }

    @Override
    public void SetWorld(World world) {
        blockAccess.setWorld(world);
        colliderModule.SetWorld(world);
    }

    @Override
    public boolean IsLock() {
        return false;
    }

    @Override
    public boolean IsInvalid() {
        return controller.IsInvalid();
    }

    @Override
    public void Reset() {

    }

    @Override
    public void Update() {
        if(!isValidated)
        {
            isValidated = true;
            Validate();
        }
        blockAccess.updateTileEntity();
        colliderModule.Update(null);
    }

    public void Validate()
    {
        if(renderer!=null) renderer.delete();
        blockAccess.setWorld(controller.World());
        blockAccess.constructFromTag(partNbtOnConstruct,
                MathHelper.floor(controller.CorePosX()),
                MathHelper.floor(controller.CorePosY()),
                MathHelper.floor(controller.CorePosZ()),
                true, renderer::CompileRenderer);
    }

    @Override
    public void Unload() {
//        soundManager.Invalid();
    }

    @Override
    public void Invalidate() {
        if(renderer!=null) renderer.delete();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagCompound modelData = nbt.getCompoundTag("model");
        partNbtOnConstruct = modelData.copy();
        colliderModule.ReadFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        if(partNbtOnConstruct == null) partNbtOnConstruct = new NBTTagCompound();
        nbt.setTag("model", partNbtOnConstruct);
        colliderModule.WriteToNBT(nbt);
    }

    @Override
    public void setRSPower(int power) {

    }


    @SideOnly(Side.CLIENT)
    @Override
    public void RenderModel(int pass, float partialTick) {
        if(pass==0)renderer.render();
        else renderer.render2();
    }

    @Override
    public boolean HasChild(){return false;}

    @Override
    public IModel[] GetChildren(){return null;}

    @Override
    public String GetName(){return "";}

    @Override
    public void SetVisible(boolean active){}

    @Override
    public void SetOffset(float x, float y, float z){}

    @Override
    public void SetRotation(Quaternion q){}

    @Override
    public CommonAddress GetCommonAddress() {
        return controller.GetCommonAddress();
    }

    @Override
    public double ModelPosX() {
        return controller.CorePosX();
    }
    @Override
    public double ModelPosY() {
        return controller.CorePosY();
    }
    @Override
    public double ModelPosZ() {
        return controller.CorePosZ();
    }

    /////////////////// IBlockModel
    @Override
    public MTYBlockAccess GetBlockAccess() {
        return blockAccess;
    }


    @Override
    public void UpdateChildConnector(@Nonnull Connector connector) {

    }

    @Override
    public void RegisterColEntity(EntityBlockModelCollider entity) {
        colliderModule.AddEntity(entity);
    }

    @Override
    public boolean IsEnableCollider() {
        return colliderModule.IsEnableCollider();
    }

    @Override
    public void ToggleEnableCollider() {
        colliderModule.ToggleActive();
    }
}
