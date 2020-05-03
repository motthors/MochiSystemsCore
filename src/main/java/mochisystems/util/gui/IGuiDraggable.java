package mochisystems.util.gui;

public interface IGuiDraggable extends IGuiElement {

    int GetStartPosX();
    void SetStartPosX(int x);
    void DragReleased();
}
