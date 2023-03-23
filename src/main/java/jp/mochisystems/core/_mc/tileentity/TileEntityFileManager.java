package jp.mochisystems.core._mc.tileentity;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.blockcopier.BlocksScanner;
import jp.mochisystems.core.blockcopier.IBLockCopyHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;

public class TileEntityFileManager extends TileEntityBlocksScannerBase {

	public String reName = "";

	private final String FolderName = "\\BlockModel\\";

	
	public void FileWrite()
	{
		if(stackSlot ==null)return;
		if(!stackSlot.hasTagCompound())return;
		NBTTagCompound tag = stackSlot.getTagCompound();
		if(tag == null) return;
		NBTTagCompound model = tag.getCompoundTag("model");
		String outputFileName = model.getString("ModelName") + ".blockmodel";// + VersionToString();
		File outputFile = new File(Minecraft.getMinecraft().mcDataDir.getAbsolutePath()+FolderName+outputFileName);
		NBTTagCompound targetNbt = new NBTTagCompound();
		stackSlot.writeToNBT(targetNbt);
		try
		{
			if(!outputFile.exists())outputFile.createNewFile();
			CompressedStreamTools.write(targetNbt, outputFile);
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void FileRead()
	{
		JFileChooser fileChooser = new JFileChooser(Minecraft.getMinecraft().mcDataDir.getAbsolutePath()+FolderName);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Mochisystems block model file", "blockmodel");
		fileChooser.setFileFilter(filter);
		fileChooser.setCurrentDirectory(new File(FolderName));
		fileChooser.setFocusable(true);
		int selected = fileChooser.showOpenDialog(null);
		if (selected != JFileChooser.APPROVE_OPTION) return;
		
		NBTTagCompound tag = null;
		try 
		{
			File target = fileChooser.getSelectedFile();
			tag = CompressedStreamTools.read(target);
			if(tag == null)
			{
				Minecraft.getMinecraft().player.sendChatMessage(
					_Core.I18n("message.fileread.error")
				);
				return;
			}
			GetSender().SendNbtToServer(tag, getPos());
		}
		catch (Exception e)
		{
			Minecraft.getMinecraft().player.sendChatMessage(
					_Core.I18n("message.fileread.error")
			);
			e.printStackTrace();
		}
	}

	@Override
	protected BlocksScanner InstantiateBlocksCopier(IBLockCopyHandler handler) {
		return null;
	}

	@Override
	public ItemStack InstantiateModelItem() {
		return null;
	}

	public void OnCompleteReceive(NBTTagCompound nbt, EntityPlayer player) {

		stackSlot = new ItemStack(nbt);
		world.playSound(null,pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1.0F, 0.9F);

//		super.OnCompleteReceive(nbt);
	}

//	public void ReNameItemStack(String newName)
//	{
//		ItemStack is = getStackInSlot(0);
//		if(is.isEmpty())return;
//		String orgName = is.getItem().getItemStackDisplayName(is);
//		is.setStackDisplayName(orgName+" : "+newName);
//		if(is.hasTagCompound()) is.getTagCompound().setString("ModelName", newName);
//		reName = newName;
//	}

	//////////extends IInventry



	@Override
	public void setInventorySlotContents(int idx, ItemStack itemstack)
	{
		super.setInventorySlotContents(idx, itemstack);

        if(!itemstack.hasTagCompound()) return;
		NBTTagCompound nbt = itemstack.getTagCompound();
		if(!nbt.hasKey("model")) return;
		if(nbt.getCompoundTag("model").hasKey("ModelName"))
        	reName = nbt.getCompoundTag("model").getString("ModelName");
	}

	@Override
	public String getName()
	{
		return "container.mfw.fileManager";
	}

}