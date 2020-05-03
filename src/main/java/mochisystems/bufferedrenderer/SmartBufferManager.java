package mochisystems.bufferedrenderer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import mochisystems._core.Logger;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SmartBufferManager {

    private class Tag{
        int count = 1;
        int bufferId = IBufferedRenderer.PreInitBufferId;
    }

    Map<Integer, Tag> bufferIdHash = new HashMap<>();

    public void Construct(IBufferedRenderer renderer)
    {
        int hash = renderer.GetHash();
        boolean hasHash = bufferIdHash.containsKey(hash);
        if(!hasHash)
        {
//            Logger.debugInfo(hash + "new!");
            int bufferId = GL11.glGenLists(1);
            renderer.Construct(bufferId);
            Tag t = new Tag();
            t.bufferId = bufferId;
            bufferIdHash.put(hash, t);
        }
        else
        {
//            Logger.debugInfo(hash + "exist!  count:"+bufferIdHash.get(hash).count);
            renderer.SetBufferID(bufferIdHash.get(hash).bufferId);
            bufferIdHash.get(hash).count++;
        }
    }

    public void Delete(IBufferedRenderer renderer)
    {
        int id = renderer.GetBufferID();
        if(IBufferedRenderer.PreInitBufferId == id) return;
        int hash = renderer.GetHash();
        Tag tag = bufferIdHash.get(hash);
        if(tag == null) return;
        int count = tag.count;
        if(count > 1)
        {
//            Logger.debugInfo(hash + " count down " + count + " -> " + (count-1));
            bufferIdHash.get(hash).count--;
        }
        else
        {
//            Logger.debugInfo(hash + "delete!  id:"+id);
            bufferIdHash.remove(hash);
            if(id >= 0) GL11.glDeleteLists(id, 1);
        }
    }

    @SubscribeEvent
    public void deleteAllBuffer(FMLNetworkEvent.ClientDisconnectionFromServerEvent e)
    {
        for(Tag tag : bufferIdHash.values())
        {
//            Logger.debugInfo(tag.bufferId + "delete!  "+tag.count+" -> 0");
            bufferIdHash.remove(tag.bufferId);
        }
        bufferIdHash.clear();
    }

    public static class Dummy extends SmartBufferManager{
        @Override public void Construct(IBufferedRenderer r){}
        @Override public void Delete(IBufferedRenderer r){}
    }
}
