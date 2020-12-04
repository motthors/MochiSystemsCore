package mochisystems._mc._1_7_10.easyasm.adaptor;

import cpw.mods.fml.relauncher.FMLRelaunchLog;
import org.objectweb.asm.*;

public class FieldAdapter_Logger extends FieldAdapterBase implements Opcodes {

	private void log(String tag) {
		FMLRelaunchLog.info("TransformLog : FieldLogger : " + tag);
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		log("Annotation  desc:" + desc + ", visible:" + visible);
		return super.visitAnnotation(desc, visible);
	}

	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		log("TypeAnnotation  typeRef:"+typeRef+" typePath:"+typePath.toString()+" desc:" + desc + ", visible:" + visible);
		return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
	}

	public void visitAttribute(Attribute attr) {
		log("Attribute  attr:" + attr.toString());
		super.visitAttribute(attr);
	}

	public void visitEnd() {
		log("end");
		super.visitEnd();
	}
}
