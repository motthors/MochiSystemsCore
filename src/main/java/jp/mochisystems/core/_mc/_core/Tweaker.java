//package jp.mochisystems.core._mc._core;
//
//import net.minecraft.launchwrapper.ITweaker;
//import net.minecraft.launchwrapper.Launch;
//import net.minecraft.launchwrapper.LaunchClassLoader;
//import org.spongepowered.asm.launch.MixinBootstrap;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Tweaker implements ITweaker {
//
//    private static boolean setupTransformer = false;
//
//    private List<String> args;
//    public Tweaker() {
//        MixinBootstrap.init();
//    }
//
//    @Override
//    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
//        (this.args = new ArrayList<>()).addAll(args);
//        addArg("gameDir", gameDir);
//        addArg("assetsDir", assetsDir);
//        addArg("version", profile);
//    }
//
//    @Override
//    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
//        doInitialSetup(classLoader);
//    }
//
//    @Override
//    public String getLaunchTarget() {
//        return "net.minecraft.client.main.Main";
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public String[] getLaunchArguments() {
//        // Parse the arguments that we are able to pass to the game
//        List<Argument> parsed = Arguments.parse(this.args);
//
//        // Parse the arguments that are already being passed to the game
//        List<Argument> existing = Arguments.parse((List<String>) Launch.blackboard.get("ArgumentList"));
//
//        // Remove any arguments that conflict with existing ones
//        parsed.removeIf(argument -> existing.stream().anyMatch(a -> a.conflicts(argument)));
//
//        // Join back the filtered arguments and pass those to the game
//        return Arguments.join(parsed).toArray(new String[0]);
//    }
//
//    private void addArg(String label, File file) {
//        if (file != null)
//            addArg(label, file.getAbsolutePath());
//    }
//
//    private void addArg(String label, String value) {
//        if (!args.contains("--" + label) && value != null) {
//            this.args.add("--" + label);
//            this.args.add(value);
//        }
//    }
//
//    private static void doInitialSetup(LaunchClassLoader classLoader) {
//        if (!setupTransformer) {
//            classLoader.addClassLoaderExclusion("io.github.impactdevelopment.simpletweaker.");
//            classLoader.registerTransformer(SimpleTransformer.class.getName());
//            setupTransformer = true;
//        }
//    }
//}
