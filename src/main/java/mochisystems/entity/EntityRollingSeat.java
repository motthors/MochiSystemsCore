package mochisystems.entity;

import mochisystems.math.Math;
import mochisystems.math.Quaternion;
import mochisystems.math.Vec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityRollingSeat extends Entity {

	AxisAlignedBB absoluteAABB;
	int counterForSearchParent = 0;

    private final Vec3d offset = new Vec3d();
    private final Vec3d eyePos = new Vec3d();
	private final Quaternion rotation = new Quaternion();
	public Quaternion GetRotation() { return rotation; }

//	Vec3d vx = new Vec3d(0, 0, 0);
//	Vec3d vy = new Vec3d(0, 0, 0);
//	Vec3d vz = new Vec3d(0, 0, 0);
//	public float rotationRoll;
//	public float prevRotationRoll;

//	//for render
//	public double renderroty, renderrotyPrev;
//	public double renderrotp, renderrotpPrev;
//	public double renderrotr, renderrotrPrev;

	public EntityRollingSeat(World world)
	{
		super(world);
	}

	public EntityRollingSeat(World world, double posX, double posY, double posZ, Vec3d offset)
	{
		super(world);
		setSize(1.0f, 1.0f);
		absoluteAABB = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
//		setSeatHeight(-0.3f);
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.offset.CopyFrom(offset);
//		setSeatAngle(seatAngle);
//		setMiscData(parent.xCoord, parent.yCoord, parent.zCoord, parent.getTreeIndexOf(parent));
//		setPosOffset(cpx, cpy, cpz);
//		setVecViewFromMeta(seatAngle, parent);
	}

	@Override
	public double getMountedYOffset()
    {
		return 0;
    }

	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int p_70056_9_)
    {
	}

	//
	@Override
	public boolean interactFirst(EntityPlayer player)
    {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player)
        {
            return true;
        }
        else if (this.riddenByEntity != null && this.riddenByEntity != player)
        {
        	riddenByEntity.mountEntity(null);
        	riddenByEntity = null;
            return true;
        }
        else if (this.riddenByEntity != null)
        {
        	return true;
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
                player.mountEntity(this);
            }
            return true;
        }
    }

	@Override
	protected void entityInit()
	{
		dataWatcher.addObject(11, new Integer(0));	// seatmeta
	}


//	public void setPosition(Vector3)
//	{
//		float size = tile.wheelSize.get();
//		posInOut.xCoord *= size;
//		posInOut.yCoord *= size;
//		posInOut.zCoord *= size;
//		MFW_Math.rotateAroundVector(posInOut,
//				tile.rotvecConst_meta2.x, tile.rotvecConst_meta2.y, tile.rotvecConst_meta2.z,
//				Math.toRadians(-tile.rotConst_meta2));
//
//		MFW_Math.rotateAroundVector(posInOut, 0, 0, 1, Math.toRadians(-tile.rotation.get()));
//
//		MFW_Math.rotateAroundVector(posInOut, 0, 1, 0, Math.toRadians(-tile.rotVar2));
//		MFW_Math.rotateAroundVector(posInOut, 1, 0, 0, Math.toRadians(-tile.rotVar1));
//
//		MFW_Math.rotateAroundVector(posInOut,
//				tile.rotvecMeta2_side.x, tile.rotvecMeta2_side.y, tile.rotvecMeta2_side.z,
//				Math.toRadians(-tile.rotMeta2_side));
//
//
//		TileEntityFerrisWheel nextparent = tile.getParentTile();
//		if(nextparent != null)
//		{
//			posInOut.xCoord += tile.ConnectPos.x;// * nextparent.wheelSize;
//			posInOut.yCoord += tile.ConnectPos.y;// * nextparent.wheelSize;
//			posInOut.zCoord += tile.ConnectPos.z;// * nextparent.wheelSize;
//			setPositionToRoot(nextparent, posInOut);
//		}
//	}

	protected void SetPosition(Vec3d pos, Quaternion rotation)
    {
    	prevPosX = posX;
    	prevPosY = posY;
    	prevPosZ = posZ;
    	this.rotation.CopyFrom(rotation);
        posX = pos.x;
        posY = pos.y;
        posZ = pos.z;

//    	setPositionToRoot(parenttile, pos);
//    	this.posX = pos.xCoord/**parentTile.wheelSize + parentTile.posX */+ getBaseX() +0.5;// + getOffsetX();
//		this.posY = pos.yCoord/**parentTile.wheelSize + parentTile.posY */+ getBaseY() +0.5;// + getOffsetY();
//		this.posZ = pos.zCoord/**parentTile.wheelSize + parentTile.posZ */+ getBaseZ() +0.5;// + getOffsetZ();
//
//        double wx = 0.5;//(absoluteAABB.minX+absoluteAABB.maxX)/2;
//        double wz = 0.5;//(absoluteAABB.minZ+absoluteAABB.maxZ)/2;
//        double hu = 0.5;//this.absoluteAABB.maxY;
////        double hd = 0.0;//this.absoluteAABB.minY;
//        this.boundingBox.setBounds(posX-wx, posY-hu, posZ-wz, posX+wx, posY+hu, posZ+wz);
//
////        if(worldObj.isRemote)MFW_Logger.debugInfo("seat setPosition y:"+posY);
////    	if(waitUpdateRiderFlag)super.updateRiderPosition();
////        if(riddenByEntity!=null)updateRiderPosition();
    }

	protected boolean CheckParent()
	{
		return false;
	}

	@Override
	public void setDead()
	{
		//hock
//		MFW_Logger.debugInfo("dead");
		super.setDead();
	}

	@Override
	public void onUpdate()
	{
        if(!CheckParent())
        {
            counterForSearchParent+=1;
            if(counterForSearchParent>=100)
            {
                setDead();
            }
            return;
        }
//    	if(parenttile.isInvalid())
//    	{
//    		setDead();
//    		return;
//    	}
//    	super.onUpdate();
//    	setPosition();
	}


