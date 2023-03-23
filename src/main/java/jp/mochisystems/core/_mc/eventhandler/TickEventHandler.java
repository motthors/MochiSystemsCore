package jp.mochisystems.core._mc.eventhandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TickEventHandler {

	private static List<Runnable> ServerTickPreRunners = new ArrayList<>();
	private static List<Runnable> ServerTickPostRunners = new ArrayList<>();
	private static List<Runnable> ClientTickPreRunners = new ArrayList<>();
	private static List<Runnable> ClientTickPostRunners = new ArrayList<>();
	private static List<Consumer<EntityPlayer>> PlayerTickPreRunner = new ArrayList<>();
	private static List<Consumer<EntityPlayer>> PlayerTickPostRunner = new ArrayList<>();
	private static List<Consumer<Float>> RenderTickPreRunner = new ArrayList<>();
	private static List<Consumer<Float>> RenderTickPostRunner = new ArrayList<>();
	public static void AddServerTickPreListener(Runnable runner)
	{
		ServerTickPreRunners.add(runner);
	}
	public static void AddServerTickPostListener(Runnable runner)
	{
		ServerTickPostRunners.add(runner);
	}
	public static void AddClientTickPreListener(Runnable runner)
	{
		ClientTickPreRunners.add(runner);
	}
	public static void AddClientTickPostListener(Runnable runner)
	{
		ClientTickPostRunners.add(runner);
	}
	public static void AddPlayerTickPreListener(Consumer<EntityPlayer> runner)
	{
		PlayerTickPreRunner.add(runner);
	}
	public static void AddPlayerTickPostListener(Consumer<EntityPlayer> runner)
	{
		PlayerTickPostRunner.add(runner);
	}
	public static void AddRenderTickPreListener(Consumer<Float> runner)
	{
		RenderTickPreRunner.add(runner);
	}
	public static void AddRenderTickPostListener(Consumer<Float> runner)
	{
		RenderTickPostRunner.add(runner);
	}

	private static int tickCounter = 0;
	public static int getTickCounter() {
		return tickCounter;
	}

	private static float partialTick;
	public static float getPartialTick(){return partialTick;}

	@SubscribeEvent
	public void onTickEvent(TickEvent.ServerTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START) {
            tickCounter++;
            for(Runnable runner : ServerTickPreRunners) runner.run();
		}
		else {
			for(Runnable runner : ServerTickPostRunners) runner.run();
		}
	}

	@SubscribeEvent
	public void onClientTickEvent(TickEvent.ClientTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START) {
			tickCounter++;
			for(Runnable runner : ClientTickPreRunners) runner.run();
		}
		else{
			for(Runnable runner : ClientTickPostRunners) runner.run();
		}
	}

	@SubscribeEvent
	public void onPlayerTickEvent(TickEvent.PlayerTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START) {
			for(Consumer<EntityPlayer> runner : PlayerTickPreRunner) runner.accept(event.player);
		}
		else{
			for(Consumer<EntityPlayer> runner : PlayerTickPostRunner) runner.accept(event.player);
		}
	}

	@SubscribeEvent
	public void onRenderTickEvent(TickEvent.RenderTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START) {
			partialTick = event.renderTickTime;
			for(Consumer<Float> runner : RenderTickPreRunner) runner.accept(event.renderTickTime);
		}
		else{
			for(Consumer<Float> runner : RenderTickPostRunner) runner.accept(event.renderTickTime);
		}
	}
}
