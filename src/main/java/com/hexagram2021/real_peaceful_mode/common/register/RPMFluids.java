package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.api.MissionType;
import com.hexagram2021.real_peaceful_mode.common.fluid.MagicPoolWaterFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.function.BiFunction;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMFluids {
	public static final DeferredRegister<Fluid> REGISTER = DeferredRegister.create(Registries.FLUID, MODID);
	public static final DeferredRegister<FluidType> TYPE_REGISTER = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, MODID);

	public static final FluidEntry<MagicPoolWaterFluid> MAGIC_POOL_WATER_FLUID = FluidEntry.register(
			"magic_pool_water",
			ResourceLocation.fromNamespaceAndPath(MODID, "block/fluid/magic_pool_water_still"), ResourceLocation.fromNamespaceAndPath(MODID, "block/fluid/magic_pool_water_flowing"),
			RPMFluidTags.MAGIC_POOL_WATER, MagicPoolWaterFluid.Source::new, MagicPoolWaterFluid.Flowing::new,
			(entry, props) -> new LiquidBlock(entry.getStill(), props) {
				@Override
				public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
					if(entity instanceof ItemEntity itemEntity) {
						ItemStack itemStack = itemEntity.getItem();
						if(itemStack.is(RPMBlocks.Decoration.DARK_ZOMBIE_KNIGHT_SKULL.asItem())) {
							for(int x = -2; x <= 2; ++x) {
								for(int z = -2; z <= 2; ++z) {
									for(int y = -2; y < 2; ++y) {
										BlockPos current = blockPos.offset(x, y, z);
										if(level.getFluidState(current).is(RPMFluidTags.MAGIC_POOL_WATER)) {
											level.setBlock(current, DARK_MAGIC_POOL_WATER_FLUID.getBlock().defaultBlockState(), UPDATE_ALL);
										}
									}
								}
							}
							level.explode(itemEntity, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 0.5F, Level.ExplosionInteraction.NONE);
							itemEntity.discard();
						}
					}
					super.entityInside(blockState, level, blockPos, entity);
				}
			}
	);
	public static final FluidEntry<MagicPoolWaterFluid> DARK_MAGIC_POOL_WATER_FLUID = FluidEntry.register(
			"dark_magic_pool_water",
			ResourceLocation.fromNamespaceAndPath(MODID, "block/fluid/dark_magic_pool_water_still"), ResourceLocation.fromNamespaceAndPath(MODID, "block/fluid/dark_magic_pool_water_flowing"),
			RPMFluidTags.DARK_MAGIC_POOL_WATER, MagicPoolWaterFluid.Source::new, MagicPoolWaterFluid.Flowing::new,
			(entry, props) -> new LiquidBlock(entry.getStill(), props) {
				@Override
				public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
					if(entity instanceof ItemEntity itemEntity && level instanceof ServerLevel serverLevel) {
						ItemStack itemStack = itemEntity.getItem();
						if(itemStack.is(Items.ROTTEN_FLESH)) {
							for(int x = -2; x <= 2; ++x) {
								for(int z = -2; z <= 2; ++z) {
									for(int y = -2; y < 2; ++y) {
										BlockPos current = blockPos.offset(x, y, z);
										if(serverLevel.getFluidState(current).is(RPMFluidTags.DARK_MAGIC_POOL_WATER)) {
											serverLevel.setBlock(current, MAGIC_POOL_WATER_FLUID.getBlock().defaultBlockState(), UPDATE_ALL);
										}
									}
								}
							}
							serverLevel.explode(itemEntity, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 0.5F, Level.ExplosionInteraction.NONE);

							int distance = 16;
							serverLevel.getPlayers(player -> player.closerThan(itemEntity, distance)).forEach(player -> player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 160)));
							MissionHelper.triggerMissionForPlayers(
									ResourceLocation.fromNamespaceAndPath(MODID, "zombie2"), MissionType.FINISH, serverLevel,
									player -> player.closerThan(itemEntity, distance), null, player -> {}
							);
							itemEntity.discard();
						}
					}
					super.entityInside(blockState, level, blockPos, entity);
				}
			}
	);

	public record FluidEntry<T extends Fluid>(DeferredHolder<Fluid, T> still, DeferredHolder<Fluid, T> flowing,
											  RPMBlocks.BlockEntry<LiquidBlock> fluidBlock, RPMItems.ItemEntry<BucketItem> bucket,
											  DeferredHolder<FluidType, FluidType> type, ResourceLocation stillTex, ResourceLocation flowingTex) {
		public T getFlowing() {
			return this.flowing.get();
		}

		public T getStill() {
			return this.still.get();
		}

		public LiquidBlock getBlock() {
			return this.fluidBlock.get();
		}

		public BucketItem getBucket() {
			return this.bucket.get();
		}

		public FluidType getType() {
			return this.type.get();
		}

		public static <T extends Fluid> FluidEntry<T> register(String name, ResourceLocation stillTex, ResourceLocation flowingTex,
															   TagKey<Fluid> fluidTag, BiFunction<FluidEntry<T>, TagKey<Fluid>, T> stillMaker, BiFunction<FluidEntry<T>, TagKey<Fluid>, T> flowingMaker,
															   BiFunction<FluidEntry<T>, BlockBehaviour.Properties, ? extends LiquidBlock> blockMaker) {
			FluidType.Properties builder = FluidType.Properties.create()
					.descriptionId("block.%s.%s".formatted(MODID, name))
					.fallDistanceModifier(0F)
					.canExtinguish(true).canConvertToSource(true).supportsBoating(true).canHydrate(true)
					.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
					.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
					.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH);
			DeferredHolder<FluidType, FluidType> type = TYPE_REGISTER.register(name, () -> new FluidType(builder));
			Mutable<FluidEntry<T>> thisMutable = new MutableObject<>();
			DeferredHolder<Fluid, T> still = REGISTER.register(name, () -> makeFluid(
					stillMaker, thisMutable.getValue(), fluidTag
			));
			DeferredHolder<Fluid, T> flowing = REGISTER.register("flowing_" + name, () -> makeFluid(
					flowingMaker, thisMutable.getValue(), fluidTag
			));
			RPMBlocks.BlockEntry<LiquidBlock> block = new RPMBlocks.BlockEntry<>(
					name,
					() -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER),
					p -> blockMaker.apply(thisMutable.getValue(), p)
			);
			RPMItems.ItemEntry<BucketItem> bucket = RPMItems.ItemEntry.register(name+"_bucket", () -> makeBucket(still));
			FluidEntry<T> entry = new FluidEntry<>(still, flowing, block, bucket, type, stillTex, flowingTex);
			thisMutable.setValue(entry);
			return entry;
		}

		private static <T extends Fluid> T makeFluid(BiFunction<FluidEntry<T>, TagKey<Fluid>, T> maker, FluidEntry<T> entry, TagKey<Fluid> fluidTag) {
			return maker.apply(entry, fluidTag);
		}

		private static <T extends Fluid> BucketItem makeBucket(DeferredHolder<Fluid, T> still) {
			return new BucketItem(still.get(), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET));
		}
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
		TYPE_REGISTER.register(bus);
	}
}
