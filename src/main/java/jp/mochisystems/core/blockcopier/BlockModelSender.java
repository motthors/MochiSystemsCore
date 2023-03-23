package jp.mochisystems.core.blockcopier;

import jp.mochisystems.core._mc.message.MessageSendModelData;
import jp.mochisystems.core._mc.message.PacketHandler;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BlockModelSender {

    public interface IHandler {
        BlockModelSender GetSender();
        void OnCompleteReceive(NBTTagCompound nbt);
    }

    IHandler handler;

    public BlockModelSender(IHandler handler)
    {
        this.handler = handler;
    }

    public void SendNbtToServer(NBTTagCompound nbt, BlockPos pos)
    {
        byte[] bytearray = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            CompressedStreamTools.writeCompressed(nbt, os);
            bytearray = os.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int byteLength = bytearray.length;
        int divNum = byteLength / (20*1024) + 1;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytearray);
        for(int i=0; i<divNum; ++i)
        {
            byte[] divedArray = new byte[(20*1024)];
            inputStream.read(divedArray, 0, (20*1024));
            MessageSendModelData packet = new MessageSendModelData(pos.getX(), pos.getY(), pos.getZ(), i, divNum, divedArray);
            PacketHandler.INSTANCE.sendToServer(packet);
        }
    }


    private byte[][] arrayDataIndex;
    public void ReceivePartialBlockData(int idx, int total, byte[] bytes)
    {
        if(arrayDataIndex == null || arrayDataIndex.length != total) arrayDataIndex = new byte[total][];
        arrayDataIndex[idx] = bytes;

        int i=0;
        for( ; i<total; ++i)
        {
            if(arrayDataIndex[i] == null)break;
        }
        if(i != total)return;

        ByteArrayOutputStream allbytearray = new ByteArrayOutputStream();
        for(i=0; i<total; ++i)
        {
            allbytearray.write(arrayDataIndex[i], 0, arrayDataIndex[i].length);
        }
        bytes = allbytearray.toByteArray();

        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        NBTTagCompound nbt = null;
        try {
            nbt = CompressedStreamTools.readCompressed(stream);
        } catch (IOException e) { e.printStackTrace(); }
        if(nbt == null)
        {
            arrayDataIndex = null;
            return;
        }

        handler.OnCompleteReceive(nbt);
    }
}
