package jp.mochisystems.core.util;

import jp.mochisystems.core._mc.eventhandler.TickEventHandler;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelBiped extends net.minecraft.client.model.ModelBiped {

    private IModel modelHead;
    private IModel modelBody;
    public IModel modelArmLeft;
    public IModel modelArmRight;
    public IModel modelLegLeft;
    public IModel modelLegRight;

    public boolean isSeparateArmL;
    public boolean isSeparateArmR;
    public boolean isSeparateLegL;
    public boolean isSeparateLegR;

    public ModelBiped()
    {

    }

    public void SetModel(IModel model, int slot)
    {
        switch(slot)
        {
            case 0: modelHead = model; break;
            case 1:
                if(model.HasChild())
                {
                    IModel[] children = model.GetChildren();
                    for(int i = 0; i < children.length; ++i)
                    {
                        if(children[i] == null) continue;
                        if(children[i].GetName().toUpperCase().equals("L")){
                            modelArmLeft = children[i];
//                            children[i] = null;
                            isSeparateArmL = true;
                        }
                        else if(children[i].GetName().toUpperCase().equals("R")){
                            modelArmRight = children[i];
//                            children[i] = null;
                            isSeparateArmR = true;
                        }
                        else if(children[i].GetName().toUpperCase().equals("BODY")) {
                            modelBody = children[i];
                        }
                    }
                    if(modelArmLeft==null && modelArmRight==null) modelBody = model;
                    else model.SetLock(true);
                }
                else if(model.GetName().toUpperCase().equals("L")) {
                    modelArmLeft = model;
                }
                else if(model.GetName().toUpperCase().equals("R")) {
                    modelArmRight = model;
                }
                else modelBody = model;
                break;
            case 2:
            case 3:
                if(model.HasChild())
                {
                    model.SetLock(true);
                    IModel[] children = model.GetChildren();
                    for(int i = 0; i < children.length; ++i)
                    {
                        if(children[i] == null) continue;
                        if(children[i].GetName().toUpperCase().equals("L")){
                            modelLegLeft = children[i];
//                            children[i] = null;
                            isSeparateLegL = true;
                        }
                        else if(children[i].GetName().toUpperCase().equals("R")){
                            modelLegRight = children[i];
//                            children[i] = null;
                            isSeparateLegR = true;
                        }
                    }
                }
                else if(model.GetName().toUpperCase().equals("L")) modelLegLeft = model;
                else modelLegRight = model;
                break;
        }
    }

    public void Update()
    {
        if(modelHead!=null)modelHead.Update();
        if(modelBody!=null)modelBody.Update();
        if(modelArmLeft!=null)modelArmLeft.Update();
        if(modelArmRight!=null)modelArmRight.Update();
        if(modelLegLeft!=null)modelLegLeft.Update();
        if(modelLegRight!=null)modelLegRight.Update();
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float handleRotationFloat,
                       float rotYaw, float rotPitch, float offset)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, handleRotationFloat, rotYaw, rotPitch, offset, entity);
        float tick = TickEventHandler.getPartialTick();
        if(modelHead != null)
        {
            GL11.glPushMatrix();
            Rotate(bipedHead, offset);
            modelHead.RenderModel(0, tick);
            modelHead.RenderModel(1, tick);
            GL11.glPopMatrix();
        }
        if(modelBody != null)
        {
            GL11.glPushMatrix();
            Rotate(bipedBody, offset);
            modelBody.RenderModel(0, tick);
            modelBody.RenderModel(1, tick);
            GL11.glPopMatrix();
        }
        if(modelArmLeft != null)
        {
            GL11.glPushMatrix();
            Rotate(bipedLeftArm, offset);
            modelArmLeft.RenderModel(0, tick);
            modelArmLeft.RenderModel(1, tick);
            GL11.glPopMatrix();
        }
        if(modelArmRight != null)
        {
            GL11.glPushMatrix();
            Rotate(bipedRightArm, offset);
            modelArmRight.RenderModel(0, tick);
            modelArmRight.RenderModel(1, tick);
            GL11.glPopMatrix();
        }
        if(modelLegLeft != null)
        {
            GL11.glPushMatrix();
            Rotate(bipedLeftLeg, offset);
            modelLegLeft.RenderModel(0, tick);
            modelLegLeft.RenderModel(1, tick);
            GL11.glPopMatrix();
        }
        if(modelLegRight != null)
        {
            GL11.glPushMatrix();
            Rotate(bipedRightLeg, offset);
            modelLegRight.RenderModel(0, tick);
            modelLegRight.RenderModel(1, tick);
            GL11.glPopMatrix();
        }
    }

    public void Rotate(ModelRenderer renderer, float t)
    {
        GL11.glTranslatef(renderer.offsetX, renderer.offsetY, renderer.offsetZ);
        GL11.glTranslatef(renderer.rotationPointX * t, renderer.rotationPointY * t, renderer.rotationPointZ * t);

        if (renderer.rotateAngleZ != 0.0F)
        {
            GL11.glRotatef(renderer.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
        }

        if (renderer.rotateAngleY != 0.0F)
        {
            GL11.glRotatef(renderer.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
        }

        if (renderer.rotateAngleX != 0.0F)
        {
            GL11.glRotatef(renderer.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
        }

        GL11.glRotatef(180f, 0.0F, 0.0F, 1.0F);
    }

    public void Invalidate()
    {
        if(modelHead!=null)modelHead.Invalidate();
        if(modelBody!=null)modelBody.Invalidate();
        if(modelArmLeft!=null)modelArmLeft.Invalidate();
        if(modelArmRight!=null)modelArmRight.Invalidate();
        if(modelLegLeft!=null)modelLegLeft.Invalidate();
        if(modelLegRight!=null)modelLegRight.Invalidate();
        modelHead = null;
        modelBody = null;
        modelArmLeft = null;
        modelArmRight = null;
        modelLegLeft = null;
        modelLegRight = null;
    }
}
