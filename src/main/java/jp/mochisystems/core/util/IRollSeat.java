package jp.mochisystems.core.util;

import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import net.minecraft.entity.Entity;

public interface IRollSeat {
    Vec3d RemovePassenger(Entity rider);
    Quaternion GetSeatRotation(Entity rider);
    Quaternion Attitude(Quaternion out, float partialTick);
}
