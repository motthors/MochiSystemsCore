package mochisystems.util.gui;

import mochisystems._mc.gui.GuiToggleButton;
import net.minecraft.client.gui.FontRenderer;

import java.util.function.Consumer;

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
    public static void addButton2(GuiGroupCanvas Canvas, int group, int posx, int posy, String label, int flag,
                                 Runnable action1, Runnable action2)
    {
        GuiButtonWrapper button1 = new GuiButtonWrapper(0, posx, posy, 12, 12, "-", action1);
        GuiButtonWrapper button2 = new GuiButtonWrapper(0, posx+32, posy, 12, 12, "+", action2);
        Canvas.Register(group, button1);
        Canvas.Register(group, button2);
    }

    @SuppressWarnings("unchecked")
    public static void addButton4(GuiGroupCanvas Canvas, int group, int posx, int posy, String label, int flag,
                                 Runnable action1, Runnable action2, Runnable action3, Runnable action4)
    {
        int offsetx = -19;
        int offsety = 2;
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
    public static void addCheckButton(GuiGroupCanvas Canvas, FontRenderer font, int group, int posx, int posy, Boolean bool, String Description, Consumer<Boolean> onToggle)
    {
        GuiToggleButton button = new GuiToggleButton(0, posx, posy, 18, 18, "", "", bool, onToggle);
        Canvas.Register(group, button);
        Canvas.Register(group, new GuiLabel(Description, font, posx - 54, posy - 10, 0xffffff));
    }
}
