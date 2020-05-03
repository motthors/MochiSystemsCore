package mochisystems.easyasm.adaptor;

import java.util.HashMap;
import java.util.Map;

import mochisystems.easyasm.distributor.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

public class MethodAdaptorBase extends MethodVisitor implements Opcodes{
	
//	private MethodVisitor mv;
//	private String TargetClassName;
//	private Map<String, MethodAdapter> methodmap = new HashMap<>();

	public MethodAdaptorBase() {
		super(ASM5);
//		super(ASM5, mv);
	}

	public MethodAdaptorBase SetMethodVisitor(MethodVisitor mv)
	{
		if(this.mv != null && this.mv instanceof MethodAdaptorBase)
		{
			((MethodAdaptorBase)this.mv).SetMethodVisitor(mv);
		}
		else this.mv = mv;
		return this;
	}

//	public MethodAdaptorBase add(MethodAdapter target)
//	{
//		FMLRelaunchLog.info("ClassAdapter add : "+target.Name+" ::: "+target.mcpName+target.Desc);
//		methodmap.put(target.Name, target);
//		methodmap.put(target.mcpName+target.Desc, target);
//		return this;
//	}

	public MethodAdaptorBase Next(MethodAdaptorBase methodadaptor)
	{
		methodadaptor.mv = mv;
		mv = methodadaptor;
		return this;
	}
	
//	public void setClassName(String TargetClass)
//	{
//		this.TargetClassName = TargetClass;
//	}
}
