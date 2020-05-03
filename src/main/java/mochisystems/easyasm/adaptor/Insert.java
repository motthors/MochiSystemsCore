package mochisystems.easyasm.adaptor;


import org.objectweb.asm.MethodVisitor;

import java.util.function.Consumer;

public class Insert extends MethodAdaptorBase{

    protected Consumer<MethodVisitor> PushCode;

    public Insert(Consumer<MethodVisitor> PushCode)
    {
        this.PushCode = PushCode;
    }

    public static class First extends Insert {
        public First(Consumer<MethodVisitor> PushCode){
            super(PushCode);
        }
        @Override
        public void visitCode() {
            super.visitCode();
            PushCode.accept(this);
        }
    }

    public static class Last extends Insert {
        public Last(Consumer<MethodVisitor> PushCode){
            super(PushCode);
        }
        @Override
        public void visitInsn(int opcode) {
            if(IRETURN <= opcode && opcode <= RETURN)
            {
                PushCode.accept(this);
            }
            super.visitInsn(opcode);
        }
    }
}
