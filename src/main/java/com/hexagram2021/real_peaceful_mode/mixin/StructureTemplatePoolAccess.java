package com.hexagram2021.real_peaceful_mode.mixin;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructureTemplatePool.class)
public interface StructureTemplatePoolAccess {
	@Accessor
	List<Pair<StructurePoolElement, Integer>> getRawTemplates();
	@Accessor @Final @Mutable
	void setRawTemplates(List<Pair<StructurePoolElement, Integer>> value);

	@Accessor
	ObjectArrayList<StructurePoolElement> getTemplates();
}
