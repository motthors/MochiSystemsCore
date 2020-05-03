package mochisystems.util;

import mochisystems.blockcopier.IModelCollider;
import mochisystems.blockcopier.MTYBlockAccess;
import net.minecraft.world.World;

public interface IModelController {

    int CorePosX();
    int CorePosY();
    int CorePosZ();
    int CoreSide();
    boolean IsInvalid();
    boolean IsRemote();
    void markBlockForUpdate();
    World World();
    IModel GetBlockModel(int x, int y, int z);
    IModelCollider MakeAndSpawnCollider(IModel parent, MTYBlockAccess blockAccess);
}
