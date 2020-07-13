package mochisystems._mc.item;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mochisystems._core._Core;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import org.lwjgl.opengl.GL11;

public class ItemWrenchPlaceBlock extends Item {


	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		Block placedBlock = Blocks.dirt;
		Item placedBlockItem = Item.getItemFromBlock(placedBlock);

		boolean isCreativeMode = player.capabilities.isCreativeMode;
		if(!player.inventory.hasItem(placedBlockItem) && !isCreativeMode)return itemstack;

		double pit = Math.cos(Math.toRadians(player.rotationPitch));
		int x = (int) Math.floor(player.posX - Math.sin(Math.toRadians(player.rotationYaw))*3*pit);
		int y = (int) Math.floor(player.posY - Math.sin(Math.toRadians(player.rotationPitch))*3+player.eyeHeight);
		int z = (int) Math.floor(player.posZ + Math.cos(Math.toRadians(player.rotationYaw))*3*pit);

		if (!world.canPlaceEntityOnSide(placedBlock, x, y, z, false, x, player, itemstack))return itemstack;

		boolean canPlaceBlock = (world.getBlock(x, y, z) == Blocks.air) || (world.getBlock(x, y, z) == Blocks.water) || (world.getBlock(x, y, z) == Blocks.flowing_water);

		if(canPlaceBlock)
		{
			if(!isCreativeMode) player.inventory.consumeInventoryItem(placedBlockItem);
			world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), Blocks.dirt.stepSound.func_150496_b(), (Blocks.dirt.stepSound.getVolume() + 1.0F) / 2.0F, Blocks.dirt.stepSound.getPitch() * 0.8F);
//	    		PacketHandler.INSTANCE.sendToServer(new ERC_MessageItemWrenchSync(2,x,y,z));
			player.swingItem();
			if(!world.isRemote)
			{
				placeBlockAt(new ItemStack(_Core.ItemStick), player, player.worldObj, x, y, z, Blocks.dirt);
			}
		}
		return itemstack;
	}
	
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, Block block)
    {
		if( (world.getBlock(x, y, z) != Blocks.air) )return false;
    	if (!world.setBlock(x, y, z, block, 0, 0))
    	{
    		return false;
    	}
//    	ERC_Logger.info("place block");
    	if (world.getBlock(x, y, z) == block)
    	{
    		block.onBlockPlacedBy(world, x, y, z, player, stack);
    		block.onPostBlockPlaced(world, x, y, z, 0);
    		world.markBlockForUpdate(x, y, z);
    	}
    	return true;
    }
	
//	@Override
//	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
//			float hitX, float hitY, float hitZ)
//	{
//		Block placedBlock = Blocks.dirt;
//		Item placedBlockItem = Item.getItemFromBlock(placedBlock);
//
//		boolean iscreative = player.capabilities.isCreativeMode;
//		if(!player.inventory.hasItem(placedBlockItem) && !iscreative)return false;
//
//		double pit = Math.cos(Math.toRadians(player.rotationPitch));
//		x = (int) Math.floor(player.posX - Math.sin(Math.toRadians(player.rotationYaw))*2*pit);
//		y = (int) Math.floor(player.posY - Math.sin(Math.toRadians(player.rotationPitch))*2);
//		z = (int) Math.floor(player.posZ + Math.cos(Math.toRadians(player.rotationYaw))*2*pit);
//
//		if (!world.canPlaceEntityOnSide(placedBlock, x, y, z, false, x, player, player.getHeldItem()))return false;
//
//		// �u���b�N��ݒu�ł��邩�`�F�b�N
//		boolean canPlaceBlock = (world.getBlock(x, y, z) == Blocks.air) || (world.getBlock(x, y, z) == Blocks.water) || (world.getBlock(x, y, z) == Blocks.flowing_water);
//
//		if(canPlaceBlock)
//		{
//    		if(!iscreative)player.inventory.consumeInventoryItem(placedBlockItem);
//    		world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), Blocks.dirt.stepSound.func_150496_b(), (Blocks.dirt.stepSound.getVolume() + 1.0F) / 2.0F, Blocks.dirt.stepSound.getPitch() * 0.8F);
//
//    		ERC_PacketHandler.INSTANCE.sendToServer(new ERC_MessageItemWrenchSync(2,x,y,z));
//		}
//        return true;
//	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ)
	{
		return false;
	}

	@SubscribeEvent
	public void DrawPlacePoint(DrawBlockHighlightEvent event)
	{
		if(event.currentItem != null && event.currentItem.getItem() instanceof ItemWrenchPlaceBlock)
		{
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
			GL11.glLineWidth(2.0F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(false);

			EntityPlayer player = event.player;
			Block block = Blocks.dirt;
			double f1 = 0.002f;
			double pit = Math.cos(Math.toRadians(player.rotationPitch));
			int x = (int) Math.floor(player.posX - Math.sin(Math.toRadians(player.rotationYaw))*3*pit);
			int y = (int) Math.floor(player.posY - Math.sin(Math.toRadians(player.rotationPitch))*3+player.eyeHeight);
			int z = (int) Math.floor(player.posZ + Math.cos(Math.toRadians(player.rotationYaw))*3*pit);
			double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)event.partialTicks;
			double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)event.partialTicks;
			double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)event.partialTicks;
			block.setBlockBoundsBasedOnState(player.worldObj, x, y, z);
			RenderGlobal.drawOutlinedBoundingBox(
					block.getSelectedBoundingBoxFromPool(player.worldObj, x, y, z)
							.expand(f1, f1, f1)
							.getOffsetBoundingBox(-d0, -d1, -d2),
					-1);
			event.setCanceled(true);

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}


	IIcon temIcon;

	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister p_94581_1_)
    {
		itemIcon = p_94581_1_.registerIcon(_Core.MODID+":"+"wrench_p");
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int p_77617_1_) 
	{
		return itemIcon;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) 
	{
		return itemIcon;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) 
	{
		return itemIcon;
	}
    
}
