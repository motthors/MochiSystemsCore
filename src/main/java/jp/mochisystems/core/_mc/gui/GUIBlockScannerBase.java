package jp.mochisystems.core._mc.gui;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.gui.container.ContainerBlockScanner;
import jp.mochisystems.core._mc.message.MessageChangeLimitLine;
import jp.mochisystems.core._mc.message.MessageSyncNbtCtS;
import jp.mochisystems.core._mc.message.PacketHandler;
import jp.mochisystems.core._mc.tileentity.TileEntityBlocksScannerBase;
import jp.mochisystems.core.blockcopier.LimitFrame;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.NbtParamsShadow;
import jp.mochisystems.core.util.gui.*;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import org.lwjgl.input.Keyboard;

public class GUIBlockScannerBase extends GUICanvasGroupControl {

    protected final TileEntityBlocksScannerBase tile;
    protected final LimitFrame frame;
    protected ContainerBlockScanner container;

    protected final NbtParamsShadow shadow;
    protected final NbtParamsShadow.Param<String> ModelName;
    protected final NbtParamsShadow.Param<Boolean> isDrawCore;
    protected final NbtParamsShadow.Param<Boolean> isDrawEntity;
    protected final NbtParamsShadow.Param<Boolean> trueCopy;

    public GUIBlockScannerBase(InventoryPlayer playerInventory, TileEntityBlocksScannerBase tile)
    {
        super(new ContainerBlockScanner(playerInventory, tile));
        this.tile = tile;
        this.frame = tile.GetLimitFrame();
        container = (ContainerBlockScanner) inventorySlots;
        NBTTagCompound src = tile.writeToNBT(new NBTTagCompound());
        shadow = new NbtParamsShadow(src);
        ModelName = shadow.Create("ModelName", n -> n::setString, NBTTagCompound::getString);
        isDrawCore = shadow.Create("isDrawCore", n -> n::setBoolean, NBTTagCompound::getBoolean);
        isDrawEntity = shadow.Create("isDrawEntity", n -> n::setBoolean, NBTTagCompound::getBoolean);
        trueCopy = shadow.Create("isCoreConnector", n -> n::setBoolean, NBTTagCompound::getBoolean);
    }

    protected void UpdateCameraPosFromCore(Vec3d dest, float tick)
    {
        //フレームの重心を注視点にする
        float x = (frame.getxx()+frame.getmx())/2f;
        float y = (frame.getxy()+frame.getmy())/2f;
        float z = (frame.getxz()+frame.getmz())/2f;
        dest.SetFrom(x+0.5, y+0.5, z+0.5);
        dest.add(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());
    }

    protected int FrameLengthFront() {
        switch(tile.GetSide()){
            case UP: case DOWN: return frame.lenY();
            case EAST: case WEST: return frame.lenX();
            case NORTH: case SOUTH: return frame.lenZ();
            default : return 0;
        }
    }
    protected int FrameLengthSide() {
        switch(tile.GetSide()){
            case UP: case DOWN: case NORTH: case SOUTH:
                return frame.lenX();
            case EAST: case WEST:
                return frame.lenZ();
            default : return 0;
        }
    }
    protected int FrameLengthHeight() {
        switch(tile.GetSide()){
            case UP: case DOWN:
                return frame.lenZ();
            case EAST: case WEST: case NORTH: case SOUTH:
                return frame.lenY();
            default : return 0;
        }
    }

    protected void AddFrameFront(int d){
        switch(tile.GetSide()){
            case UP: case DOWN: frame.AddLengths(0, -d, 0, 0, d, 0); break;
            case EAST: case WEST: frame.AddLengths(-d, 0, 0, d, 0, 0); break;
            case NORTH: case SOUTH: frame.AddLengths(0, 0, -d, 0, 0, d); break;
        }
    }
    protected void AddFrameSide(int d){
        switch(tile.GetSide()){
            case NORTH: case SOUTH:
            case UP: case DOWN: frame.AddLengths(-d, 0, 0, d, 0, 0); break;
            case EAST: case WEST: frame.AddLengths(0, 0, -d, 0, 0, d); break;
        }
    }
    protected void AddFrameHeight(int d){
        switch(tile.GetSide()){
            case UP: case DOWN: frame.AddLengths(0, 0, -d, 0, 0, d); break;
            case EAST: case WEST:
            case NORTH: case SOUTH: frame.AddLengths(0, -d, 0, 0, d, 0); break;
        }
    }
    protected void SetFrameFront(int set){
        AddFrameFront(set-FrameLengthFront());
        UpdateFrame();
    }
    protected void SetFrameSide(int set){
        AddFrameSide(set-FrameLengthSide());
        UpdateFrame();
    }
    protected void SetFrameHeight(int set){
        AddFrameHeight(set-FrameLengthHeight());
        UpdateFrame();
    }

