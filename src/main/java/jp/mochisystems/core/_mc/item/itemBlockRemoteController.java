package jp.mochisystems.core._mc.item;

import jp.mochisystems.core._mc._core._Core;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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

import static jp.mochisystems.core._mc.block.BlockRemoteController.*;

public class itemBlockRemoteController extends ItemMultiTab._ItemBlock {

	public itemBlockRemoteController(Block block) 
	{
		super(block);
	}


    @Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    	NBTTagCompound nbt = stack.getTagCompound();
        if(stack.hasTagCompound()) {
			tooltip.add("position : "
					+ nbt.getInteger(KeyRemotePosX) + "."
					+ nbt.getInteger(KeyRemotePosY) + "."
					+ nbt.getInteger(KeyRemotePosZ));
			tooltip.add("");
		}
		String[] usages = _Core.I18n("usage.remote").split("\\\\ ");
		for(String s : usages){
			tooltip.add(TextFormatting.AQUA+s);
		}
    }

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world,
								BlockPos pos, EnumFacing side,
								float hitX, float hitY, float hitZ,
								IBlockState newState)
	{
		if(stack == null) return false;
		if(!(stack.getItem() instanceof itemBlockRemoteController))return false;
		if(!stack.hasTagCompound()) return false;
		NBTTagCompound tag = stack.getTagCompound();
		if(tag.hasKey(KeyRemotePosX))
		{
			return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
		}
		return true;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(player.isSneaking())
		{
			return Success_RegisterPositionAnd(world, player.getHeldItem(hand), pos);
		}

		NBTTagCompound tag = player.getHeldItem(hand).getTagCompound();
		boolean hasNoTag = tag == null || !tag.hasKey(KeyRemotePosX);

		if(hasNoTag)
		{
			if(world.isRemote)
			{
				Minecraft.getMinecraft().player.sendChatMessage(
						_Core.I18n("message.remotecon.notag")
				);
			}
			return EnumActionResult.FAIL;
		}

		int x = tag.getInteger(KeyRemotePosX);
		int y = tag.getInteger(KeyRemotePosY);
		int z = tag.getInteger(KeyRemotePosZ);
		boolean isPosSameAtRemote = pos.equals(new BlockPos(x, y, z));
		if(isPosSameAtRemote)
		{
			Minecraft.getMinecraft().player.sendChatMessage(
					_Core.I18n("message.remotecon.samepos")
			);
			return EnumActionResult.FAIL;
		}

		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	private EnumActionResult Success_RegisterPositionAnd(World world, ItemStack stack, BlockPos pos)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(KeyRemotePosX, pos.getX());
		tag.setInteger(KeyRemotePosY, pos.getY());
		tag.setInteger(KeyRemotePosZ, pos.getZ());
		if(world.isRemote) {
			Minecraft.getMinecraft().player.sendChatMessage(
					"save position : " + pos.toString()
			);
		}
		stack.setTagCompound(tag);
		return EnumActionResult.SUCCESS;
	}



	float motion = 0;
	@SubscribeEvent
	public void DrawPlacePoint(DrawBlockHighlightEvent event)
	{
		EntityPlayer player = event.getPlayer();
		ItemStack stack = player.getHeldItemMainhand();

		if(!stack.isEmpty() && stack.getItem() instanceof itemBlockRemoteController) {

			motion += 0.04f;
			if(motion > 1f) motion -= 2f;
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			GlStateManager.disableAlpha();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);

			float t = event.getPartialTicks();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();

			GlStateManager.pushMatrix();
			double yaw = Math.toRadians(player.rotationYaw);
			double pit = Math.toRadians(player.rotationPitch);
			double ix = Math.sin(-yaw) * Math.cos(pit);
			double iy = player.eyeHeight - Math.sin(pit);
			double iz = Math.cos(-yaw) * Math.cos(pit);

			NBTTagCompound tag = stack.getTagCompound();
			boolean hasNoTag = tag == null || !tag.hasKey(KeyRemotePosX);

			if(!hasNoTag)
			{
				double x = 0.5 + tag.getInteger(KeyRemotePosX) - (player.prevPosX + (player.posX-player.prevPosX) * t);
				double y = 0.5 + tag.getInteger(KeyRemotePosY) - (player.prevPosY + (player.posY-player.prevPosY) * t);
				double z = 0.5 + tag.getInteger(KeyRemotePosZ) - (player.prevPosZ + (player.posZ-player.prevPosZ) * t);
				GL11.glLineWidth(4f);
				bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
				bufferbuilder.pos(x, y, z).color(255, 0, 0, 255).endVertex();
				bufferbuilder.pos(ix, iy, iz).color(255, 0, 0, 255).endVertex();
				tessellator.draw();

				GL11.glLineWidth(1.5f);
				bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
				for(int i = 0; i < 8; ++i)
				{
					double m = jp.mochisystems.core.math.Math.Clamp((motion+i)/6, 0, 1);
					double tx = x + (ix-x)*m;
					double ty = y + (iy-y)*m;
					double tz = z + (iz-z)*m;
					bufferbuilder.pos(tx, ty, tz).color(255, 255, 255, 255).endVertex();
				}
				tessellator.draw();
			}

			GlStateManager.enableAlpha();
			GlStateManager.popMatrix();
			GL11.glLineWidth(1.0F);
			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
		}
	}

}
