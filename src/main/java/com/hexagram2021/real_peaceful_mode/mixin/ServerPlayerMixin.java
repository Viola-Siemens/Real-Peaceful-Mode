package com.hexagram2021.real_peaceful_mode.mixin;

import com.google.common.collect.Maps;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.IPlayerListWithMissions;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.PlayerMissions;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements IMonsterHero {
	@Unique
	private final Map<ResourceLocation, Integer> rpm$helpedMonsters = Maps.newHashMap();

	@Unique
	@SuppressWarnings("NotNullFieldNotInitialized")
	private PlayerMissions rpm$playerMissions;

	@Inject(method = "<init>", at = @At(value = "TAIL"))
	public void rpm$createNewPlayer(MinecraftServer server, ServerLevel level, GameProfile gameProfile, CallbackInfo ci) {
		this.rpm$playerMissions = ((IPlayerListWithMissions)server.getPlayerList()).rpm$getPlayerMissions((ServerPlayer)(Object)this);
	}

	@Override
	public boolean rpm$isHero(EntityType<?> monsterType) {
		return this.rpm$helpedMonsters.containsKey(getRegistryName(monsterType));
	}

	@Override
	public void rpm$setHero(EntityType<?> monsterType) {
		this.rpm$helpedMonsters.compute(getRegistryName(monsterType), (type, count) -> {
			if(count == null) {
				return 1;
			}
			return count + 1;
		});
	}

	@Override
	public Map<ResourceLocation, Integer> rpm$getHelpedMonsters() {
		return this.rpm$helpedMonsters;
	}

	@Override
	public PlayerMissions rpm$getPlayerMissions() {
		return this.rpm$playerMissions;
	}

	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	public void rpm$readRPMData(CompoundTag nbt, CallbackInfo ci) {
		if(nbt.contains(HELPED_MONSTERS, Tag.TAG_LIST)) {
			ListTag list = nbt.getList(HELPED_MONSTERS, Tag.TAG_COMPOUND);
			list.forEach(tag -> {
				CompoundTag compound = (CompoundTag)tag;
				this.rpm$helpedMonsters.compute(new ResourceLocation(compound.getString("type")), (type, count) -> compound.getInt("count"));
			});
		}
		this.rpm$playerMissions.readNBT(nbt);
	}

	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	public void rpm$addRPMData(CompoundTag nbt, CallbackInfo ci) {
		ListTag tags = new ListTag();
		this.rpm$helpedMonsters.forEach((type, count) -> {
			CompoundTag tag = new CompoundTag();
			tag.putString("type", type.toString());
			tag.putInt("count", count);
			tags.add(tag);
		});
		nbt.put(HELPED_MONSTERS, tags);
		this.rpm$playerMissions.writeNBT(nbt);
	}

	@Inject(method = "restoreFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setLastDeathLocation(Ljava/util/Optional;)V"))
	public void rpm$restoreRPMDataFrom(ServerPlayer player, boolean won, CallbackInfo ci) {
		if(player instanceof IMonsterHero hero) {
			hero.rpm$getHelpedMonsters().forEach((type, count) -> this.rpm$helpedMonsters.compute(type, (type1, count1) -> count));
		}
	}
}
