package mochisystems._mc._1_7_10.easyasm.distributor;

import mochisystems._mc._1_7_10.easyasm.adaptor.FieldAdapterBase;

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
