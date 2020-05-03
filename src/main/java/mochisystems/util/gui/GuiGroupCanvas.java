package mochisystems.util.gui;

import mochisystems.math.Vec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.List;

public class GuiGroupCanvas {

    private int id;

    public class Group {
        List<IGuiElement> elements = new ArrayList<>();
        private List<GuiButton> buttonList = new ArrayList<>();
        private List<GuiFormatedTextField> textFieldList = new ArrayList<>();
        private List<IGuiDraggable> draggableList = new ArrayList<>();
        public boolean active;
        public int offsetX, offsetY;
        public Vec3d scale;
    }

    private Minecraft mc;
    private GuiButton selectedButton;
    private IGuiDraggable dragged;
    private int dragStartPosX, dragStartPosY;
    private Map<Integer, IGuiElement> DataByButtonId = new HashMap<>();
    private Map<Integer, Integer> MapButtonIdToGroup = new HashMap<>();
    private Map<Integer, Group> GroupTree = new HashMap<>();

    public IGuiDraggable GetDraggingElement()
    {
        return dragged;
    }

    public void Init()
    {
        this.mc = Minecraft.getMinecraft();
        id = 0;
        DataByButtonId.clear();
        MapButtonIdToGroup.clear();
        GroupTree.clear();
        dragged = null;
        selectedButton = null;
    }
    
    public void ActiveGroup(int groupId)
    {
        if(!GroupTree.containsKey(groupId)) return;
        GroupTree.get(groupId).active = true;
    }
    public void DisableGroup(int groupId)
    {
        if(!GroupTree.containsKey(groupId)) return;
        GroupTree.get(groupId).active = false;
    }

    public void MoveGroup(int groupId, int x, int y, boolean relative)
    {
        if (!GroupTree.containsKey(groupId)) GroupTree.put(groupId, new Group());
        Group g = GroupTree.get(groupId);
        if (relative) {
            g.offsetX += x;
            g.offsetY += y;

        } else {
            g.offsetX = x;
            g.offsetY = y;
        }
    }

    public Set<Integer> GetGroupIdSet()
    {
        return GroupTree.keySet();
    }
    public Group GetGroupInfo(int groupId)
    {
        return GroupTree.get(groupId);
    }


//    public int GetBaseIdFromButtonId(int buttonId){ return DataByButtonId.get(buttonId).baseID; }
//    public int GetFlagFromButtonId(int buttonId){ return DataByButtonId.get(buttonId).flag; }
    public boolean IsDefaultGroup(int buttonId){ return GroupIdFromButtonId(buttonId) == -1; }
    public int GroupIdFromButtonId(int buttonId){ return MapButtonIdToGroup.get(buttonId); }

    public IGuiElement Register(int groupId, IGuiElement element)
    {
        Group group;
        if(!GroupTree.containsKey(groupId))
        {
            group = new Group();
            GroupTree.put(groupId, group);
            if(groupId < 0) group.active = true;
        }
        else group = GroupTree.get(groupId);
        group.elements.add(element);
        MapButtonIdToGroup.put(id, groupId);
        element.SetId(id);
        if(element instanceof GuiFormatedTextField)
        {
            group.textFieldList.add((GuiFormatedTextField) element);
        }
        else if(element instanceof GuiButton)
        {
            DataByButtonId.put(id, element);
            group.buttonList.add((GuiButton)element);
        }

        if(element instanceof IGuiDraggable)
        {
            group.draggableList.add((IGuiDraggable) element);
        }
        id++;
        return element;
    }

    public void DeleteElement(int groupId, IGuiElement element)
    {
        if(!GroupTree.containsKey(groupId))return;
        Group group = GroupTree.get(groupId);
        group.elements.remove(element);
        if(element instanceof GuiFormatedTextField) group.textFieldList.remove(element);
        if(element instanceof GuiButton) group.buttonList.remove(element);
        if(element instanceof IGuiDraggable) group.draggableList.remove(element);
    }

    public void Update()
    {
        for(int key : GroupTree.keySet())
        {
            Group g = GroupTree.get(key);
            if(!g.active)continue;
            for(GuiFormatedTextField text : g.textFieldList)
            {
                text.UpdateText();
            }
        }
    }

    public void DrawContents(int mousex, int mousey)
    {
        for(int key : GroupTree.keySet())
        {
            Group g = GroupTree.get(key);
            GL11.glTranslated(g.offsetX, g.offsetY, 0);
            if(g.active)
            for(IGuiElement e : g.elements)
            {
                e.Draw(mousex-g.offsetX, mousey-g.offsetY);
            }
            GL11.glTranslated(-g.offsetX, -g.offsetY, 0);
        }
    }

    public void MouseClicked(int x, int y, int buttonId)
    {
        for(int key : GroupTree.keySet()) {
            Group g = GroupTree.get(key);
            if(!g.active)continue;
            int _x = x - g.offsetX;
            int _y = y - g.offsetY;
            for (GuiFormatedTextField text : g.textFieldList) {
                text.mouseClicked(_x, _y, buttonId);
            }

            for (int l = 0; l < g.buttonList.size(); ++l) {
                GuiButton guibutton = g.buttonList.get(l);
                if (guibutton.mousePressed(this.mc, _x, _y)) {
                    this.selectedButton = guibutton;
                    selectedButton.func_146113_a(this.mc.getSoundHandler());
                    ((IGuiElement)selectedButton).Clicked();
                }
            }
            for (int l = 0; l < g.draggableList.size(); ++l) {
                IGuiDraggable drag = g.draggableList.get(l);
                if(drag.GetPositionX() <= _x && _x <= drag.GetPositionX()+drag.GetWidth()
                && drag.GetPositionY() <= _y && _y <= drag.GetPositionY()+drag.GetHeight()){
                    dragged = drag;
                    dragStartPosX = dragPrevX = x;
                    dragStartPosY = dragPrevY = y;
                    drag.SetStartPosX(drag.GetPositionX());
                }
            }
        }
    }

    public void mouseMoveOrUp(int x, int y, int buttonId)
    {
        if(buttonId == 0)
        {
            if (this.selectedButton != null)
            {
                this.selectedButton.mouseReleased(x, y);
                this.selectedButton = null;
                dragPrevX = dragPrevY = 0;
            }
            if (this.dragged != null)
            {
                if (dragStartPosX == x && dragStartPosY == y) {
                    dragged.ClickReleased();
                }
                else dragged.DragReleased();
                dragged = null;
            }
        }
    }

    private int dragPrevX, dragPrevY;
    public void mouseClickMove(int x, int y, int event, long time)
    {
        if(dragged != null)
        {
            int _x = x - dragPrevX;
            int _y = y - dragPrevY;
            dragged.SetPosition(dragged.GetPositionX()+_x, dragged.GetPositionY()+_y);
            dragPrevX = x;
            dragPrevY = y;
        }
    }

    public boolean KeyTyped(char c, int keyCode)
    {
        boolean ret = false;
        for(int key : GroupTree.keySet()) {
            Group g = GroupTree.get(key);
            for(GuiFormatedTextField text : g.textFieldList) {
                ret |= text.textboxKeyTyped(c, keyCode);
            }
        }
        return ret;
    }
}
