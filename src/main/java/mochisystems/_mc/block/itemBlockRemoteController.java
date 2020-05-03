package mochisystems._mc.block;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class itemBlockRemoteController extends ItemBlock{

	public itemBlockRemoteController(Block block) 
	{
		super(block);
	}

//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Override
//    public void getSubItems(Item item, CreativeTabs tab, List itemList)
//	{
//        ItemStack itemStack = new ItemStack(this, 1, 0);
//
//        NBTTagCompound nbt = new NBTTagCompound();
//		nbt.setInteger("mfwremotecontroller", 42);
//		itemStack.setTagCompound(nbt);
//
//        itemList.add(itemStack);
//    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean advanced)
    {
        if(itemStack.stackTagCompound == null)return;
        list.add("position : "
                +itemStack.stackTagCompound.getString("mfwcontrollerX") + "."
                +itemStack.stackTagCompound.getString("mfwcontrollerY") + "."
                +itemStack.stackTagCompound.getString("mfwcontrollerZ"));
    }

//	@Override
//    public void onCreated(ItemStack itemStack, World world, EntityPlayer player)
//	{
//        NBTTagCompound nbt = new NBTTagCompound();
//		nbt.setInteger("mfwremotecontroller", 42);
//		itemStack.setTagCompound(nbt);
//    }

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ, int metadata) 
	{
		ItemStack is = player.getHeldItem();
		if(is == null)return false;
		if(!(is.getItem() instanceof itemBlockRemoteController))return false;
		if(!is.hasTagCompound())return false;
		NBTTagCompound tag = is.getTagCompound();
		if(tag.hasKey("mfwcontrollerX"))
		{
			return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		}
		return true;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                             int side, float xOffset, float yOffset, float zOffset)
	{
		NBTTagCompound tag = stack.getTagCompound();
        if(player.isSneaking())
        {
            onBlockActivatedWithController(player, x, y, z);
            if(world.isRemote)
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
                    "save position : "+ x +"." +y +"." +z
            ));
            return true;
        }
        else if(tag == null || !tag.hasKey("mfwcontrollerX"))
		{
			if(world.isRemote)
			{
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
	       				StatCollector.translateToLocal("message.remotecon.notag")
       				));
			}
			return true;
		}

		
		return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
	}

	public boolean onBlockActivatedWithController(EntityPlayer player, int x, int y, int z)
	{
		ItemStack is = player.getHeldItem();
		if(is == null)return false;
		if(!(is.getItem() instanceof itemBlockRemoteController))return false;
		if(!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = is.getTagCompound();
		tag.setInteger("mfwcontrollerX", x);
		tag.setInteger("mfwcontrollerY", y);
		tag.setInteger("mfwcontrollerZ", z);
		return true;
	}

//	public static void RegisterCore(ItemStack itemStack, FerrisPartBase part)
//	{
//		if(!itemStack.hasTagCompound()) return;
//		NBTTagCompound nbt = itemStack.getTagCompound();
//		if(nbt.hasKey("mfrremotecontroller"))
//		{
//			nbt.setInteger("mfwcontrollerX", part.controller.CorePosX());
//			nbt.setInteger("mfwcontrollerY", part.controller.CorePosY());
//			nbt.setInteger("mfwcontrollerZ", part.controller.CorePosZ());
//		}
//	}
}
