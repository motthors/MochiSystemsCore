package mochisystems.manager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mochisystems.hook.OrientCameraHooker;
import mochisystems.math.Mat4;
import mochisystems.math.Math;
import mochisystems.math.Quaternion;
import org.lwjgl.opengl.GL11;

public class RollingSeatManager {

    // プレイヤーが操作可能な自由視点の角度
    public static float rotationViewYaw = 0f;
    public static float prevRotationViewYaw = 0f;
    public static float rotationViewPitch = 0f;
    public static float prevRotationViewPitch = 0f;
    // 回転シートによる回転
    private static final Mat4 Attitude = new Mat4();
    private static final Quaternion rotation = new Quaternion();
    private static final Quaternion prevRotation = new Quaternion();
    private static final Quaternion fixRotation = new Quaternion();

    public static void Init()
    {
        OrientCameraHooker.AddHookLast(RollingSeatManager::CameraProc);
    }

    public static void ShakeHeadOnRollingSeat(float deltax, float deltay)
    {
        float f2 = rotationViewPitch;
        float f3 = rotationViewYaw;
        rotationViewYaw = (float)((double)rotationViewYaw + (double)deltax * 0.15D);
        rotationViewPitch = (float)((double)rotationViewPitch + (double)deltay * 0.15D);

        if (rotationViewPitch < -80.0F)rotationViewPitch = -80.0F;
        if (rotationViewPitch > 80.0F)rotationViewPitch = 80.0F;
        if (rotationViewYaw < -150.0F)rotationViewYaw = -150.0F;
        if (rotationViewYaw > 150.0F)rotationViewYaw = 150.0F;

        prevRotationViewPitch += rotationViewPitch - f2;
        prevRotationViewYaw += rotationViewYaw - f3;
    }

    public static void ResetAngles()
    {
        rotationViewYaw = 0f;
        prevRotationViewYaw = 0f;
        rotationViewPitch = 0f;
        prevRotationViewPitch = 0f;
        Attitude.Identifier();
        rotation.Identity();
        prevRotation.Identity();
    }

    public static void SetAttitude(Quaternion current, Quaternion prev)
    {
        rotation.CopyFrom(current);
        prevRotation.CopyFrom(prev);
    }

    @SideOnly(Side.CLIENT)
    public static void CameraProc(float f)
    {
//        GL11.glRotatef(45, 0.0F, 0.0F, 1.0F);//roll
//    	GL11.glRotatef(20, 1.0F, 0.0F, 0.0F);//pitch
//    	GL11.glRotatef(45, 0.0F, 1.0F, 0.0F);//yaw


//        GL11.glRotated(Math.Lerp(f, prevRotationViewPitch, rotationViewPitch), -1.0, 0.0, 0.0);
//        GL11.glRotated(Math.Lerp(f, prevRotationViewYaw, rotationViewYaw), 0.0, 1.0, 0.0);
        Quaternion.Lerp(fixRotation, prevRotation, rotation, f);
        fixRotation.Inverse();
        GL11.glMultMatrix(fixRotation.makeMatrixBuffer());
    }
}
