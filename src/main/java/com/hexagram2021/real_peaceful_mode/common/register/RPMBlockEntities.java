package com.hexagram2021.real_peaceful_mode.common.register;

import com.google.common.collect.ImmutableSet;
import com.hexagram2021.real_peaceful_mode.common.block.entity.ContinuousSummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.block.entity.CultureTableBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@SuppressWarnings("ConstantConditions")
public class RPMBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SummonBlockEntity>> SUMMON_BLOCK = REGISTER.register(
			"summon_block", () -> new BlockEntityType<>(
					SummonBlockEntity::new, ImmutableSet.of(RPMBlocks.TechnicalBlocks.SUMMON_BLOCK.get()), null
			)
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ContinuousSummonBlockEntity>> CONTINUOUS_SUMMON_BLOCK = REGISTER.register(
			"continuous_summon_block", () -> new BlockEntityType<>(
					ContinuousSummonBlockEntity::new, ImmutableSet.of(RPMBlocks.TechnicalBlocks.CONTINUOUS_SUMMON_BLOCK.get()), null
			)
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CultureTableBlockEntity>> CULTURE_TABLE = REGISTER.register(
			"culture_table", () -> new BlockEntityType<>(
					CultureTableBlockEntity::new, ImmutableSet.of(RPMBlocks.WorkStation.CULTURE_TABLE.get()), null
			)
	);

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
