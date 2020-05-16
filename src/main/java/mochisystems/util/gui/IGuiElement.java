package mochisystems.util.gui;

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
    void MouseClicked(int x, int y, int buttonId);
    void Clicked();
    void ClickReleased();
}