    @Override
    public void initGui() {
        super.initGui();
        container.SetWindowSize(width, height);

        int gDef = -1;
        GuiFormattedTextField field = new GuiFormattedTextField(0, fontRenderer, (width-95)/2, 4, 95, 12, 0xffffff,40,
                ModelName::Get,
                t -> true,
                t -> { ModelName.Set(t); SyncToServer(); });
        Canvas.Register(gDef, field);


        Canvas.Register(gDef, new GuiToggleButton(0, 82, 2, 36, 12,
                "Pro", "Pro",
                () -> _Core.CONFIG_ANNOTATIONS.isProGui,
                b -> {
                    _Core.CONFIG_ANNOTATIONS.isProGui = b;
                    ConfigManager.sync(_Core.MODID, Config.Type.INSTANCE);
                    this.initGui();
                }));

        MakeFrameControl(gDef);
        MakeFrameDirectSlide(gDef);
        MakeOptionControl(gDef);
        MakeScanControl(gDef);
    }
    protected void MakeFrameControl(int group)
    {
        if(_Core.CONFIG_ANNOTATIONS.isProGui)
        {
            frame.SetLimit(new Vec3i(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE), new Vec3i(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE));
            Canvas.Register(group, new GuiLabel("pos1", fontRenderer, 2, 0, 0xffffff));

            Canvas.Register(group, new GuiDragChangerLabel(
                    "X", fontRenderer, 2, 10, 0xffffff,
                    add -> frame.AddLengths(0, 0, 0, smoothFraming(add), 0, 0),
                    this::UpdateFrame));
            Canvas.Register(group, new GuiFormattedTextField(0, fontRenderer, 50, 10, 30, 10, 0xffffff, 5,
                    () -> String.format("%5d", frame.getxx()),
                    s -> s.matches(GuiFormattedTextField.regexInteger),
                    t -> {
                        int set = (Integer.parseInt(t)-frame.lenX());
                        frame.AddLengths(0, 0, 0, set, 0, 0);
                    }));

            Canvas.Register(group, new GuiDragChangerLabel(
                    "Y", fontRenderer, 2, 20, 0xffffff,
                    add -> frame.AddLengths(0, 0, 0, 0, smoothFraming(add), 0),
                    this::UpdateFrame));
            Canvas.Register(group, new GuiFormattedTextField(0, fontRenderer, 50, 20, 30, 10, 0xffffff, 5,
                    () -> String.format("%5d", frame.getxy()),
                    s -> s.matches(GuiFormattedTextField.regexInteger),
                    t -> {
                        int set = (Integer.parseInt(t)-frame.getxy());
                        frame.AddLengths(0, 0, 0, 0, set, 0);
                    }));

            Canvas.Register(group, new GuiDragChangerLabel(
                    "Z", fontRenderer, 2, 30, 0xffffff,
                    add -> frame.AddLengths(0, 0, 0, 0, 0, smoothFraming(add)),
                    this::UpdateFrame));
            Canvas.Register(group, new GuiFormattedTextField(0, fontRenderer, 50, 30, 30, 10, 0xffffff, 5,
                    () -> String.format("%5d", frame.getxz()),
                    s -> s.matches(GuiFormattedTextField.regexInteger),
                    t -> {
                        int set = (Integer.parseInt(t)-frame.getxz());
                        frame.AddLengths(0, 0, 0, 0, 0, set);
                    }));



            Canvas.Register(group, new GuiLabel("pos2", fontRenderer, 2, 45, 0xffffff));

            Canvas.Register(group, new GuiDragChangerLabel(
                    "X", fontRenderer, 2, 55, 0xffffff,
                    add -> frame.AddLengths(smoothFraming(add), 0, 0, 0, 0, 0),
                    this::UpdateFrame));
            Canvas.Register(group, new GuiFormattedTextField(0, fontRenderer, 50, 55, 30, 10, 0xffffff, 5,
                    () -> String.format("%5d", frame.getmx()),
                    s -> s.matches(GuiFormattedTextField.regexInteger),
                    t -> {
                        int set = (Integer.parseInt(t)-frame.getmx());
                        frame.AddLengths(set, 0, 0, 0, 0, 0);
                    }));

            Canvas.Register(group, new GuiDragChangerLabel(
                    "Y", fontRenderer, 2, 65, 0xffffff,
                    add -> frame.AddLengths(0, smoothFraming(add), 0, 0, 0, 0),
                    this::UpdateFrame));
            Canvas.Register(group, new GuiFormattedTextField(0, fontRenderer, 50, 65, 30, 10, 0xffffff, 5,
                    () -> String.format("%5d", frame.getmy()),
                    s -> s.matches(GuiFormattedTextField.regexInteger),
                    t -> {
                        int set = (Integer.parseInt(t)-frame.getmy());
                        frame.AddLengths(0, set, 0, 0, 0, 0);
                    }));

            Canvas.Register(group, new GuiDragChangerLabel(
                    "Z", fontRenderer, 2, 75, 0xffffff,
                    add -> frame.AddLengths(0, 0, smoothFraming(add), 0, 0, 0),
                    this::UpdateFrame));
            Canvas.Register(group, new GuiFormattedTextField(0, fontRenderer, 50, 75, 30, 10, 0xffffff, 5,
                    () -> String.format("%5d", frame.getmz()),
                    s -> s.matches(GuiFormattedTextField.regexInteger),
                    t -> {
                        int set = (Integer.parseInt(t)-frame.getmz());
                        frame.AddLengths(0, 0, set, 0, 0, 0);
                    }));
        }
        else
        {
            // front
            Canvas.Register(group, new GuiDragChangerLabel(
                    "Front", fontRenderer, 2, 5, 0xffffff,
                    d -> AddFrameFront(smoothFraming(d)),
                    this::UpdateFrame));
            Canvas.Register(group, new GuiFormattedTextField(0, fontRenderer, 50, 5, 50, 10, 0xffffff, 5,
                    () -> String.format("%5d", FrameLengthFront()),
                    s -> s.matches(GuiFormattedTextField.regexInteger),
                    t -> this.SetFrameFront(Integer.parseInt(t))));
            GuiUtil.addButton6(Canvas, group,42, 15,
                    () -> {AddFrameFront(-50); UpdateFrame();},
                    () -> {AddFrameFront(-5); UpdateFrame();},
                    () -> {AddFrameFront(-1); UpdateFrame();},
                    () -> {AddFrameFront(1); UpdateFrame();},
                    () -> {AddFrameFront(5); UpdateFrame();},
                    () -> {AddFrameFront(50); UpdateFrame();}
            );

            // side
            Canvas.Register(group, new GuiDragChangerLabel(
                    "Side", fontRenderer, 2, 35, 0xffffff,
                    d -> AddFrameSide(smoothFraming(d)),
                    this::UpdateFrame));
            Canvas.Register(group, new GuiFormattedTextField(0, fontRenderer, 50, 35, 50, 10, 0xffffff, 5,
                    () -> String.format("%5d", FrameLengthSide()),
                    s -> s.matches(GuiFormattedTextField.regexInteger),
                    t -> this.SetFrameSide(Integer.parseInt(t))));
            GuiUtil.addButton6(Canvas, group,42, 45,
                    () -> {AddFrameSide(-50); UpdateFrame();},
                    () -> {AddFrameSide(-5); UpdateFrame();},
                    () -> {AddFrameSide(-1); UpdateFrame();},
                    () -> {AddFrameSide(1); UpdateFrame();},
                    () -> {AddFrameSide(5); UpdateFrame();},
                    () -> {AddFrameSide(50); UpdateFrame();}
            );

            // height
            Canvas.Register(group, new GuiDragChangerLabel(
                    "Height", fontRenderer, 2, 65, 0xffffff,
                    d -> AddFrameHeight(smoothFraming(d)),
                    this::UpdateFrame));
            Canvas.Register(group, new GuiFormattedTextField(0, fontRenderer, 50, 65, 50, 10, 0xffffff, 5,
                    () -> String.format("%5d", FrameLengthHeight()),
                    s -> s.matches(GuiFormattedTextField.regexInteger),
                    t -> SetFrameHeight(Integer.parseInt(t))));
            GuiUtil.addButton6(Canvas, group,42, 75,
                    () -> {AddFrameHeight(-50); UpdateFrame();},
                    () -> {AddFrameHeight(-5); UpdateFrame();},
                    () -> {AddFrameHeight(-1); UpdateFrame();},
                    () -> {AddFrameHeight(1); UpdateFrame();},
                    () -> {AddFrameHeight(5); UpdateFrame();},
                    () -> {AddFrameHeight(50); UpdateFrame();}
            );
        }
    }
    protected void MakeFrameDirectSlide(int group)
    {
        if(_Core.CONFIG_ANNOTATIONS.isProGui)
        {
            Canvas.Register(group, new GuiDragChangerLabel(
                    "<-", fontRenderer, 83, height/2-20, 0xffffff,
                    d -> AddFlexFrameLeft(smoothFraming(d)),
                    this::UpdateFrame));
            Canvas.Register(group, new GuiDragChangerLabel.Vert(
                    "^", fontRenderer, width/2-10, 20, 0xffffff,
                    d -> AddFlexFrameUp(smoothFraming(d)),
                    this::UpdateFrame));
            Canvas.Register(group, new GuiDragChangerLabel.Vert(
                    "v", fontRenderer, width/2-10, height-70, 0xffffff,
                    d -> AddFlexFrameDown(smoothFraming(d)),
                    this::UpdateFrame));
            Canvas.Register(group, new GuiDragChangerLabel(
                    "->", fontRenderer, width-110, height/2-20, 0xffffff,
                    d -> AddFlexFrameRight(smoothFraming(d)),
                    this::UpdateFrame));
        }
    }
    protected void MakeOptionControl(int group)
    {
        if(!_Core.CONFIG_ANNOTATIONS.isProGui) return;

        GuiUtil.addCheckButton(Canvas, fontRenderer, group, width - 20, 42,
                isDrawCore::Get,
                _Core.I18n("gui.scan.text.showcore"),
                isDrawCore::Set);
        GuiUtil.addCheckButton(Canvas, fontRenderer, group, width - 20, 72,
                isDrawEntity::Get,
                _Core.I18n("gui.scan.text.scanmobs"),
                isDrawEntity::Set);
        GuiUtil.addCheckButton(Canvas, fontRenderer, group, width - 20, 12,
                trueCopy::Get,
                _Core.I18n("gui.scan.text.truecopy"),
                trueCopy::Set);
    }
    protected void MakeScanControl(int group)
    {
        Canvas.Register(group,
                new GuiButtonWrapper(0,
                        width - 70, height - 44,
                        60, 26, _Core.I18n("gui.scan.text.scan"),
                        this::StartScanning));
    }



