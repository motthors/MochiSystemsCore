package jp.mochisystems.core.util.gui;

import jp.mochisystems.core._mc.gui.GuiToggleButton;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.NbtParamsShadow;
import net.minecraft.client.gui.FontRenderer;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiUtil {

//    public static GuiButton addButton1(GuiGroupCanvas Canvas, int group, int lenx, int leny, int posx, int posy, String str, int flag, Runnable action)
//    {
//        return addButton1(Canvas, group, lenx, leny, posx, posy, str, flag, false, false, "", action);
//    }
//    @SuppressWarnings("unchecked")
//    public static GuiButton addButton1(GuiGroupCanvas Canvas, int group, int lenx, int leny, int posx, int posy, String str,
//                                       int flag, boolean isToggle, boolean defToggle, String onStr, Runnable action)
//    {
//        int id = Canvas.nextId();
//        GuiButton button = isToggle ?
//                new GuiToggleButton(id, posx, posy, lenx, leny, str, onStr, defToggle) :
//                new GuiButtonWrapper(id, posx, posy, lenx, leny, str, action);
//        Canvas.Register(group, flag, -999, (IGuiElement) button);
//        return button;s
//    }
    @SuppressWarnings("unchecked")
    public static void addButton2(GuiGroupCanvas Canvas, int group, int posx, int posy,
                                 Runnable action1, Runnable action2)
    {
        GuiButtonWrapper button1 = new GuiButtonWrapper(0, posx, posy, 12, 12, "-", action1);
        GuiButtonWrapper button2 = new GuiButtonWrapper(0, posx+34, posy, 12, 12, "+", action2);
        Canvas.Register(group, button1);
        Canvas.Register(group, button2);
    }

    @SuppressWarnings("unchecked")
    public static void addButton4(GuiGroupCanvas Canvas, int group, int posx, int posy,
                                 Runnable action1, Runnable action2, Runnable action3, Runnable action4)
    {
        GuiButtonWrapper button1 = new GuiButtonWrapper(0, posx ,   posy, 14, 10, "", action1);
        GuiButtonWrapper button2 = new GuiButtonWrapper(0, posx+19, posy, 12, 10, "", action2);
        GuiButtonWrapper button3 = new GuiButtonWrapper(0, posx+34, posy, 12, 10, "", action3);
        GuiButtonWrapper button4 = new GuiButtonWrapper(0, posx+49, posy, 14, 10, "", action4);
        Canvas.Register(group, button1);
        Canvas.Register(group, button2);
        Canvas.Register(group, button3);
        Canvas.Register(group, button4);
    }
//    public static int addButton6(GuiGroupCanvas Canvas, int group, int posx, int posy, String label, int flag,
//                                 Runnable action1, Runnable action2, Runnable action3, Runnable action4, Runnable action5, Runnable action6)
//    {
//        return addButton6(Canvas, group, posx, posy, label, -53, 2, flag, action1, action2, action3, action4, action5, action6);
//    }
    @SuppressWarnings("unchecked")
    public static int addButton6(GuiGroupCanvas Canvas, int group, int posx, int posy,
                                 Runnable action1, Runnable action2, Runnable action3, Runnable action4, Runnable action5, Runnable action6)
    {
        int id = 0;
        GuiButtonWrapper button1 = new GuiButtonWrapper(0, posx-41, posy, 14, 10, "", action1);
        GuiButtonWrapper button2 = new GuiButtonWrapper(0, posx-26, posy, 12, 10, "", action2);
        GuiButtonWrapper button3 = new GuiButtonWrapper(0, posx-13, posy, 10, 10, "", action3);
        GuiButtonWrapper button4 = new GuiButtonWrapper(0, posx+00, posy, 10, 10, "", action4);
        GuiButtonWrapper button5 = new GuiButtonWrapper(0, posx+11, posy, 12, 10, "", action5);
        GuiButtonWrapper button6 = new GuiButtonWrapper(0, posx+24, posy, 14, 10, "", action6);
        Canvas.Register(group, button1);
        Canvas.Register(group, button2);
        Canvas.Register(group, button3);
        Canvas.Register(group, button4);
        Canvas.Register(group, button5);
        Canvas.Register(group, button6);
        return id;
    }

    @SuppressWarnings("unchecked")
    public static void addCheckButton(GuiGroupCanvas Canvas, FontRenderer font, int group, int posx, int posy, Supplier<Boolean> bool, String Description, Consumer<Boolean> onToggle)
    {
        GuiToggleButton button = new GuiToggleButton(0, posx, posy, 18, 18, "", "✓", bool, onToggle);
        Canvas.Register(group, button);
        Canvas.Register(group, new GuiLabel(Description, font, posx - 54, posy - 10, 0xffffff));
    }


    public static void AddInspector(
            GuiGroupCanvas canvas, FontRenderer font,
            String Label,
            int group, int posX, int posY, boolean flat,
            Supplier<Float> GetParam,
            Consumer<Float> SetParam, float updateRatio,
            Runnable OnConfirm)
    {
        IGuiElement label = new GuiDragChangerLabel(Label, font, posX, posY, 0xffffff,
                d -> SetParam.accept(GetParam.get() + d * updateRatio),
                OnConfirm);

        GuiFormattedTextField param = new GuiFormattedTextField(
            0, font, flat?posX+26:posX+5, flat?posY:posY+9, 45, 9, 0xffffff, 8,
                () -> String.format("%8.2f", GetParam.get()),
                s -> s.matches(GuiFormattedTextField.regexNumber),
                v -> {
                    float p = Float.parseFloat(v);
                    SetParam.accept(p);
                    OnConfirm.run();
                }
        );

        canvas.Register(group, label);
        canvas.Register(group, param);
    }

    public static void Vec3(
            String label,
            NbtParamsShadow.Class<Vec3d> shadow,
            GuiGroupCanvas canvas, FontRenderer font,
            int offsetX, int offsetY,
            int groupId,
            float updateRatio,
            Runnable OnConfirm
    )
    {
        Supplier<Float> updateTextX = () -> (float)shadow.Get().x;
        Supplier<Float> updateTextY = () -> (float)shadow.Get().y;
        Supplier<Float> updateTextZ = () -> (float)shadow.Get().z;
        Consumer<Float> updateX = v -> { shadow.Get().x = v; };
        Consumer<Float> updateY = v -> { shadow.Get().y = v; };
        Consumer<Float> updateZ = v -> { shadow.Get().z = v; };

        canvas.Register(groupId, new GuiLabel(label, font, offsetX, offsetY, 0xffffff));
        AddInspector(canvas, font, "X", groupId, offsetX, offsetY+10, true, updateTextX, updateX, updateRatio, OnConfirm);
        AddInspector(canvas, font, "Y", groupId, offsetX, offsetY+20, true, updateTextY, updateY, updateRatio, OnConfirm);
        AddInspector(canvas, font, "Z", groupId, offsetX, offsetY+30, true, updateTextZ, updateZ, updateRatio, OnConfirm);
    }

    public static void Quaternion(
        String label,
        NbtParamsShadow.Class<Quaternion> shadow,
        GuiGroupCanvas canvas, FontRenderer font,
        int offsetX, int offsetY,
        int groupId,
        float updateRatio,
        Runnable OnConfirm
    )
    {
        Vec3d tiltEuler = shadow.Get().Euler();
        Runnable EulerToQ = () -> {
            shadow.Get().Make(Vec3d.Front, tiltEuler.z)
                    .mul(new Quaternion().Make(Vec3d.Left, tiltEuler.x))
                    .mul(new Quaternion().Make(Vec3d.Up, tiltEuler.y))
                    .normalized();
        };
        Supplier<Float> updateTextX = () -> (float)Math.toDegrees(tiltEuler.x);
        Supplier<Float> updateTextY = () -> (float)Math.toDegrees(tiltEuler.y);
        Supplier<Float> updateTextZ = () -> (float)Math.toDegrees(tiltEuler.z);
        Consumer<Float> updateX = v -> { tiltEuler.x = Math.toRadians(v); EulerToQ.run(); };
        Consumer<Float> updateY = v -> { tiltEuler.y = Math.toRadians(v); EulerToQ.run(); };
        Consumer<Float> updateZ = v -> { tiltEuler.z = Math.toRadians(v); EulerToQ.run(); };
        canvas.Register(groupId, new GuiLabel(label, font, offsetX+2, offsetY, 0xffffff));
        AddInspector(canvas, font, "X", groupId, offsetX+2, offsetY+10, true, updateTextX, updateX, updateRatio, OnConfirm);
        AddInspector(canvas, font, "Y", groupId, offsetX+2, offsetY+20, true, updateTextY, updateY, updateRatio, OnConfirm);
        AddInspector(canvas, font, "Z", groupId, offsetX+2, offsetY+30, true, updateTextZ, updateZ, updateRatio, OnConfirm);
    }


}
