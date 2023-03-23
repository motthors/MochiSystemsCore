package jp.mochisystems.core._mc.gui;

import jp.mochisystems.core.util.gui.IGuiElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiToggleButton extends GuiButtonExt implements IGuiElement {

    private boolean isOn;
    private String OffString;
    private String OnString;
    private Consumer<Boolean> action;
    private Supplier<Boolean> supplier;

    public GuiToggleButton(int id, int xPos, int yPos, int width, int height,
                           String OffString, String OnString,
                           Supplier<Boolean> supplier, Consumer<Boolean> action) {
        super(id, xPos, yPos, width, height, supplier.get() ? OnString : OffString);
        isOn = supplier.get();
        this.OffString = OffString;
        this.OnString = OnString;
        this.action = action;
        this.supplier = supplier;
    }

    public void Clicked()
    {
        Toggle();
        action.accept(isOn);
    }

    private void Toggle()
    {
        isOn = !isOn;
        displayString = isOn ? OnString : OffString;
    }

    public boolean GetToggle()
    {
        return isOn;
    }

    public void SetState(boolean state)
    {
        isOn = state;
        displayString = isOn ? OnString : OffString;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getHoverState(this.hovered);

            if(isOn) {
                GL11.glPushMatrix();
                GL11.glTranslated(x + width, y + height, 0);
                GL11.glRotated(180, 0, 0, 1);
                GL11.glTranslated(-x, -y, 0);
            }
            GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            if(isOn) {
                GL11.glPopMatrix();
            }

            this.mouseDragged(mc, mouseX, mouseY);
            int color = 0xE0E0E0;

            if (packedFGColour != 0)
            {
                color = packedFGColour;
            }
            else if (!this.enabled)
            {
                color = 0xA0A0A0;
            }
            else if (this.hovered)
            {
                color = 0xFFFFA0;
            }
            if(isOn) color &= 0x50FFA0;

            String buttonText = this.displayString;
            int strWidth = mc.fontRenderer.getStringWidth(buttonText);
            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");

            if (strWidth > width - 6 && strWidth > ellipsisWidth)
                buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";

            this.drawCenteredString(mc.fontRenderer, buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
        }
    }

    @Override
    public void Update()
    {
        if(isOn != supplier.get())
        {
            SetState(supplier.get());
        }
    }

    @Override
    public void SetId(int id)
    {
        this.id = id;
    }
    @Override
    public int GetId()
    {
        return id;
    }
    @Override
    public void SetPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    @Override
    public int GetPositionX()
    {
        return x;
    }
    @Override
    public int GetPositionY()
    {
        return y;
    }
    @Override
    public int GetWidth(){ return width; }
    @Override
    public int GetHeight(){return height;}
    @Override
    public void SetPositionY(int y)
    {
        this.y = y;
    }
    @Override
    public void Draw(int mouseX, int mouseY)
    {
        drawButton(Minecraft.getMinecraft(), mouseX, mouseY, 0);
    }
    @Override
    public void ClickReleased(){}

}
