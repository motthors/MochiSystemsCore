package jp.mochisystems.core._mc.item;

import jp.mochisystems.core._mc._core._Core;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPlacingBlockStick extends ItemMultiTab._Item {

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		String[] usages = _Core.I18n("usage.stick").split("\\\\ ");
		for(String s : usages){
			tooltip.add(TextFormatting.AQUA+s);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		Block placedBlock = Blocks.DIRT;
		Item placedBlockItem = Item.getItemFromBlock(placedBlock);

		boolean isCreativeMode = player.capabilities.isCreativeMode;
		if(!player.inventory.hasItemStack(new ItemStack(placedBlockItem)) && !isCreativeMode)
			return super.onItemRightClick(world, player, hand);

		double pit = Math.cos(Math.toRadians(player.rotationPitch));
		int x = (int) Math.floor(player.posX - Math.sin(Math.toRadians(player.rotationYaw))*3*pit);
		int y = (int) Math.floor(player.posY - Math.sin(Math.toRadians(player.rotationPitch))*3+player.eyeHeight);
		int z = (int) Math.floor(player.posZ + Math.cos(Math.toRadians(player.rotationYaw))*3*pit);
		BlockPos pos = new BlockPos(x, y, z);
//		if (!world.canPlaceEntityOnSide(placedBlock, x, y, z, false, x, player, itemstack))return super.onItemRightClick(world, player, hand);

		Block existingBlock = world.getBlockState(pos).getBlock();
		boolean canPlaceBlock = (world.isAirBlock(pos)) || (existingBlock == Blocks.WATER) || (existingBlock == Blocks.FLOWING_WATER);

		if(canPlaceBlock)
		{
			if(!isCreativeMode)
			{
				findBlock(player).shrink(1);
			}
			world.playSound(null, x+0.5, y+0.5, z+0.5, SoundEvents.BLOCK_GRASS_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
//	    		PacketHandler.INSTANCE.sendToServer(new ERC_MessageItemWrenchSync(2,x,y,z));
			player.swingArm(hand);
			if(!world.isRemote)
			{
				world.setBlockState(pos, Blocks.DIRT.getDefaultState());
//				placeBlockAt(new ItemStack(_Core.ItemStick), player, player.worldObj, pos, Blocks.dirt);
			}
		}
		return super.onItemRightClick(world, player, hand);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void DrawPlacePoint(DrawBlockHighlightEvent event)
	{
		ItemStack stack = event.getPlayer().getHeldItemMainhand();
		if(!stack.isEmpty() && stack.getItem() instanceof ItemPlacingBlockStick)
		{
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
			GL11.glLineWidth(2.0F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(false);

			EntityPlayer player = event.getPlayer();
			Block block = Blocks.DIRT;
			double f1 = 0.002f;
			double pit = Math.cos(Math.toRadians(player.rotationPitch));
			int x = (int) Math.floor(player.posX - Math.sin(Math.toRadians(player.rotationYaw))*3*pit);
			int y = (int) Math.floor(player.posY - Math.sin(Math.toRadians(player.rotationPitch))*3+player.eyeHeight);
			int z = (int) Math.floor(player.posZ + Math.cos(Math.toRadians(player.rotationYaw))*3*pit);
			double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)event.getPartialTicks();
			double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)event.getPartialTicks();
			double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)event.getPartialTicks();
//			block.setBlockBoundsBasedOnState(player.worldObj, x, y, z);
			net.minecraft.client.renderer.RenderGlobal.drawSelectionBoundingBox(
					new AxisAlignedBB(x, y, z, x+1, y+1, z+1)
							.expand(f1, f1, f1)
							.offset(-d0, -d1, -d2),
					0, 0, 0, 0.4f);
			event.setCanceled(true);

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}

	private ItemStack findBlock(EntityPlayer player)
	{
		for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
		{
			ItemStack itemstack = player.inventory.getStackInSlot(i);

			if (itemstack.getItem() instanceof ItemBlock && ((ItemBlock)itemstack.getItem()).getBlock() instanceof BlockDirt)
			{
				return itemstack;
			}
		}

		return ItemStack.EMPTY;
	}

}
