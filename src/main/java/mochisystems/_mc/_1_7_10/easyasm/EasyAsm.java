package mochisystems._mc._1_7_10.easyasm;


import java.io.File;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import mochisystems._mc._1_7_10.easyasm.distributor.ClassAdapter;
import mochisystems._mc._1_7_10.easyasm.distributor.classMap;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.ITweaker;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

//@MCVersion("1.7.10")
@TransformerExclusions({"easyasm","easyasm.adaptor","easyasm.Distributor"})
public abstract class EasyAsm extends classMap implements IFMLLoadingPlugin, ITweaker, IClassTransformer {
	

	/////// IFMLLoadingPlugin /////////
	
	static File location;
	
	public String[] getLibraryRequestClass() {
		return null;
	}

//    @Override
//    public String[] getASMTransformerClass() {
//    	return new String[] {classDistributor.class.getName()};
//    }
//    @Override
//    public String getModContainerClass() {
//    	return modContainer.class.getName();
//    }
    @Override
    public String getSetupClass() {
    	return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    	 if (data.containsKey("coremodLocation"))
         {
             location = (File) data.get("coremodLocation");
         }
    }

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
	
	
	
	/////// ITweaker /////////
	
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
	{
		//dbg("tsfs : acceptOptions");
	}
	
//	public void injectIntoClassLoader(LaunchClassLoader classLoader)
//	{
//		//dbg("tsfs : injectIntoClassLoader");
//		classLoader.registerTransformer(classDistributor.class.getName());
//	}
	  
	public String getLaunchTarget()
	{
		//dbg("tsfs : getLaunchTarget");
		return "net.minecraft.client.main.Main";
	}
	
	public String[] getLaunchArguments()
	{
		//dbg("tsfs : getLaunchArguments");
		return new String[0];
	}

	/////// IClassTransformer

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		try{
			if(name.contains("easyasm"))return bytes;

			////////// 各クラスが順番に呼ばれてくる //////////
			if(transformedName.equals("net.minecraft.client.renderer.EntityRenderer"))
				log("TransformLog : transform : name:"+name+" :: Tname:"+transformedName+":side:"+Thread.currentThread().getName());

			// 今流れてきているクラスが変換対象として登録されていたらそのpackを持ってくる
			ClassAdapter pack = getMap().GetTargetClassOrDefault(transformedName);

			// packが無ければそのまま帰る
			if( pack == null ) return bytes;

			ClassReader cr = new ClassReader(bytes); 			// byte配列を読み込み、利用しやすい形にする。
			ClassWriter cw = new ClassWriter(cr, 2); 			// これのvisitを呼ぶことによって情報が溜まっていく。

			pack.Transform(cw, cr);

			// Writer内の情報をbyte配列にして返す。
			return cw.toByteArray();

		}
		catch(ClassCircularityError e)
		{
			return bytes;
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
			return bytes;
		}
	}

	//// util ////

	/**
	 * クラスの名前を難読化(obfuscation)する。
	 */
	public static String unmapClassName(String name) {
		return FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/', '.');
	}

	/**
	 * メソッドの名前を易読化(deobfuscation)する。
	 */
	public static String mapMethodName(String owner, String methodName, String desc) {
		return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(unmapClassName(owner), methodName, desc);
	}

	/**
	 * フィールドの名前を易読化(deobfuscation)する。
	 */
	public static String mapFieldName(String owner, String name, String desc) {
		return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(unmapClassName(owner), name, desc);
	}

	public static void log(String str)
	{
		FMLRelaunchLog.info(str);
	}
}
