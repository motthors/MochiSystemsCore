package jp.mochisystems.core.util.gui;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core.hook.OrientCameraHooker;
import jp.mochisystems.core.math.Vec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public abstract class GuiDragController {

    private static float cameraInterporate;
    private boolean enableDrag;
    private static float cameraDistance, cameraDistanceTemp;
    private static double tempX = 0, tempY = 0, tempZ = 0;
    private static float rotationYawTemp, rotationPitchTemp;
    private static float defRotYaw, defRotPitch;
    private final Vec3d vecForCamera = new Vec3d();
    private OrientCameraHooker.CameraHooker CameraPreHook = this::CameraPreProc;
    private OrientCameraHooker.CameraHooker CameraPostHook = this::CameraPostProc;
    private EntityPlayer player;

    public float CurrentCameraDistance(){return cameraDistanceTemp;}

    public abstract void RegisterCamPosFromCore(Vec3d dest, float tick);

    public void Init()
    {
        player = Minecraft.getMinecraft().player;
        defRotYaw = rotationYawTemp = player.rotationYaw;
        defRotPitch = rotationPitchTemp = player.rotationPitch;
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
        cameraDistanceTemp = 4;//ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, new String[]{"thirdPersonDistance", "field_78490_B"});
    }
    private void CameraPreProc(float tick)
    {
        cameraDistanceTemp += (cameraDistance - cameraDistanceTemp) * 0.1f;
        GL11.glTranslatef(0, 0, -cameraDistanceTemp);

        player.rotationYaw = (rotationYawTemp-player.rotationYaw)*0.3f + player.rotationYaw;
        player.rotationPitch = (rotationPitchTemp-player.rotationPitch)*0.3f + player.rotationPitch;
    }
    private void CameraPostProc(float tick)
    {
        RegisterCamPosFromCore(vecForCamera, tick);
        vecForCamera.x +=  - Minecraft.getMinecraft().player.posX;
        vecForCamera.y +=  - Minecraft.getMinecraft().player.posY - player.eyeHeight;
        vecForCamera.z +=  - Minecraft.getMinecraft().player.posZ;
        tempX += (vecForCamera.x - tempX) * (1f-cameraInterporate);
        tempY += (vecForCamera.y - tempY) * (1f-cameraInterporate);
        tempZ += (vecForCamera.z - tempZ) * (1f-cameraInterporate);
        cameraInterporate -= 0.015;
        cameraInterporate = Math.max(cameraInterporate, 0.0f);
//        tempY -= cameraDistanceTemp*0.2f;
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
                Minecraft.getMinecraft().player.rotationYaw -= 360f;
            }
            else if(rotationYawTemp < -180f)
            {
                rotationYawTemp += 360f;
                Minecraft.getMinecraft().player.rotationYaw += 360f;
            }
            rotationPitchTemp -= dy * 1.3f;
            rotationPitchTemp = Math.min(Math.max(-90f, rotationPitchTemp), 90f);
        }
    }

    public void EndDragCamera()
    {
        OrientCameraHooker.RemoveHookFirst(CameraPreHook);
        OrientCameraHooker.RemoveHookLast(CameraPostHook);
        player.rotationYaw = defRotYaw;
        player.rotationPitch = defRotPitch;
    }
}
