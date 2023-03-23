package jp.mochisystems.core.util.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class GuiFormattedTextField extends GuiTextField implements IGuiElement {

	private final Predicate<String> formatPredicate;
	private final Consumer<String> confirmed;
	private final Supplier<String> updateText;
	public int id;

	public static final String regexNumber = " *[+-]?(?:\\d+\\.?\\d*|\\.\\d+)";
	public static final String regexInteger = " *[+-]?(?:\\d+)";
	public static final String regexNaturalNum = " *\\d+";

	public GuiFormattedTextField(int id, FontRenderer renderer, int x, int y, int width, int height, int color, int maxLen,
								 Supplier<String> updateText,
								 Predicate<String> formatPredicate,
								 Consumer<String> confirmed) {
		super(id, renderer, x, y, width, height);
		this.formatPredicate = formatPredicate;
		this.confirmed = confirmed;
		this.updateText = updateText;
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
	
	public void setFocused(boolean isFocusedIn)
    {
		if(isFocusedIn && !this.isFocused())
		{
			setText(getText().replaceAll("\\s", ""));
		}
        if (!isFocusedIn && this.isFocused())
        {
			CheckFormat();
        }
        super.setFocused(isFocusedIn);
    }

    private void CheckFormat()
	{
		if(formatPredicate.test(getText()))
		{
			setTextColor(0xffffff);
			confirmed.accept(getText().replaceAll("\\s", ""));
		}
		else
		{
			setTextColor(0xff0000);
		}
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
		this.x = x;
		this.y = y;
	}
	public void SetPositionY(int y)
	{
		this.y = y;
	}
	public int GetPositionX()
	{
		return x;
	}
	public int GetPositionY()
	{
		return y;
	}
	public int GetWidth()
	{
		return width;
	}
	public int GetHeight(){return height;}
	public void Update(){
		if(!isFocused()) setText(updateText.get());
	}
	public void Clicked() {}
	@Override
	public void ClickReleased(){}
	public void Draw(int mouseX, int mouseY)
	{
		if(y > 0 && this.getVisible()) {
			drawTextBox();
		}
	}
}
