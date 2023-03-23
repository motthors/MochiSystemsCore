package jp.mochisystems.core._mc.entity;

import jp.mochisystems.core.util.Connector;
import jp.mochisystems.core.util.IBlockModel;
import jp.mochisystems.core.util.IModelController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EntityCollisionParts extends Entity {

	public final Connector connector;
	private final IModelController controller;

	public EntityCollisionParts(World world) {
		super(world); // kokoni kitara yaba
		connector=null;
		controller=null;
	}

	public EntityCollisionParts(World world, Connector connector, IModelController controller, AxisAlignedBB aabb) {
		super(world);
		this.connector = connector;
		this.controller = controller;
		SetScale(1f);
	}

	public void SetScale(float scale)
	{
		setSize(scale, scale);
	}

	public void SetPosition(IBlockModel target)
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		setPosition(
				controller.CorePosX() + connector.Current().x,
				controller.CorePosY() + connector.Current().y,
				controller.CorePosZ() + connector.Current().z);
		target.UpdateChildConnector(connector);
	}

	@Override
	public void setPosition(double x, double y, double z)
	{
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		double w = this.width * 0.5;
		double h = this.height * 0.5;
		super.setEntityBoundingBox(new MovingAABB(this, x-w, y-h, z-w, x+w, y+h, z+w));
	}


	public boolean MoveCollidedEntities(Entity entity, double x, double y, double z)
	{
//		List<Entity> entities = this.world.getEntitiesInAABBexcluding(this,
//				this.getEntityBoundingBox()
////						.expand(0, 0.2, 0)
////						.offset(posX-prevPosX, posY-prevPosY, posZ-prevPosZ),
//				e -> !(e instanceof EntityCollisionParts || e instanceof EntityBlockModelCollider));
//		for (Entity entity : entities){
			if(this.getEntityBoundingBox().expand(0, 0.5, 0).offset(posX-prevPosX, posY-prevPosY, posZ-prevPosZ)
					.intersects(entity.getEntityBoundingBox())){
				if(!entity.updateBlocked) {
					entity.updateBlocked = true;
					x = connector.Current().x - connector.Prev().x;
					y = connector.Current().y - connector.Prev().y;
					z = connector.Current().z - connector.Prev().z;
				}
				y = entity.getEntityBoundingBox().calculateYOffset(this.getEntityBoundingBox(), y);
				if (y < 0) y = 0;
				if (y != 0.0) entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0, y, 0));
				x = entity.getEntityBoundingBox().calculateXOffset(this.getEntityBoundingBox(), x);
				if (x != 0.0) entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(x, 0, 0));
				z = entity.getEntityBoundingBox().calculateZOffset(this.getEntityBoundingBox(), z);
				if (z != 0.0) entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0, 0, z));
				return true;
			}
			return false;
//		}
	}


	public boolean canBeCollidedWith()
    {
        return true;
    }


	@Override
	public boolean processInitialInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand)
	{
//		if(parent==null)return true;
//		if(!player.isSneaking())return true;
//		parent.openRootCoreGUI(player);
		return true;
	}

	@Override
	public void setEntityBoundingBox(AxisAlignedBB bb)
	{
		super.setEntityBoundingBox(new MovingAABB(this, bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ));
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox()
	{
		return getEntityBoundingBox();
	}

	@Override
	protected void readEntityFromNBT(@Nonnull NBTTagCompound nbt) {}

	@Override
	protected void writeEntityToNBT(@Nonnull NBTTagCompound nbt) {}

	@Override
	protected void entityInit() {}

}
