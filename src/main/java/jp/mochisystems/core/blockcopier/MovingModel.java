package jp.mochisystems.core.blockcopier;


import jp.mochisystems.core.util.InterpolationTick;

public interface MovingModel {
    void SetAccel(float accel);
    void SetSpeed(float speed);
    float GetAccel();
    float GetSpeed();
    InterpolationTick Position();
}
