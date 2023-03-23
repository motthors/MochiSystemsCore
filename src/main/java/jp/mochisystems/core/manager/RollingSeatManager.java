package jp.mochisystems.core.manager;

import jp.mochisystems.core.hook.OrientCameraHooker;
import jp.mochisystems.core.math.Mat4;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Quaternion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.nio.DoubleBuffer;

public class RollingSeatManager {

    private static final Quaternion seatRotation = new Quaternion();
    private static final Quaternion rotation = new Quaternion();
    private static final Quaternion prevRotation = new Quaternion();
    private static final Quaternion fixRotation = new Quaternion();
    private static final Quaternion.MatBuffer fixBuf = new Quaternion.MatBuffer();

    public static void Init()
    {
        OrientCameraHooker.AddHookFirst(RollingSeatManager::CameraProcPre);
        OrientCameraHooker.AddHookLast(RollingSeatManager::CameraProc);
    }

    public static void ResetAngles()
    {
        seatRotation.Identity();
        rotation.Identity();
        prevRotation.Identity();
    }

    public static void SetAttitude(Quaternion current, Quaternion prev, Quaternion seatRot)
    {
        rotation.CopyFrom(current);
        prevRotation.CopyFrom(prev);
        seatRotation.CopyFrom(seatRot).Inverse();
    }

    @SideOnly(Side.CLIENT)
    public static void CameraProcPre(float f)
    {
//        GL11.glRotated(Math.Lerp(f, prevRotationViewYaw, rotationViewYaw), 0.0, 1.0, 0.0);
//        GL11.glRotated(Math.Lerp(f, prevRotationViewPitch, rotationViewPitch), -1.0, 0.0, 0.0);
    }

    @SideOnly(Side.CLIENT)
    public static void CameraProc(float f)
    {
        GL11.glMultMatrix(fixBuf.Fix(seatRotation));

//        GL11.glRotatef(45, 0.0F, 0.0F, 1.0F);//roll
//    	GL11.glRotatef(20, 1.0F, 0.0F, 0.0F);//pitch
//    	GL11.glRotatef(45, 0.0F, 1.0F, 0.0F);//yaw

        Quaternion.Lerp(fixRotation, prevRotation, rotation, f);
        fixRotation.Inverse();
        GL11.glMultMatrix(fixBuf.Fix(fixRotation));

    }

}
