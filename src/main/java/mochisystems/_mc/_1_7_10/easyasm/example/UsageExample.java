package mochisystems._mc._1_7_10.easyasm.example;

import mochisystems._mc._1_7_10.easyasm.EasyAsm;
import mochisystems._mc._1_7_10.easyasm.adaptor.MethodAdapter_Logger;
import mochisystems._mc._1_7_10.easyasm.adaptor.MethodAdaptorBase;
import mochisystems._mc._1_7_10.easyasm.distributor.ClassAdapter;
import mochisystems._mc._1_7_10.easyasm.distributor.MethodAdapter;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.objectweb.asm.Opcodes;

public class UsageExample extends EasyAsm{
	
	@Override
	protected void MakeMap()
	{
		/*
		 * add(対象のクラス).add(対象の関数).register(MethodAdapterBase派生クラス)
		 * な感じでどんどん追加していけます。Rxみたいな感じで
		 */
		add(new ClassAdapter("net.minecraft.client.renderer.EntityRenderer")
			.add(new MethodAdapter("func_78471_a", "renderWorld", "(FJ)V",
					new MethodAdapter_Logger()))
			.add(new MethodAdapter("func_78479_a", "setupCameraTransform", "(FI)V",
					new MethodAdapter_Logger())));


		/*
		 * 直接アダプタの実装を書きたいときはMethodAdaptorBaseに匿名クラスを実装できます
		 */
		add(new ClassAdapter("net.minecraft.client.renderer.EntityRenderer")
			.add(new MethodAdapter("func_78471_a", "renderWorld", "(FJ)V",
					new MethodAdaptorBase(){
						 int MethodCount = 0;
						 @Override
						 public void visitFieldInsn(int opcode, String owner, String name, String desc)
						 {
							 boolean flag = false;
							 flag |= "farPlaneDistance".equals(name);
							 flag |= "field_78530_s".equals(name);
							 if(flag && opcode == Opcodes.PUTFIELD && MethodCount==0)
							 {
								 MethodCount += 1;
								 super.visitFieldInsn(Opcodes.GETSTATIC, "hoge/fuga/someclass", "funcxxxx", "F");
								 super.visitInsn(Opcodes.FMUL);
							 }
							 super.visitFieldInsn(opcode, owner, name, desc);
						 }
					 }
				)));



		/*
		 * Loggerはバイトコードを順番にログに出します。
		 * 開発環境と実行環境でコードが違うときとかの確認などに
		 */
		add(new ClassAdapter("net.minecraft.client.renderer.EntityRenderer")
			.add(new MethodAdapter("*", "*", "",
					new MethodAdapter_Logger())));
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {UsageExample.class.getName()};
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
