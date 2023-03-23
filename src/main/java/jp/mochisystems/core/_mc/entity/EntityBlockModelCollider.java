package jp.mochisystems.core._mc.entity;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc.block.BlockSeatPositionMarker;
import jp.mochisystems.core._mc.eventhandler.TickEventHandler;
import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import jp.mochisystems.core.blockcopier.IModelCollider;
import jp.mochisystems.core.manager.RollingSeatManager;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.*;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityBlockModelCollider extends Entity implements IModelCollider, IRollSeat /*, ILateUpdater*/ {

	private final CommonAddress parentAddress = new CommonAddress();
	private final Connector connector = new Connector("");
	private int partialPointX;
	private int partialPointY;
	private int partialPointZ;
	public IBlockModel target;
	private IModelController controller;
	private EntityCollisionParts[] partsArray = new EntityCollisionParts[]{};
	static int reservedRidingSeatIndex;
	Entity[] passengerSeats = new Entity[0];
	Quaternion[] seatRot = new Quaternion[0];
	Quaternion attitude, prevAttitude;

	private int deadCount;

	public EntityBlockModelCollider(World world) {
		super(world);
		this.setSize(5f*1.732f, 5f*1.732f);
		this.preventEntitySpawning = true;
		setRenderDistanceWeight(Double.MAX_VALUE);
		attitude = new Quaternion();
		prevAttitude = new Quaternion();
	}
	public EntityBlockModelCollider(World world, IBlockModel model, IModelController controller, int partialX, int partialY, int partialZ) {
		this(world);
		this.target = model;
		this.controller = controller;
		this.parentAddress.CopyFrom(model.GetCommonAddress());
		this.partialPointX = partialX;
		this.partialPointY = partialY;
		this.partialPointZ = partialZ;
		InitConnector(connector, partialX, partialY, partialZ, 2.5, target.GetBlockAccess());
		makeColliders(target.GetBlockAccess());
		dataManager.set(BASE_POS_X, parentAddress.x);
		dataManager.set(BASE_POS_Y, parentAddress.y);
		dataManager.set(BASE_POS_Z, parentAddress.z);
		dataManager.set(BASE_TREE_IDX, parentAddress.TreeListIndex);
		dataManager.set(BASE_EID, parentAddress.entityId);
		dataManager.set(PARTIAL_POINT_X, partialX);
		dataManager.set(PARTIAL_POINT_Y, partialY);
		dataManager.set(PARTIAL_POINT_Z, partialZ);
		passengerSeats = new Entity[target.GetBlockAccess().listSeat.size()];
		seatRot = new Quaternion[target.GetBlockAccess().listSeat.size()];
	}

	private static void InitConnector(Connector connector, int partialX, int partialY, int partialZ, double center, MTYBlockAccess ba){
		double x = partialX + center - ba.GetLocalCorePosX();
		double y = partialY + center - ba.GetLocalCorePosY();
		double z = partialZ + center - ba.GetLocalCorePosZ();
		connector.SetOrigin(new Vec3d(x, y, z));
	}


	private static final DataParameter<Integer> BASE_POS_X = EntityDataManager.createKey(EntityBlockModelCollider.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> BASE_POS_Y = EntityDataManager.createKey(EntityBlockModelCollider.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> BASE_POS_Z = EntityDataManager.createKey(EntityBlockModelCollider.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> BASE_TREE_IDX = EntityDataManager.createKey(EntityBlockModelCollider.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> BASE_EID = EntityDataManager.createKey(EntityBlockModelCollider.class, DataSerializers.VARINT);

	private static final DataParameter<Integer> PARTIAL_POINT_X = EntityDataManager.createKey(EntityBlockModelCollider.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> PARTIAL_POINT_Y = EntityDataManager.createKey(EntityBlockModelCollider.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> PARTIAL_POINT_Z = EntityDataManager.createKey(EntityBlockModelCollider.class, DataSerializers.VARINT);

	@Override
	protected void entityInit()
	{
		dataManager.register(BASE_POS_X, 0);
		dataManager.register(BASE_POS_Y, 0);
		dataManager.register(BASE_POS_Z, 0);
		dataManager.register(BASE_TREE_IDX, 0);
		dataManager.register(BASE_EID, 0);

		dataManager.register(PARTIAL_POINT_X, 0);
		dataManager.register(PARTIAL_POINT_Y, 0);
		dataManager.register(PARTIAL_POINT_Z, 0);
	}


	@Override
	protected void readEntityFromNBT(@Nonnull NBTTagCompound nbt)
	{
		parentAddress.readFromNBT(nbt, "target");
		partialPointX = nbt.getInteger("partialPointX");
		partialPointY = nbt.getInteger("partialPointY");
		partialPointZ = nbt.getInteger("partialPointZ");

		dataManager.set(BASE_POS_X, parentAddress.x);
		dataManager.set(BASE_POS_Y, parentAddress.y);
		dataManager.set(BASE_POS_Z, parentAddress.z);
		dataManager.set(BASE_TREE_IDX, parentAddress.TreeListIndex);
		dataManager.set(BASE_EID, parentAddress.entityId);
		dataManager.set(PARTIAL_POINT_X, partialPointX);
		dataManager.set(PARTIAL_POINT_Y, partialPointY);
		dataManager.set(PARTIAL_POINT_Z, partialPointZ);
	}

	@Override
	protected void writeEntityToNBT(@Nonnull NBTTagCompound nbt)
	{
		parentAddress.writeToNBT(nbt, "target");
		 nbt.setInteger("partialPointX", partialPointX);
		 nbt.setInteger("partialPointY", partialPointY);
		 nbt.setInteger("partialPointZ", partialPointZ);
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, @Nonnull EnumHand hand) {
		if (player.world.isRemote && player.isSneaking() && target != null) {
			GUIHandler.OpenBlockModelGuiInClient(target);
			return false;
		}
		if (!this.world.isRemote && target != null) {
			List<Connector> seats = target.GetBlockAccess().listSeat;
			if (seats.size() > 0) {
				double d0 = Minecraft.getMinecraft().playerController.getBlockReachDistance();
				net.minecraft.util.math.Vec3d playerPos = player.getPositionEyes(1);
				net.minecraft.util.math.Vec3d look = player.getLook(1.0F);
				net.minecraft.util.math.Vec3d handDir = playerPos.addVector(look.x * d0, look.y * d0, look.z * d0);
				for (int i = 0; i < seats.size(); ++i) {
					Connector c = seats.get(i);
					Vec3d p = c.Current();
					p.x += target.ModelPosX();
					p.y += target.ModelPosY();
					p.z += target.ModelPosZ();
					AxisAlignedBB aabb = new AxisAlignedBB(p.x - 0.5, p.y - 0.5, p.z - 0.5, p.x + 0.5, p.y + 0.5, p.z + 0.5);
					if (aabb.contains(player.getPositionEyes(0))) {
						reservedRidingSeatIndex = i;
						player.startRiding(this);
						return true;
					}
					RayTraceResult rayResult = aabb.calculateIntercept(playerPos, handDir);
					if (rayResult != null) {
						reservedRidingSeatIndex = i;
						player.startRiding(this);
						return true;
					}
				}
			}
		}
		return false;
	}
	@Override
	protected void addPassenger(@Nonnull Entity passenger)
	{
		if(passengerSeats[reservedRidingSeatIndex] == null)
		{
			passengerSeats[reservedRidingSeatIndex] = passenger;
		}
		else return;

		passenger.rotationYaw = 0;
		passenger.rotationPitch = 0;
		super.addPassenger(passenger);
	}
	@Override
	public void updatePassenger(@Nonnull Entity passenger)
	{
		if(target == null) return;
		if (!this.isPassenger(passenger)) return;
		int i = 0;
		for(; i < passengerSeats.length; ++i) {
			if (passenger.equals(passengerSeats[i])) break;
		}
		Connector c = target.GetBlockAccess().listSeat.get(i);
		passenger.setPosition(
				c.Current().x + target.ModelPosX(),
				c.Current().y + target.ModelPosY(),
				c.Current().z + target.ModelPosZ());
//		if(passenger.getRidingEntity()==this)
//			ObfuscationReflectionHelper.setPrivateValue(Entity.class, passenger, seats[i], "ridingEntity");

		if(world.isRemote && Minecraft.getMinecraft().player == passenger)
		{
			RollingSeatManager.SetAttitude(
					attitude,
					prevAttitude,
					target.GetBlockAccess().listSeatRot.get(i));
		}
	}
	@Override
	public Quaternion GetSeatRotation(Entity rider)
	{
		int idx = 0;
		for(; idx < passengerSeats.length; ++idx) {
			if (rider.equals(passengerSeats[idx])) break;
		}
		return target.GetBlockAccess().listSeatRot.get(idx);
	}
	@Override
	public void removePassengers()
	{
		Arrays.fill(passengerSeats, null);
		super.removePassengers();
	}
	@Override
	protected void removePassenger(@Nonnull Entity passenger)
	{
		super.removePassenger(passenger);
		if(world.isRemote && Minecraft.getMinecraft().player == passenger)
			RollingSeatManager.ResetAngles();
	}
	@Override
	public Vec3d RemovePassenger(Entity rider)
	{
		int idx = 0;
		for(; idx < passengerSeats.length; ++idx) {
			if (rider.equals(passengerSeats[idx])) break;
		}

//		if(passengerSeats.length <= idx) return ;
		passengerSeats[idx] = null;

		return target.GetBlockAccess().listSeat.get(idx).Current().New().add(target.ModelPosX(),target.ModelPosY(),target.ModelPosZ());
	}


	@Override
	public Quaternion Attitude(Quaternion out, float partialTicks)
	{
		Quaternion.Lerp(out, prevAttitude, attitude, partialTicks);
		return out;
	}


	public void Delete()
	{
//		Logger.debugInfo("collider is deleted");
//		if(target != null) target.RemoveLateUpdater(this);
		setDead();
	}

	public boolean IsDead()
	{
		return isDead;
	}




	@Override
	public Entity[] getParts()
	{
		return this.partsArray;
	}

//    @Nonnull
//	@Override
//	public AxisAlignedBB getEntityBoundingBox()
//    {
//        	return null;
//    }

    @Override
	public AxisAlignedBB getCollisionBox(@Nonnull Entity entity)
	{
		return entity.getEntityBoundingBox();
	}


	@Override
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

	@Override
    protected boolean canTriggerWalking() {return false;}

	@Override
	public boolean attackEntityFrom(@Nonnull DamageSource ds, float p_70097_2_)
    {
    	return true;
    }

	@Override
	public void setDead()
	{
//		Logger.debugInfo("DEAD");
		if(world.isRemote) Thread.dumpStack();
		this.isDead = true;
	}

	@Override
	public void onUpdate()
	{
//		if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
//			Logger.debugInfo("EntityBlockModelCollider.onUpdate, "+ TickEventHandler.getTickCounter());
//		}
//		if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
//			Logger.debugInfo(this.posY+", "+this.prevPosY+", "+this.lastTickPosY+", "+this.serverPosY);
//		}
		if(target == null)
		{
//			if(!world.isRemote) Delete();
			if(deadCount++ > 100) Delete();

			parentAddress.x = dataManager.get(BASE_POS_X);
			parentAddress.y = dataManager.get(BASE_POS_Y);
			parentAddress.z = dataManager.get(BASE_POS_Z);
			parentAddress.TreeListIndex = dataManager.get(BASE_TREE_IDX);
			parentAddress.entityId = dataManager.get(BASE_EID);
			partialPointX = dataManager.get(PARTIAL_POINT_X);
			partialPointY = dataManager.get(PARTIAL_POINT_Y);
			partialPointZ = dataManager.get(PARTIAL_POINT_Z);
			if(!parentAddress.isSyncing()) Delete();
			IModel model = parentAddress.GetInstance(world);
			if(!(model instanceof IBlockModel))
			{
//				Delete();
				return;
			}
//			Logger.debugInfo("Find parent");
			target = (IBlockModel) model;
			target.RegisterColEntity(this);
			controller = parentAddress.GetController(world);
			makeColliders(target.GetBlockAccess());
			passengerSeats = new Entity[target.GetBlockAccess().listSeat.size()];
			seatRot = new Quaternion[target.GetBlockAccess().listSeat.size()];
			deadCount = 0;
		}
		if(target.IsInvalid())
		{
			Delete();
			return;
		}
	}

	public void LateUpdate(Quaternion attitude)
	{
		prevAttitude.CopyFrom(this.attitude);
		this.attitude.CopyFrom(attitude);
//		if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
//			Logger.debugInfo("EntityBlockModelCollider.LateUpdate, "+ TickEventHandler.getTickCounter());
//		}
//		if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
//			Logger.debugInfo(connector.Base().toString());
//		}
		super.onUpdate();
		this.SetPosition();
	}



	private void makeColliders(MTYBlockAccess blockAccess)
	{
		List<EntityCollisionParts> list = new ArrayList<>();
		int startX = partialPointX;
		int startY = partialPointY;
		int startZ = partialPointZ;
		int len = 5;
		for(int x = startX; x < startX + len; ++x)
			for(int y = startY; y < startY + len; ++y)
				for(int z = startZ; z < startZ + len; ++z) {
					if(blockAccess.getBlockStateLocal(x, y+1, z).getBlock() instanceof BlockSeatPositionMarker) continue;
					BlockPos mtyBaWorld = new BlockPos(blockAccess.WorldFromLocalX(x), blockAccess.WorldFromLocalY(y), blockAccess.WorldFromLocalZ(z));
					IBlockState state = blockAccess.getBlockStateLocal(x, y, z);
					if(state.getBlock() instanceof BlockSeatPositionMarker) continue;
					AxisAlignedBB aabb = state.getCollisionBoundingBox(blockAccess, mtyBaWorld);
					if(aabb != null){
						Connector connector = new Connector("");
						connector.SetOrigin(new Vec3d(
								x - blockAccess.GetLocalCorePosX(),
								y - blockAccess.GetLocalCorePosY(),
								z - blockAccess.GetLocalCorePosZ()));
						list.add(new EntityCollisionParts(world, connector, controller, aabb));
					}
				}
		if(!list.isEmpty()){
			partsArray = list.toArray(partsArray);
		}
		double center = len * 0.5;
		double x = startX + center - blockAccess.GetLocalCorePosX();
		double y = startY + center - blockAccess.GetLocalCorePosY();
		double z = startZ + center - blockAccess.GetLocalCorePosZ();
		InitConnector(connector, startX, startY, startZ, center, blockAccess);
		connector.SetOrigin(new Vec3d(x, y, z));
	}

	private void SetPosition()
	{
		if(target == null) return;
		if(controller == null) return;
		target.UpdateChildConnector(connector);
		setPosition(
				controller.CorePosX() + connector.Current().x,
				controller.CorePosY() + connector.Current().y,
				controller.CorePosZ() + connector.Current().z);
		for(EntityCollisionParts part : partsArray)
		{
			part.SetPosition(target);
		}
		for(Connector c : target.GetBlockAccess().listSeat)
		{
			target.UpdateChildConnector(c);
		}
		List<Entity> entities = this.world.getEntitiesInAABBexcluding(this,
				this.getEntityBoundingBox(),
				e -> !(e instanceof EntityCollisionParts || e instanceof EntityBlockModelCollider));
		for (Entity entity : entities){
			for(EntityCollisionParts part : partsArray)
			{
				double x = 0, y = 0, z = 0;
				part.MoveCollidedEntities(entity, x, y, z);
			}
			entity.updateBlocked = false;
		}
	}

	@Override
	public void setPosition(double x, double y, double z)
	{
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		double w = this.width * 0.5;
		double h = this.height * 0.5;
		super.setEntityBoundingBox(new AxisAlignedBB(x-w, y-h, z-w, x+w, y+h, z+w));
	}

	public void SetScale(float scale)
	{
		this.setSize(5f*1.732f*scale, 5f*1.732f*scale);
		for(EntityCollisionParts part : partsArray)
		{
			part.SetScale(scale);
		}
	}
}
