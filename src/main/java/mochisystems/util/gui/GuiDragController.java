package mochisystems.util.gui;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import mochisystems.hook.OrientCameraHooker;
import mochisystems.math.Vec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public abstract class GuiDragController {

    private static float cameraInterporate;
    private boolean enableDrag;
    private static float cameraDistance, cameraDistanceTemp;
    private static double tempX = 0, tempY = 0, tempZ = 0;
    private static float rotationYawTemp, rotationPitchTemp;
    private final Vec3d vecForCamera = new Vec3d();
    private OrientCameraHooker.CameraHooker CameraPreHook = this::CameraPreProc;
    private OrientCameraHooker.CameraHooker CameraPostHook = this::CameraPostProc;
    int originalX, originalY, originalZ;

    public float CurrentCameraDistance(){return cameraDistanceTemp;}

    public abstract void RegisterCamPosFromCore(Vec3d dest, float tick);

    public void Init(int originalX, int originalY, int originalZ)
    {
        this.originalX = originalX;
        this.originalY = originalY;
        this.originalZ = originalZ;
        rotationYawTemp = Minecraft.getMinecraft().thePlayer.rotationYaw;
        rotationPitchTemp = Minecraft.getMinecraft().thePlayer.rotationPitch;
        cameraInterporate = 1;
//        for (Connector c : part.connectors) ConnectorNameList.add(c.GetName());
        OrientCameraHooker.AddHookFirst(CameraPreHook);
        OrientCameraHooker.AddHookLast(CameraPostHook);
    }

    public static void ResetSpecialCamera()
    {
        tempX = 0;
        tempY = 0;
        tempZ = 0;
        cameraDistance = 10;
        cameraDistanceTemp = ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, new String[]{"thirdPersonDistance", "field_78490_B"});
    }
    private void CameraPreProc(float tick)
    {
        cameraDistanceTemp += (cameraDistance - cameraDistanceTemp) * 0.1f;
        GL11.glTranslatef(0, 0, -cameraDistanceTemp);

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        player.rotationYaw = (rotationYawTemp-player.rotationYaw)*0.3f + player.rotationYaw;
        player.rotationPitch = (rotationPitchTemp-player.rotationPitch)*0.3f + player.rotationPitch;
    }
    private void CameraPostProc(float tick)
    {
        RegisterCamPosFromCore(vecForCamera, tick);
        vecForCamera.x +=  - Minecraft.getMinecraft().thePlayer.posX + originalX + 0.5;
        vecForCamera.y +=  - Minecraft.getMinecraft().thePlayer.posY + originalY + 0.5;
        vecForCamera.z +=  - Minecraft.getMinecraft().thePlayer.posZ + originalZ + 0.5;
        tempX += (vecForCamera.x - tempX) * (1f-cameraInterporate);
        tempY += (vecForCamera.y - tempY) * (1f-cameraInterporate);
        tempZ += (vecForCamera.z - tempZ) * (1f-cameraInterporate);
        cameraInterporate -= 0.015;
        cameraInterporate = Math.max(cameraInterporate, 0.0f);
        GL11.glTranslated(-tempX, -tempY, -tempZ);
    }

    public void handleMouseWheel(int dWheel)
    {
        ChangeDistance(dWheel * 0.01f);
    }

    private void ChangeDistance(float dDistance)
    {
        cameraDistance -= dDistance;
        if(cameraDistance < 1) cameraDistance = 1;
        if(cameraDistance > 100) cameraDistance = 100;
    }

    public void StartDrag()
    {
        enableDrag = true;
    }

    public void StopMove()
    {
        enableDrag = false;
    }

    public void Drag(int dx, int dy)
    {
        if(enableDrag) {
            rotationYawTemp += dx * 1.3f;
            if(rotationYawTemp >  180f)
            {
                rotationYawTemp -= 360f;
                Minecraft.getMinecraft().thePlayer.rotationYaw -= 360f;
            }
            else if(rotationYawTemp < -180f)
            {
                rotationYawTemp += 360f;
                Minecraft.getMinecraft().thePlayer.rotationYaw += 360f;
            }
            rotationPitchTemp -= dy * 1.3f;
            rotationPitchTemp = Math.min(Math.max(-90f, rotationPitchTemp), 90f);
        }
    }

    public void EndDragCamera()
    {
        OrientCameraHooker.RemoveHookFirst(CameraPreHook);
        OrientCameraHooker.RemoveHookLast(CameraPostHook);
    }
}
