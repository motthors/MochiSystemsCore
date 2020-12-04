package mochisystems.util;

import mochisystems.blockcopier.IModelCollider;
import mochisystems._mc._1_7_10.world.MTYBlockAccess;
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
