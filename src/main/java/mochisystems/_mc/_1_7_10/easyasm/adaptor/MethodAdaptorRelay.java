//package mochisystems._mc._1_7_10.easyasm.adaptor;
//
//import static org.objectweb.asm.Opcodes.ASM5;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.objectweb.asm.AnnotationVisitor;
//import org.objectweb.asm.Attribute;
//import org.objectweb.asm.Handle;
//import org.objectweb.asm.Label;
//import org.objectweb.asm.MethodVisitor;
//import org.objectweb.asm.TypePath;
//
//public class MethodAdaptorRelay extends MethodVisitor{
//
//	public MethodAdaptorBase wrapper;
//
//	public MethodAdaptorRelay(MethodVisitor mv, MethodAdaptorBase wrapper)
//	{
//		super(ASM5, mv);
//		this.wrapper = wrapper;
//	}
//
//	public void visitCode()
//	{
//		wrapper.visitCode();
//	}
//
//
////	visitAnnotationDefault()
////	visitAnnotation(String, boolean)
////	visitTypeAnnotation(int, TypePath, String, boolean)
////	visitParameterAnnotation(int, String, boolean)
//
//	public void visitParameter(String name, int access) {
//		wrapper.visitParameter(name,access);
//	}
//	public void visitAttribute(Attribute attr) {
//		wrapper.visitAttribute(attr);
//	}
//	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
//		wrapper.visitFrame(type, nLocal, local, nStack, stack);
//	}
//	public void visitInsn(int opcode) {
//		wrapper.visitInsn(opcode);
//	}	//xxxInsn()�̑O�ɕK������́H
//	public void visitIntInsn(int opcode, int operand) {
//		wrapper.visitIntInsn(opcode, operand);
//	}	//�����ł�����
//	public void visitVarInsn(int opcode, int var) {
//		wrapper.visitVarInsn(opcode, var);
//	}	//�ϐ�
//	public void visitTypeInsn(int opcode, String type) {
//		wrapper.visitTypeInsn(opcode, type);
//	}	//new��instanceof�Ƃ��@�@either NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
//	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
//		wrapper.visitFieldInsn(opcode, owner, name, desc);
//	}	//either GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
//	@SuppressWarnings("deprecation")
//	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
//		wrapper.visitMethodInsn(opcode, owner, name, desc);
//	}
//	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf){
//		wrapper.visitMethodInsn(opcode, owner, name, desc, itf);
//	}
//	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
//		wrapper.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
//	}
//	public void visitJumpInsn(int opcode, Label label) {
//		wrapper.visitJumpInsn(opcode, label);
//	}
//	public void visitLabel(Label label) {
//		wrapper.visitLabel(label);
//    }
//	public void visitLdcInsn(Object cst) {
//		wrapper.visitLdcInsn(cst);
//	}
//	public void visitIincInsn(int var, int increment) {
//		wrapper.visitIincInsn(var, increment);
//	}
//	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
//		wrapper.visitTableSwitchInsn(min, max, dflt, labels);
//	}
//	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
//		wrapper.visitLookupSwitchInsn(dflt, keys, labels);
//	}
//	public void visitMultiANewArrayInsn(String desc, int dims) {
//		wrapper.visitMultiANewArrayInsn(desc, dims);
//	}
////	public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
////		return wrapper.visitInsnAnnotation(typeRef, typePath, desc, visible);
////	}
//	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
//		wrapper.visitTryCatchBlock(start, end, handler, type);
//	}
////	public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
////		return wrapper.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
////	}
//	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
//		wrapper.visitLocalVariable(name, desc, signature, start, end, index);
//	}
////	public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible){
////		return wrapper.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
////	}
//	public void visitLineNumber(int line, Label start) {
//		wrapper.visitLineNumber(line, start);
//	}
//	public void visitMaxs(int maxStack, int maxLocals) {
//		wrapper.visitMaxs(maxStack, maxLocals);
//	}
//	public void visitEnd() {
//		wrapper.visitEnd();
//	}
//}
