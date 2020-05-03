package mochisystems.easyasm.distributor;


import mochisystems.easyasm.adaptor.MethodAdaptorBase;

public class MethodAdapter {

	public String Name;
	public String mcpName;
	public String Desc;
	public MethodAdaptorBase methodAdaptor;
	
	public MethodAdapter(String name, String mcpName, String desc, MethodAdaptorBase methodAdaptor)
	{
		this.Name = name;
		this.mcpName = mcpName;
		this.Desc = desc;
		this.methodAdaptor = methodAdaptor;
	}
}

