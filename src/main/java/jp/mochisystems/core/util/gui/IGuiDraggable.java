package jp.mochisystems.core.util.gui;

public interface IGuiDraggable extends IGuiElement {

    int GetStartPos();
    void SetStartPos(int x);
    void Dragged(int dx, int dy);
    void DragReleased();
}