    protected void UpdateFrame()
    {
//        LimitFrame frame = frame;
        MessageChangeLimitLine m = new MessageChangeLimitLine(tile.getPos(), frame);
        PacketHandler.INSTANCE.sendToServer(m);
//        frame.WriteToNBT(shadow.GetNbtTag());
//        SyncClient();
//        SyncToServer();
    }

    protected void SyncClient()
    {
        shadow.WriteAll();
        NBTTagCompound n = tile.writeToNBT(new NBTTagCompound());
        n.merge(shadow.GetNbtTag());
        tile.readFromNBT(n);
    }
    protected void SyncToServer()
    {
        shadow.WriteAll();
        PacketHandler.INSTANCE.sendToServer(
                new MessageSyncNbtCtS(tile, shadow.GetNbtTag()));
    }

    protected void StartScanning()
    {
        SyncClient();
//        SyncToServer();
        tile.startScanning();
    }



    private int sumFrameD = 0;
    private int smoothFraming(int add)
    {
        sumFrameD += add;
        int d = sumFrameD / 4;
        if(d == 0) return 0;
        sumFrameD -= 4*d;
        return d;
    }

    private void AddFlexFrameLeft(int d)
    {
        int o = 0;
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)){
            o = -d;
        }
        EnumFacing horz = mc.player.getHorizontalFacing();
        switch (horz){
            case SOUTH: frame.AddLengths(-o, 0, 0, -d, 0, 0, false); break;
            case NORTH: frame.AddLengths(d, 0, 0, o, 0, 0, false); break;
            case EAST: frame.AddLengths(0, 0, d, 0, 0, o, false); break;
            case WEST: frame.AddLengths(0, 0, -o, 0, 0, -d, false); break;
        }
    }
    private void AddFlexFrameRight(int d)
    {
        int o = 0;
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)){
            o = -d;
        }
        EnumFacing horz = mc.player.getHorizontalFacing();
        switch (horz){
            case SOUTH: frame.AddLengths(-d, 0, 0, -o, 0, 0, false); break;
            case NORTH: frame.AddLengths(o, 0, 0, d, 0, 0, false); break;
            case EAST: frame.AddLengths(0, 0, o, 0, 0, d, false); break;
            case WEST: frame.AddLengths(0, 0, -d, 0, 0, -o, false); break;
        }
    }
    private void AddFlexFrameUp(int d)
    {
        EnumFacing face = EnumFacing.UP;
        if(mc.player.rotationPitch > 45) // down
        {
            face = mc.player.getHorizontalFacing();
        }
        else if(mc.player.rotationPitch < -45)
        {
            face = mc.player.getHorizontalFacing().getOpposite();
        }
        AddFrameFront(d, face);
    }
    private void AddFlexFrameDown(int d)
    {
        EnumFacing face = EnumFacing.DOWN;
        if(mc.player.rotationPitch > 45) // down
        {
            face = mc.player.getHorizontalFacing().getOpposite();
        }
        else if(mc.player.rotationPitch < -45)
        {
            face = mc.player.getHorizontalFacing();
        }
        AddFrameFront(d, face);
    }
    private void AddFrameFront(int d, EnumFacing dir)
    {
        int o = 0;
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)){
            o = -d;
        }
        switch (dir){
            case UP: frame.AddLengths(0, o, 0, 0, d, 0, false); break;
            case DOWN: frame.AddLengths(0, d, 0, 0, o, 0, false); break;
            case SOUTH: frame.AddLengths(0, 0, o, 0, 0, d, false); break;
            case NORTH: frame.AddLengths(0, 0, -d, 0, 0, -o, false); break;
            case EAST: frame.AddLengths(o, 0, 0, d, 0, 0, false); break;
            case WEST: frame.AddLengths(-d, 0, 0, -o, 0, 0, false); break;
        }
    }






    @Override
    public void drawWorldBackground(int p_146270_1_)
    {
        super.drawWorldBackground(p_146270_1_);
        int offset = 2;
        int mx = offset, my = height - 19 * 3 + offset;
        int xx = 164 - offset, xy = height - offset;
        int color = 0x80777777;
        drawRect(mx, my, xx, xy, color);
        mx = 164;
        my = height - 20 + offset;
        xx = 164*2 - offset;
        drawRect(mx, my, xx, xy, color);

        mx = width - 92; my = height - 42;
        xx = mx + 20; xy = my + 20;
        drawRect(mx, my, xx, xy, color);
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseZ);

        this.fontRenderer.drawString("Name:", width/2-76, 4, 0xB0B0B0);
//        drawString(this.fontRenderer, "RotateCopy", width-76, 3, 0xffffff);
//        drawString(this.fontRenderer, "Copy Mode", width-76, 153, 0xffffff);
//        drawRightedString(this.fontRenderer, Integer.toString(tile.copyNum.Get()), width-2, 15, 0xffffff);
        drawRightedString(fontRenderer, (int)(100*tile.getCookProgress())+"%", width - 70, height-15, 0xffffff);


    }





    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    @Override
    public void onGuiClosed()
    {
        SyncToServer();
        super.onGuiClosed();
    }

}
