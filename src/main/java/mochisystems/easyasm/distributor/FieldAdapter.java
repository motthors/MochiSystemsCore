package mochisystems.easyasm.distributor;

import mochisystems.easyasm.adaptor.FieldAdapterBase;
import mochisystems.easyasm.adaptor.MethodAdaptorBase;

public class FieldAdapter {
    public String Name;
    public String mcpName;
//    public String Desc;
    public FieldAdapterBase fieldAdapter;

    public FieldAdapter(String name, String mcpName, FieldAdapterBase methodAdaptor)
    {
        this.Name = name;
        this.mcpName = mcpName;
//        this.Desc = desc;
        this.fieldAdapter = methodAdaptor;
    }
}
