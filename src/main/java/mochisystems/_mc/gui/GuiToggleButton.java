package mochisystems._mc.gui;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiUtils;
import mochisystems.util.gui.IGuiElement;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

public class GuiToggleButton extends GuiButtonExt implements IGuiElement {

    private boolean isOn;
    private String OffString;
    private String OnString;
    private Consumer<Boolean> action;

    public GuiToggleButton(int id, int xPos, int yPos, int width, int height, String OffString, String OnString, boolean defToggle, Consumer<Boolean> action) {
        super(id, xPos, yPos, width, height, defToggle ? OnString : OffString);
        isOn = defToggle;
        this.OffString = OffString;
        this.OnString = OnString;
        this.action = action;
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
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);

            if(isOn) {
                GL11.glPushMatrix();
                GL11.glTranslated(xPosition + width, yPosition + height, 0);
                GL11.glRotated(180, 0, 0, 1);
                GL11.glTranslated(-xPosition, -yPosition, 0);
            }
            GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition, this.yPosition, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            if(isOn) {
                GL11.glPopMatrix();
            }

            this.mouseDragged(mc, mouseX, mouseY);
            int color = 14737632;

            if (packedFGColour != 0)
            {
                color = packedFGColour;
            }
            else if (!this.enabled)
            {
                color = 10526880;
            }
            else if (this.field_146123_n)
            {
                color = 16777120;
            }

            String buttonText = this.displayString;
            int strWidth = mc.fontRenderer.getStringWidth(buttonText);
            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");

            if (strWidth > width - 6 && strWidth > ellipsisWidth)
                buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";

            this.drawCenteredString(mc.fontRenderer, buttonText, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, color);
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
        xPosition = x;
        yPosition = y;
    }
    @Override
    public int GetPositionX()
    {
        return xPosition;
    }
    @Override
    public int GetPositionY()
    {
        return yPosition;
    }
    @Override
    public int GetWidth(){ return width; }
    @Override
    public int GetHeight(){return height;}
    @Override
    public void SetPositionY(int y)
    {
        yPosition = y;
    }
    @Override
    public void MouseClicked(int x, int y, int buttonId){}
    @Override
    public void Draw(int mouseX, int mouseY)
    {
        drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
    }
    @Override
    public void ClickReleased(){}

}
