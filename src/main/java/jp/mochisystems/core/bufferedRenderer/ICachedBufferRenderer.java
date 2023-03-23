package jp.mochisystems.core.bufferedRenderer;

public interface ICachedBufferRenderer {
    boolean IsBuilt();
    void Build();
    void Render();
    void Delete();
}
