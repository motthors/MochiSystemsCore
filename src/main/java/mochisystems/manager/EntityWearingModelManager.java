package mochisystems.manager;

import mochisystems.blockcopier.IItemBlockModelHolder;
import mochisystems.util.EntityModelWrapper;
import mochisystems.util.IModel;
import mochisystems.util.ModelBiped;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class EntityWearingModelManager {

    private static class ModelSet{
        IModel[] models;
        ModelBiped[] biped;
    }

    private static HashMap<Integer, ModelSet> entityIdToModels = new HashMap<>();

    public static void OnWear(Entity entity, IModel model, int slot)
    {
        ModelSet set = null;
        if(entityIdToModels.containsKey(entity.getEntityId()))
        {
            set = entityIdToModels.get(entity.getEntityId());
        }
        else
        {
            set = new ModelSet();
            set.models = new IModel[4];
            set.biped = new ModelBiped[4];
            for(int i = 0; i < set.biped.length; ++i) set.biped[i] = new ModelBiped();
            entityIdToModels.put(entity.getEntityId(), set);
        }
        if(set.models[slot] != null) set.biped[slot].Invalidate();
        set.models[slot] = model;
        set.biped[slot].SetModel(model, slot);
    }

    public static void UpdateModel()
    {
        for(ModelSet set : entityIdToModels.values())
        {
            for(ModelBiped biped : set.biped)
            {
                if(biped != null) biped.Update();
            }
        }
    }

    public static ModelBiped GetModelBiped(Entity entity, ItemStack stack, int slot) {
        slot = 3 - slot;
        try {
            if(!entityIdToModels.containsKey(entity.getEntityId())) OnWear(entity, GetModel(entity, stack), slot);
            return entityIdToModels.get(entity.getEntityId()).biped[slot];
        } catch (NullPointerException e){
            throw e;
        }
    }


    public static IModel GetModel(Entity entity, ItemStack stack)
    {
        EntityModelWrapper wrapper = new EntityModelWrapper(entity);
        IModel model = ((IItemBlockModelHolder)stack.getItem()).GetBlockModel(wrapper);
        model.SetWorld(Minecraft.getMinecraft().theWorld);
        model.readFromNBT(stack.getTagCompound());
        wrapper.SetModel(model);
        return model;
    }
}
