package com.hexagram2021.real_peaceful_mode.common.register;

import com.google.common.collect.ImmutableSet;
import com.hexagram2021.real_peaceful_mode.common.block.entity.ContinuousSummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@SuppressWarnings("ConstantConditions")
public class RPMBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

	public static final RegistryObject<BlockEntityType<SummonBlockEntity>> SUMMON_BLOCK = REGISTER.register(
			"summon_block", () -> new BlockEntityType<>(
					SummonBlockEntity::new, ImmutableSet.of(RPMBlocks.TechnicalBlocks.SUMMON_BLOCK.get()), null
			)
	);

	public static final RegistryObject<BlockEntityType<ContinuousSummonBlockEntity>> CONTINUOUS_SUMMON_BLOCK = REGISTER.register(
			"continuous_summon_block", () -> new BlockEntityType<>(
					ContinuousSummonBlockEntity::new, ImmutableSet.of(RPMBlocks.TechnicalBlocks.CONTINUOUS_SUMMON_BLOCK.get()), null
			)
	);

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
