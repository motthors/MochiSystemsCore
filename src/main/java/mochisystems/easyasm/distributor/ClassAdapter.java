package mochisystems.easyasm.distributor;

import java.util.HashMap;
import java.util.Map;

import mochisystems.easyasm.EasyAsm;
import mochisystems.easyasm.adaptor.FieldAdapterBase;
import org.objectweb.asm.*;


public class ClassAdapter extends ClassVisitor implements Opcodes{
	
	protected String TargetClassName;
//	private List<MethodAdaptorBase> methodAdaptMap = new ArrayList<>();
	private Map<String, MethodAdapter> methodMap = new HashMap<>();
    private Map<String, FieldAdapter> fieldMap = new HashMap<>();


	public ClassAdapter(String TargetClass)
	{
		super(ASM5);
		this.TargetClassName = TargetClass;
	}
	
	public String Name()
	{
		return TargetClassName;
	}

//	public MethodAdaptorBase add(MethodAdaptorBase pack)
//	{
//		pack.setClassName(TargetClassName);
//		methodAdaptMap.add(pack);
//		return pack;
//	}
    public ClassAdapter add(FieldAdapter targetField)
    {
        fieldMap.put(targetField.Name, targetField);
        fieldMap.put(targetField.mcpName, targetField);
        return this;
    }

	public ClassAdapter add(MethodAdapter targetMethod)
    {
        methodMap.put(targetMethod.Name, targetMethod);
        methodMap.put(targetMethod.mcpName+targetMethod.Desc, targetMethod);
        return this;
    }
	
	public void Transform(ClassWriter cw, ClassReader cr)
	{
        this.cv = cw;
        cr.accept(this, 0);
        addMember(cw);
	}

    public FieldVisitor FieldAdapt(FieldVisitor fv, int access, String name , String desc, Object value){return fv;}
    private void addMember(ClassWriter cw){}




	////// call from cr.accept() /////

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces)
    {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        String mapedFieldName = EasyAsm.mapFieldName(TargetClassName, name, desc);
        if(fieldMap.containsKey(mapedFieldName))
        {
            FieldAdapterBase fa = fieldMap.get(mapedFieldName).fieldAdapter;
            FieldVisitor fv = super.visitField(fa.ModifyAccess(access), name, desc, signature, value);
            return fa.SetFieldVisitor(fv);
        }
        else if(fieldMap.containsKey("*"))
        {
            FieldAdapterBase fa = fieldMap.get("*").fieldAdapter;
            FieldVisitor fv = super.visitField(fa.ModifyAccess(access), name, desc, signature, value);
            return fa.SetFieldVisitor(fv);
        }
        else
        {
            return super.visitField(access, name, desc, signature, value);
        }
    }

    /**
     * メソッドについて呼ばれる。
     *
     * @param access  {@link Opcodes}に載ってるやつ。publicとかstaticとかの状態がわかる。
     * @param name	メソッドの名前。
     * @param desc メソッドの(引数と返り値を合わせた)型。
     * @param signature   ジェネリック部分を含むメソッドの(引数と返り値を合わせた)型。ジェネリック付きでなければおそらくnull。
     * @param exceptions  throws句にかかれているクラスが列挙される。Lと;で囲われていないので  {@link String#replace(char, char)}で'/'と'.'を置換してやればOK。
     * @return ここで返したMethodVisitorのメソッド群が適応される。  ClassWriterがセットされていればMethodWriterがsuperから降りてくる。
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        // EasyAsm.log("MFWTransformLog : visitMethod : name'%s%s'", name, desc);
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        String mapMethodName = EasyAsm.mapMethodName(TargetClassName, name, desc);
        String FullName = mapMethodName + desc;
        if(methodMap.containsKey(FullName))
        {
            return methodMap.get(FullName).methodAdaptor.SetMethodVisitor(mv);
        }
        else if(methodMap.containsKey(mapMethodName))
        {
            return methodMap.get(mapMethodName).methodAdaptor.SetMethodVisitor(mv);
        }
        else if(methodMap.containsKey("*"))
        {
            return methodMap.get("*").methodAdaptor.SetMethodVisitor(mv);
        }
        else
        {
            return mv;
        }
//        return MethodAdaptor.MethodAdapt(mv, name, desc);
    }

    @Override
    public void visitEnd()
    {
        //if(cv != null && cv instanceof ClassWriter)pack.addMethod((ClassWriter)cv);
        super.visitEnd();
    }
}
