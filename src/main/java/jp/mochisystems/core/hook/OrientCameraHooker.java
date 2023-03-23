package jp.mochisystems.core.hook;

import jp.mochisystems.core._mc._core.Logger;

import java.util.ArrayList;
import java.util.function.Consumer;

public class OrientCameraHooker {

    public interface CameraHooker extends Consumer<Float>{
    }

    private static ArrayList<CameraHooker> hooksFirst = new ArrayList<>();
    private static ArrayList<CameraHooker> hooksLast = new ArrayList<>();

    public static void AddHookFirst(CameraHooker cameraHooker)
    {
        hooksFirst.add(cameraHooker);
    }
    public static void AddHookLast(CameraHooker cameraHooker)
    {
        hooksLast.add(cameraHooker);
    }

    public static void HandleFirst(float pertialTick)
    {
        for(CameraHooker hook : hooksFirst) hook.accept(pertialTick);
    }
    public static void HandleLast(float pertialTick)
    {
        for(CameraHooker hook : hooksLast) hook.accept(pertialTick);
    }

    public static void RemoveHookFirst(CameraHooker cameraHooker)
    {
        hooksFirst.remove(cameraHooker);
//        hooksFirst.clear();
    }
    public static void RemoveHookLast(CameraHooker cameraHooker)
    {
        hooksLast.remove(cameraHooker);
//        hooksLast.clear();
    }
}
