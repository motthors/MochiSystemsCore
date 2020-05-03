package mochisystems.util.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class GuiFormatedTextField extends GuiTextField implements IGuiElement {

	private Predicate<String> formatPredicate;
	private Consumer<String> confirmed;
	private Supplier<String> updateText;
	private int BackgroundColor;
	public int id;

	public static final String regexNumber = " *[+-]?(?:\\d+\\.?\\d*|\\.\\d+)";
	public static final String regexInteger = " *[+-]?(?:\\d+)";
	public static final String regexNaturalNum = " *\\d+";

	public GuiFormatedTextField(FontRenderer renderer, int x, int y, int width, int height, int color, int maxLen,
								Supplier<String> updateText,
								Predicate<String> formatPredicate,
								Consumer<String> confirmed) {
		super(renderer, x, y, width, height);
		this.formatPredicate = formatPredicate;
		this.confirmed = confirmed;
		this.updateText = updateText;
		BackgroundColor = 0xFF000000;
		setTextColor(color);
		setDisabledTextColour(color/2);
		setEnableBackgroundDrawing(false);
		setMaxStringLength(maxLen);
		setText(updateText.get());
	}
	
	@Override
	public boolean textboxKeyTyped(char c, int keycode)
	{
		if(!isFocused()) return false;
		if(keycode == Keyboard.KEY_RETURN)
		{
			CheckFormat();
			return true;
		}
		setTextColor(0xffff00);
		return super.textboxKeyTyped(c, keycode);
	}
	
	public void setFocused(boolean flag)
    {
        if (!flag && this.isFocused())
        {
			CheckFormat();
        }
        super.setFocused(flag);
    }

    private void CheckFormat()
	{
		if(formatPredicate.test(getText()))
		{
			setTextColor(0xffffff);
			confirmed.accept(getText());
		}
		else
		{
			setTextColor(0xff0000);
		}
	}

	public void UpdateText()
	{
		if(!isFocused())
		{
			setText(updateText.get());
		}
	}

	public void changeColor(int color)
	{
		BackgroundColor = color;
	}

	public void SetId(int id)
	{
		this.id = id;
	}
	public int GetId()
	{
		return id;
	}
	public void SetPosition(int x, int y)
	{
		xPosition = x;
		yPosition = y;
	}
	public void SetPositionY(int y)
	{
		yPosition = y;
	}
	public int GetPositionX()
	{
		return xPosition;
	}
	public int GetPositionY()
	{
		return yPosition;
	}
	public int GetWidth(){ return width; }
	public int GetHeight(){return height;}
	public void Draw(int mouseX, int mouseY)
	{
		if(yPosition > 0) drawTextBox();
	}
	public void MouseClicked(int x, int y, int buttonId)
	{
		mouseClicked(x, y, buttonId);
	}
	public void Clicked() {}
	@Override
	public void ClickReleased(){}
}
