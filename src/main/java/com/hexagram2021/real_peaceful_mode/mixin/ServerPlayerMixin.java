package com.hexagram2021.real_peaceful_mode.mixin;

import com.google.common.collect.Maps;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements IMonsterHero {
	private final Map<ResourceLocation, Integer> helpedMonsters = Maps.newHashMap();

	@Override
	public boolean isHero(EntityType<?> monsterType) {
		return this.helpedMonsters.containsKey(getRegistryName(monsterType));
	}

	@Override
	public void setHero(EntityType<?> monsterType) {
		this.helpedMonsters.compute(getRegistryName(monsterType), (type, count) -> {
			if(count == null) {
				return 1;
			}
			return count + 1;
		});
	}

	@Override
	public Map<ResourceLocation, Integer> getHelpedMonsters() {
		return this.helpedMonsters;
	}

	private static final String HELPED_MONSTERS = "helpedMonsters";
	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	public void readRPMData(CompoundTag nbt, CallbackInfo ci) {
		if(nbt.contains(HELPED_MONSTERS, Tag.TAG_LIST)) {
			ListTag list = nbt.getList(HELPED_MONSTERS, Tag.TAG_COMPOUND);
			list.forEach(tag -> {
				CompoundTag compound = (CompoundTag)tag;
				this.helpedMonsters.compute(new ResourceLocation(compound.getString("type")), (type, count) -> compound.getInt("count"));
			});
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	public void addRPMData(CompoundTag nbt, CallbackInfo ci) {
		ListTag tags = new ListTag();
		this.helpedMonsters.forEach((type, count) -> {
			CompoundTag tag = new CompoundTag();
			tag.putString("type", type.toString());
			tag.putInt("count", count);
			tags.add(tag);
		});
		nbt.put(HELPED_MONSTERS, tags);
	}

	@Inject(method = "restoreFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setLastDeathLocation(Ljava/util/Optional;)V"))
	public void restoreRPMDataFrom(ServerPlayer player, boolean won, CallbackInfo ci) {
		if(player instanceof IMonsterHero hero) {
			hero.getHelpedMonsters().forEach((type, count) -> this.helpedMonsters.compute(type, (type1, count1) -> count));
		}
	}
}
