package jp.mochisystems.core._mc.item;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.blockcopier.*;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class itemBlockBlockModelTester extends ItemBlock implements IItemBlockModelHolder {

	public itemBlockBlockModelTester(Block block)
	{
		super(block);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world,
								BlockPos pos, EnumFacing side,
								float hitX, float hitY, float hitZ,
								IBlockState newState)
	{
		if(stack == null) return false;
		if(!(stack.getItem() instanceof itemBlockBlockModelTester))return false;
		if(!stack.hasTagCompound()) return false;
		NBTTagCompound tag = stack.getTagCompound();
		if(!stack.hasTagCompound())
		{
			return true;
		}
		return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);
		NBTTagCompound tag = stack.getTagCompound();

		if(player.isSneaking())
		{
			CopyAround(pos, stack);
			return EnumActionResult.SUCCESS;
		}

		if(!stack.hasTagCompound())
		{
			if(world.isRemote)
			{
				Minecraft.getMinecraft().player.sendChatMessage(
						_Core.I18n("message.remotecon.notag")
				);
			}
			return EnumActionResult.FAIL;
		}

		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	private void CopyAround(BlockPos pos, ItemStack stack)
	{
		BlocksScanner scanner = new BlocksScanner(null);
		IBLockCopyHandler handler = new IBLockCopyHandler() {
			public BlockPos GetHandlerPos() {
				return pos;
			}
			public EnumFacing GetSide() {
				return null;
			}
			public boolean isExceptBlock(IBlockState b) {
				return false;
			}
			public IBlockState GetBlockState(BlockPos pos) {
				return Minecraft.getMinecraft().world.getBlockState(pos);
			}
			public TileEntity getTileEntity(BlockPos pos) {
				return Minecraft.getMinecraft().world.getTileEntity(pos);
			}
			public List<Entity> getEntities(float mx, float my, float mz, float xx, float xy, float xz) {
				return null;
			}
			public Vec3d GetOriginLocalOffset() {return Vec3d.Zero;}
			public void registerExternalParam(NBTTagCompound model, NBTTagCompound nbt) {

			}
			public BlocksScanner GetScanner() {
				return scanner;
			}
			public BlockModelSender GetSender() {
				return null;
			}
			public void ReceivePartial(int idx, int total, byte[] bytes) {
			}
			public void OnCompleteReceive(NBTTagCompound nbt, EntityPlayer player) {

			}
		};
		LimitFrame limitFrame = new LimitFrame();
		limitFrame.SetLengths(-3, -3, -3, 3, 3, 3);
		Logger.info("start");
		scanner.RegisterSettings(pos.getX(), pos.getY(), pos.getZ(), limitFrame, false, true);
		scanner.StartParallelScan();

		stack.setTagCompound(scanner.GetTag());
		Logger.info("end");
	}

	@Override
	public IModel GetBlockModel(IModelController controller) {
		return new DefBlockModel(controller);
	}

}
