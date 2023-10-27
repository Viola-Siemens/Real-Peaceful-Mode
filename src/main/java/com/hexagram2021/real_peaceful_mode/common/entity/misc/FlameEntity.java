package com.hexagram2021.real_peaceful_mode.common.entity.misc;

import com.hexagram2021.real_peaceful_mode.common.entity.boss.HuskPharaoh;
import com.hexagram2021.real_peaceful_mode.common.register.RPMEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class FlameEntity extends Entity {
	private static final EntityDataAccessor<Optional<BlockPos>> DATA_BEAM_TARGET = SynchedEntityData.defineId(FlameEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
	private static final EntityDataAccessor<Float> DATA_YAW = SynchedEntityData.defineId(FlameEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_DIST_CENTER = SynchedEntityData.defineId(FlameEntity.class, EntityDataSerializers.FLOAT);

	private static final float MAX_POWER = 50.0F;

	private float power = 2.5F;

	private int removeTick = 2400;

	public FlameEntity(EntityType<?> entityType, Level level) {
		super(entityType, level);
		this.blocksBuilding = true;
	}

	public FlameEntity(Level level, BlockPos blockPos, float distCenter) {
		super(RPMEntities.FLAME_CRYSTAL, level);
		this.moveTo(blockPos.getCenter());
		this.setBeamTarget(blockPos);
		this.setDistCenter(distCenter);
	}

	@Override
	protected Entity.MovementEmission getMovementEmission() {
		return Entity.MovementEmission.NONE;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	protected void defineSynchedData() {
		this.getEntityData().define(DATA_BEAM_TARGET, Optional.empty());
		this.getEntityData().define(DATA_YAW, 0.0F);
		this.getEntityData().define(DATA_DIST_CENTER, 0.0F);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag nbt) {
		if(nbt.contains("Power", Tag.TAG_FLOAT)) {
			this.power = nbt.getFloat("Power");
		}
		if(nbt.contains("Yaw", Tag.TAG_FLOAT)) {
			this.setYaw(nbt.getFloat("Yaw"));
		}
		if(nbt.contains("DistCenter", Tag.TAG_FLOAT)) {
			this.setDistCenter(nbt.getFloat("DistCenter"));
		}
		if (nbt.contains("BeamTarget", Tag.TAG_COMPOUND)) {
			this.setBeamTarget(NbtUtils.readBlockPos(nbt.getCompound("BeamTarget")));
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt) {
		nbt.putFloat("Power", this.power);
		nbt.putFloat("Yaw", this.getYaw());
		nbt.putFloat("DistCenter", this.getDistCenter());
		if (this.getBeamTarget() != null) {
			nbt.put("BeamTarget", NbtUtils.writeBlockPos(this.getBeamTarget()));
		}
	}

	@Override
	public boolean isOnFire() {
		return true;
	}

	@Override
	public boolean hurt(DamageSource damageSource, float v) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		}
		if (damageSource.getDirectEntity() instanceof TinyFireballEntity) {
			this.power += (this.power > MAX_POWER - 2.0D) ? (MAX_POWER - this.power) / 2.0D : 1.0D;
			return false;
		}
		if (!this.isRemoved() && !this.level().isClientSide) {
			for(int i = 0; i < this.power; ++i) {
				double twoPi = Math.PI * 2.0D;
				double pitch = this.level().getRandom().nextDouble() * twoPi;
				double yaw = this.level().getRandom().nextDouble() * twoPi;
				double vecX = Math.cos(pitch) * Math.cos(yaw);
				double vecY = Math.sin(pitch);
				double vecZ = Math.cos(pitch) * Math.sin(yaw);
				TinyFireballEntity fireball = new TinyFireballEntity(this.level(), this.getX(), this.getEyeY(), this.getZ(), vecX, vecY, vecZ);
				this.level().addFreshEntity(fireball);
			}
			if(this.power >= 1) {
				this.playSound(SoundEvents.FIRECHARGE_USE);
				this.power = 0;
			}
			this.remove(Entity.RemovalReason.KILLED);
		}
		return true;
	}

	@Override
	public void tick() {
		double x, z;
		if(this.level() instanceof ServerLevel serverLevel) {
			--this.removeTick;
			if(this.removeTick <= 0) {
				this.hurt(this.damageSources().generic(), 1.0F);
				return;
			}
			BlockPos pos = this.getBeamTarget();
			if(pos != null) {
				Vec3 position = pos.getCenter();
				float yaw = this.getYaw();
				yaw += 1.0F;
				if(yaw > 100.0F) {
					yaw -= 100.0F;
				}
				this.setYaw(yaw);
				yaw = yaw / 100.0F * Mth.TWO_PI;
				x = position.x + this.getDistCenter() * Mth.cos(yaw);
				z = position.z + this.getDistCenter() * Mth.sin(yaw);
				this.moveTo(x, position.y, z);
			}
			if(this.tickCount % 20 == 0) {
				if(!serverLevel.getLevelData().isRaining() && this.level().getBrightness(LightLayer.SKY, this.blockPosition()) > 4) {
					this.power += (this.power > MAX_POWER - 0.5D) ? (MAX_POWER - this.power) / 8.0D : 0.0625D;
				}
				serverLevel.getPlayers(serverPlayer -> serverPlayer.closerThan(this, 32.0D) && serverPlayer.canBeSeenAsEnemy()).forEach(serverPlayer -> {
					Vec3 diff = this.position().subtract(serverPlayer.position());
					double mag = Math.sqrt(diff.length()) / 2.0D;
					serverPlayer.setDeltaMovement(diff.normalize().multiply(mag, 0.25D, mag).add(serverPlayer.getDeltaMovement()));
					if (this.random.nextFloat() * 2.0F < this.power / MAX_POWER && this.hasLineOfSight(serverPlayer)) {
						this.power -= 1.0F;
						this.performRangedAttack(serverPlayer);
					}
				});
				serverLevel.getEntitiesOfClass(HuskPharaoh.class, this.getBoundingBox().inflate(this.getDistCenter())).forEach(pharaoh -> {
					if(pharaoh.isAlive()) {
						pharaoh.heal(1.0F);
					}
				});
			}
		}
	}

	@Override
	public void playerTouch(Player player) {
		super.playerTouch(player);
		player.setRemainingFireTicks(player.getRemainingFireTicks() + 20);
	}

	public boolean hasLineOfSight(Entity entity) {
		if (entity.level() != this.level()) {
			return false;
		}
		Vec3 currentPosition = new Vec3(this.getX(), this.getEyeY(), this.getZ());
		Vec3 entityPosition = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
		if (entityPosition.distanceTo(currentPosition) > 40.0D) {
			return false;
		}
		return this.level().clip(new ClipContext(currentPosition, entityPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
	}

	public void performRangedAttack(LivingEntity target) {
		double x = target.getX() - this.getX();
		double z = target.getZ() - this.getZ();
		double y = target.getY() - this.getY(1.0D / 3.0D);
		double r = Math.sqrt(x * x + z * z);
		double r2 = Math.sqrt(x * x + y * y + z * z);
		TinyFireballEntity fireball = new TinyFireballEntity(this.level(), this.getX() + x / r, this.getY(1.0D / 3.0D), this.getZ() + z / r, x / r2, y / r2, z / r2);
		this.level().addFreshEntity(fireball);
		this.playSound(SoundEvents.FIRECHARGE_USE);
	}

	public void setBeamTarget(@Nullable BlockPos beamTarget) {
		this.getEntityData().set(DATA_BEAM_TARGET, Optional.ofNullable(beamTarget));
	}
	@Nullable
	public BlockPos getBeamTarget() {
		return this.getEntityData().get(DATA_BEAM_TARGET).orElse(null);
	}

	public void setYaw(float yaw) {
		this.getEntityData().set(DATA_YAW, yaw);
	}
	public float getYaw() {
		return this.getEntityData().get(DATA_YAW);
	}

	public void setDistCenter(float distCenter) {
		this.getEntityData().set(DATA_DIST_CENTER, distCenter);
	}
	public float getDistCenter() {
		return this.getEntityData().get(DATA_DIST_CENTER);
	}
}
