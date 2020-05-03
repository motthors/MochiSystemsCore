package mochisystems.easyasm.adaptor;


import org.objectweb.asm.MethodVisitor;

import java.time.format.DecimalStyle;
import java.util.function.Consumer;

public class InsertStaticMethod extends MethodAdaptorBase{

    protected String ClassName;
    protected String MethodName;
    protected String Desc;
    protected Consumer<MethodVisitor>[] PushVarToStack;

    public InsertStaticMethod(String ClassName, String MethodName, String Desc,
                                   Consumer<MethodVisitor>... PushVarToStack)
    {
        this.ClassName = ClassName;
        this.MethodName = MethodName;
        this.Desc = Desc;
        this.PushVarToStack = PushVarToStack;
    }

    public static class First extends InsertStaticMethod{
        public First(String ClassName, String MethodName, String Desc, Consumer<MethodVisitor>... PushVarToStack){
            super(ClassName, MethodName, Desc, PushVarToStack);
        }
        @Override
        public void visitCode() {
            super.visitCode();
            for (Consumer<MethodVisitor> rambda : PushVarToStack) rambda.accept(this);
            super.visitMethodInsn(INVOKESTATIC, ClassName, MethodName, Desc, false);
        }
    }

    public static class Last extends InsertStaticMethod{
        public Last(String ClassName, String MethodName, String Desc, Consumer<MethodVisitor>... PushVarToStack){
            super(ClassName, MethodName, Desc, PushVarToStack);
        }
        @Override
        public void visitInsn(int opcode) {
            if(IRETURN <= opcode && opcode <= RETURN)
            {
                for (Consumer<MethodVisitor> rambda : PushVarToStack) rambda.accept(this);
                super.visitMethodInsn(INVOKESTATIC, ClassName, MethodName, Desc, false);
            }
            super.visitInsn(opcode);
        }
    }
}
