package jp.mochisystems.core.util.gui;

public interface IGuiElement {
    void SetId(int id);
    int GetId();
    void SetPosition(int x, int y);
    void SetPositionY(int y);
    int GetPositionX();
    int GetPositionY();
    int GetWidth();
    int GetHeight();
    void Update();
    void Draw(int mouseX, int mouseY);
    void Clicked();
    void ClickReleased();
}
