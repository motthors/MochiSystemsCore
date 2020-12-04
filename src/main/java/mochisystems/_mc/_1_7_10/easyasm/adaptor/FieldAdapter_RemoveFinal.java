package mochisystems._mc._1_7_10.easyasm.adaptor;

import org.objectweb.asm.Opcodes;

public class FieldAdapter_RemoveFinal extends FieldAdapterBase implements Opcodes{

    public int ModifyAccess(int access)
    {
        return access ^ ACC_FINAL;
    }
}
