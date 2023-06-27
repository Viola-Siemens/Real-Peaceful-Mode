package com.hexagram2021.real_peaceful_mode.mixin;

import com.google.common.collect.Maps;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;

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
}
