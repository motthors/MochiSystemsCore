package mochisystems._mc._1_7_10.easyasm.distributor;


import mochisystems._mc._1_7_10.easyasm.adaptor.MethodAdaptorBase;

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

