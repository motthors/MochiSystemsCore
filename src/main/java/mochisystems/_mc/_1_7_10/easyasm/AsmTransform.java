package mochisystems._mc._1_7_10.easyasm;

import mochisystems._mc._1_7_10.easyasm.adaptor.InsertStaticMethod;
import mochisystems._mc._1_7_10.easyasm.distributor.ClassAdapter;
import mochisystems._mc._1_7_10.easyasm.distributor.MethodAdapter;
import mochisystems._mc._1_7_10.easyasm.example.UsageExample;
import mochisystems._mc._1_7_10.easyasm.example.modContainer;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class AsmTransform extends EasyAsm {

	@Override
	protected void MakeMap()
	{
        add(new ClassAdapter("net.minecraft.client.renderer.EntityRenderer")
            .add(new MethodAdapter("func_78467_g", "orientCamera", "(F)V",
                    new InsertStaticMethod.First(
                    		"mochisystems/hook/OrientCameraHooker",
							"HandleFirst", "(F)V",
							(MethodVisitor mv) -> mv.visitVarInsn(Opcodes.FLOAD, 1)
					)
					.Next(
						new InsertStaticMethod.Last(
								"mochisystems/hook/OrientCameraHooker",
								"HandleLast", "(F)V",
								(MethodVisitor mv) -> mv.visitVarInsn(Opcodes.FLOAD, 1)
						)
					)
//                    .Next(new MethodAdapter_Logger())
				)
			)
        );
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {AsmTransform.class.getName()};
	}
	@Override
	public String getModContainerClass() {
		return modContainer.class.getName();
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader)
	{
		classLoader.registerTransformer(UsageExample.class.getName());
	}
}