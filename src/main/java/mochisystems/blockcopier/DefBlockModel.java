package mochisystems.blockcopier;

import mochisystems._mc._1_7_10.world.MTYBlockAccess;
import mochisystems.util.IModel;
import mochisystems.util.IModelController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class DefBlockModel implements IModel {

    public final IModelController controller;

    private final MTYBlockAccess blockAccess = new MTYBlockAccess(null);
    private final BlocksRenderer renderer = new BlocksRenderer(blockAccess);
    public BlocksRenderer GetRenderer(){return renderer;}

    protected NBTTagCompound partNbtOnConstruct;
    private boolean isValidated;

    public DefBlockModel(IModelController controller)
    {
        this.controller = controller;
    }

    @Override
    public boolean IsLock() {
        return false;
    }

    @Override
    public void Reset() {

    }

    @Override
    public void Update() {
        if(!isValidated)
        {
            isValidated = true;
            blockAccess.constructFromTag(partNbtOnConstruct,
                    controller.CorePosX(), controller.CorePosY(), controller.CorePosZ(),
                    true, renderer::CompileRenderer);
            renderer.delete();
        }
        blockAccess.updateTileEntity();
    }

    @Override
    public void Unload() {
//        soundManager.Invalid();
    }

    @Override
    public void Invalidate() {
        renderer.delete();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        partNbtOnConstruct = (NBTTagCompound) nbt.getTag("model").copy();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        if(partNbtOnConstruct == null) partNbtOnConstruct = new NBTTagCompound();
        nbt.setTag("model", partNbtOnConstruct);
    }

    @Override
    public void setRSPower(int power) {

    }

    @Override
    public void SetWorld(World world) {
        blockAccess.setWorld(world);
        blockAccess.setWorldToTileEntities(world);
    }

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
    public void SetActive(boolean active){}

    @Override
    public void SetOffset(float x, float y, float z){}
}
