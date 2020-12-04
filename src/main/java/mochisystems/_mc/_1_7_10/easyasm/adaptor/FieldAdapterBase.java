package mochisystems._mc._1_7_10.easyasm.adaptor;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class FieldAdapterBase extends FieldVisitor implements Opcodes{


	public FieldAdapterBase() {
		super(ASM5);
//		super(ASM5, mv);
	}

	public int ModifyAccess(int access)
	{
		return access;
	}

	public FieldAdapterBase SetFieldVisitor(FieldVisitor fv)
	{
		if(this.fv != null && this.fv instanceof FieldAdapterBase)
		{
			((FieldAdapterBase)this.fv).SetFieldVisitor(fv);
		}
		else this.fv = fv;
		return this;
	}

	public FieldAdapterBase Next(FieldAdapterBase methodadaptor)
	{
		methodadaptor.fv = fv;
		fv = methodadaptor;
		return this;
	}

}
