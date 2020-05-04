package mochisystems.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mochisystems.handler.TickEventHandler;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelBiped extends net.minecraft.client.model.ModelBiped {

    private IModel modelHead;
    private IModel modelBody;
    private IModel modelArmLeft;
    private IModel modelArmRight;
    private IModel modelLegLeft;
    private IModel modelLegRight;


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
                        if(children[i].GetName().toUpperCase().equals("L")){
                            modelArmLeft = children[i];
                            modelArmLeft.SetOffset(0, 0, 0);
                            children[i] = null;
                        }
                        else if(children[i].GetName().toUpperCase().equals("R")){
                            modelArmRight = children[i];
                            modelArmRight.SetOffset(0, 0, 0);
                            children[i] = null;}
                    }
                    modelBody = model;
                }
                else if(model.GetName().toUpperCase().equals("L")) {
                    model.SetOffset(0, 0, 0);
                    modelArmLeft = model;
                }
                else if(model.GetName().toUpperCase().equals("R")) {
                    model.SetOffset(0, 0, 0);
                    modelArmRight = model;
                }
                else modelBody = model;
                break;
            case 2:
                if(model.HasChild())
                {
                    IModel[] children = model.GetChildren();
                    for(int i = 0; i < children.length; ++i)
                    {
                        if(children[i].GetName().toUpperCase().equals("L")){ modelLegLeft = children[i]; children[i] = null;}
                        else if(children[i].GetName().toUpperCase().equals("R")){ modelLegRight = children[i]; children[i] = null;}
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
