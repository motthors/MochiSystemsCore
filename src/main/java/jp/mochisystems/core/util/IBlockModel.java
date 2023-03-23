package jp.mochisystems.core.util;

import jp.mochisystems.core._mc.entity.EntityBlockModelCollider;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import jp.mochisystems.core.math.Quaternion;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public interface IBlockModel extends IModel {
    MTYBlockAccess GetBlockAccess();
    boolean IsEnableCollider();
    void ToggleEnableCollider();
    void UpdateChildConnector(@Nonnull Connector connector);
    void RegisterColEntity(EntityBlockModelCollider entity);
}