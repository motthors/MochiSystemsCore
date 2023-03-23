package jp.mochisystems.core.bufferedRenderer;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc.eventhandler.TickEventHandler;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class SmartBufferManager {

    Map<Integer, Tag> bufferHashMap = new HashMap<>();

    private class Tag{
        int count = 1;
        ICachedBufferRenderer renderer;
    }

    List<ICachedBufferRenderer> reservedDeleteRenderer = new ArrayList<>();

    public SmartBufferManager()
    {
        TickEventHandler.AddRenderTickPostListener(this::_Delete);
    }

    public void Construct(CachedBufferBase buffer)
    {
        int hash = buffer.GetHash();
        int bufferId = -1;
        boolean hasHash = bufferHashMap.containsKey(hash);
        if (!hasHash) {
            ICachedBufferRenderer renderer = OpenGlHelper.useVbo() ?
                    new RendererVbo(bufferId, buffer)
                    :new RendererList(buffer);
            buffer.SetRenderer(renderer);
//            Logger.debugInfo(hash + "new!");
            Tag t = new Tag();
            t.renderer = renderer;
            bufferHashMap.put(hash, t);
            renderer.Build();
        } else {
            Tag t = bufferHashMap.get(hash);
//            Logger.debugInfo(hash + "exist!  count:" + t.count +" > "+(t.count+1));
            buffer.SetRenderer(t.renderer);
            bufferHashMap.get(hash).count++;
        }
    }

    public void Delete(CachedBufferBase buffer) {
        int hash = buffer.GetHash();
        Tag tag = bufferHashMap.get(hash);
        if (tag == null) return;
        int count = tag.count;
        if (count > 1) {
//            Logger.debugInfo(hash + " count down " + count + " -> " + (count - 1));
            bufferHashMap.get(hash).count--;
        } else {
//            Logger.debugInfo(hash + "delete!");
            bufferHashMap.remove(hash);
            reservedDeleteRenderer.add(tag.renderer);
        }
    }

    private void _Delete(float f){
        if(reservedDeleteRenderer.size() > 0) {
            for (ICachedBufferRenderer renderer : reservedDeleteRenderer) {
                renderer.Delete();
            }
            reservedDeleteRenderer.clear();
        }
    }

    @SubscribeEvent
    public void deleteAllBuffer(FMLNetworkEvent.ClientDisconnectionFromServerEvent e)
    {
        for(Tag tag : bufferHashMap.values())
        {
            reservedDeleteRenderer.add(tag.renderer);
        }
        Logger.debugInfo("Delete All Buffers");//TODO ログの出力チェック
        bufferHashMap.clear();
    }


    public static class Dummy extends SmartBufferManager {
        @Override public void Construct(CachedBufferBase buffer){}
        @Override public void Delete(CachedBufferBase buffer){}
    }




}
