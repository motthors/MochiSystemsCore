package jp.mochisystems.core.blockcopier;

public interface BlockExcluder {
    int getMinX();
    int getMaxX();
    int getMinY();
    int getMaxY();
    int getMinZ();
    int getMaxZ();
}