//	public void setVecViewFromMeta(int seatAngle, TileEntityFerrisWheel parenttile)
//	{
//		vx = new Vec3d(-1, 0.001, 0.001);
//		vy = new Vec3d(-0.001, 1, -0.001);
//		vz = new Vec3d(0.0001, 0.001, -1);
//
//		double rot = Math.toRadians(seatAngle);
//		vx = Math.rotateAroundVector(vx, vy, rot);
//		vz = Math.rotateAroundVector(vz, vy, rot);
//	}

//	public void transformVec3ToRootTile(TileEntityFerrisWheel tile, Vec3d vx, Vec3d vy, Vec3d vz)
//	{
//		double drot;
//		drot = -Math.toRadians(tile.rotConst_meta2);
//		Vec3d _vx = Math.rotateAroundVector(vx, tile.rotvecConst_meta2, drot);
//		Vec3d _vy = Math.rotateAroundVector(vy, tile.rotvecConst_meta2, drot);
//		Vec3d _vz = Math.rotateAroundVector(vz, tile.rotvecConst_meta2, drot);
//
//		drot = Math.toRadians(tile.rotation.get());// - parenttile.prevRotation;
//		Vec3d vecrot = new Vec3d(0, 0, -1);
//		_vx = Math.rotateAroundVector(_vx, vecrot, drot);
//		_vy = Math.rotateAroundVector(_vy, vecrot, drot);
//		_vz = Math.rotateAroundVector(_vz, vecrot, drot);
//
//		drot = Math.toRadians(tile.rotVar2);// - parenttile.prevRotation;
//		vecrot.x = 0; vecrot.y = -1; vecrot.z = 0;
//		_vx = Math.rotateAroundVector(_vx, vecrot, drot);
//		_vy = Math.rotateAroundVector(_vy, vecrot, drot);
//		_vz = Math.rotateAroundVector(_vz, vecrot, drot);
//		drot = Math.toRadians(tile.rotVar1);// - parenttile.prevRotation;
//		vecrot.x = -1; vecrot.y = 0; vecrot.z = 0;
//		_vx = Math.rotateAroundVector(_vx, vecrot, drot);
//		_vy = Math.rotateAroundVector(_vy, vecrot, drot);
//		_vz = Math.rotateAroundVector(_vz, vecrot, drot);
//
//		drot = -Math.toRadians(tile.rotMeta2_side);
//		_vx = Math.rotateAroundVector(_vx, tile.rotvecMeta2_side, drot);
//		_vy = Math.rotateAroundVector(_vy, tile.rotvecMeta2_side, drot);
//		_vz = Math.rotateAroundVector(_vz, tile.rotvecMeta2_side, drot);
//
//		vx.x = _vx.x; vx.y = _vx.y; vx.z = _vx.z;
//		vy.x = _vy.x; vy.y = _vy.y; vy.z = _vy.z;
//		vz.x = _vz.x; vz.y = _vz.y; vz.z = _vz.z;
//		if(tile.getParentTile() != null)
//		{
//			transformVec3ToRootTile(tile.getParentTile(), vx, vy, vz);
//		}
//	}


	@Override
	public void updateRiderPosition()
	{
//		if(parenttile==null)return;
    	if (this.riddenByEntity != null)
        {
            // 方針：データ上のEntityは常にX軸の方向を向いたままにする
            // その上でRenderEntityEventで指定の角度に回転させる

            eyePos.CopyFrom(offset).Rotate(rotation);


//    		waitUpdateRiderFlag = false; // TODO これなに
    		// �����]
//    		Vec3d vx = new Vec3d(this.vx.x, this.vx.y, this.vx.z);
//    		Vec3d vy = new Vec3d(this.vy.x, this.vy.y, this.vy.z);
//    		Vec3d vz = new Vec3d(this.vz.x, this.vz.y, this.vz.z);
//    		transformVec3ToRootTile(parenttile, vx, vy, vz);
//    		vz = vx.crossProduct(vy);


    		prevRotationYaw = Math.fixrot(rotationYaw, prevRotationYaw);
    		prevRotationPitch = Math.fixrot(rotationPitch, prevRotationPitch);
//    		prevRotationRoll = Math.fixrot(rotationRoll, prevRotationRoll);

    		this.riddenByEntity.rotationYaw = 0;
    		this.riddenByEntity.rotationPitch = 0;
    		this.riddenByEntity.prevRotationYaw = 0;
    		this.riddenByEntity.prevRotationPitch = 0;

    		double toffsety = 0.3 + this.riddenByEntity.getYOffset();

    		this.riddenByEntity.setPosition(
    				this.posX + eyePos.y,
    				this.posY + eyePos.y,
    				this.posZ + eyePos.z);


//            if(worldObj.isRemote)
//            {
//            	renderrotyPrev = renderroty;
//            	renderrotpPrev = renderrotp;
//            	renderrotrPrev = renderrotr;
//            	renderroty = -Math.toDegrees( java.lang.Math.atan2(vz.x, vz.z) );
//            	renderrotp = 0;//TODO Math.toDegrees( Math.angleTwoVec3(vz, Vec3.createVectorHelper(vz.x, 0, vz.z)) * (vz.y>=0?-1f:1f) );
//            	renderrotr = 0;//TODO Math.toDegrees( Math.angleTwoVec3(vx, Vec3.createVectorHelper(0, 1, 0).crossProduct(vz)) * (vx.yCoord>=0?1f:-1f) );
////            	MFW_Logger.debugInfo("seatex : "+renderrotr);
//            	EntityLivingBase el = (EntityLivingBase) this.riddenByEntity;
//            	el.renderYawOffset = (float) renderroty;
//            	if(riddenByEntity == Minecraft.getMinecraft().thePlayer){
////            		TODO el.rotationYawHead = ERC_CoasterAndRailManager.rotationViewYaw + el.renderYawOffset;
//            	}
//// el.head
        }
//    	ERC_CoasterAndRailManager.setRotRoll(rotationRoll, prevRotationRoll);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
//		setBasePos(nbt.getInteger("tilex"),nbt.getInteger("tiley"),nbt.getInteger("tilez"));
//		setSeatAngle(nbt.getInteger("seatmeta"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
//		nbt.setInteger("tilex", getBaseX());
//		nbt.setInteger("tiley", getBaseY());
//		nbt.setInteger("tilez", getBaseZ());
//		nbt.setInteger("seatmeta", getSeatAngle());
	}

//	public void setSeatAngle(int d){dataWatcher.updateObject(11, Integer.valueOf(d));}
//	public int getSeatAngle(){return dataWatcher.getWatchableObjectInt(11);}
}
