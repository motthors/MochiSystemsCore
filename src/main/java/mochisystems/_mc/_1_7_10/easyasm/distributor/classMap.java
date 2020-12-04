package mochisystems._mc._1_7_10.easyasm.distributor;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

public abstract class classMap {

	protected abstract void MakeMap();


	private Map<String, ClassAdapter> transmap;
	protected boolean isMapped = false;
	
	public classMap getMap()
	{
		if(!isMapped)
		{
			FMLRelaunchLog.info("TransformLog : MakeMap : side:"+Thread.currentThread().getName());
			transmap = new HashMap<>();
			MakeMap();
			isMapped = true;
		}
		
		return this;
	}
	
	public ClassAdapter GetTargetClassOrDefault(String transformedName)
	{
		return transmap.getOrDefault(transformedName, null);
	}

	protected ClassAdapter add(ClassAdapter target)
	{
		String str = target.Name();
		if(!transmap.containsKey(str))
		{
			transmap.put(str, target);
			return target;
		}
		else
		{
			return transmap.get(str);
		}
	}
	
}
