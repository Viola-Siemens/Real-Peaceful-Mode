package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.fluid.MagicPoolWaterFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
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
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@SuppressWarnings("deprecation")
public class RPMFluids {
	public static final DeferredRegister<Fluid> REGISTER = DeferredRegister.create(ForgeRegistries.FLUIDS, MODID);
	public static final DeferredRegister<FluidType> TYPE_REGISTER = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MODID);

	public static final FluidEntry<MagicPoolWaterFluid> MAGIC_POOL_WATER_FLUID = FluidEntry.register(
			"magic_pool_water",
			new ResourceLocation(MODID, "block/fluid/magic_pool_water_still"), new ResourceLocation(MODID, "block/fluid/magic_pool_water_flowing"),
			RPMFluidTags.MAGIC_POOL_WATER, MagicPoolWaterFluid.Source::new, MagicPoolWaterFluid.Flowing::new,
			(entry, props) -> new LiquidBlock(entry::getStill, props) {
				@Override
				public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
					if(entity instanceof ItemEntity itemEntity) {
						ItemStack itemStack = itemEntity.getItem();
						if(itemStack.is(RPMBlocks.Decoration.DARK_ZOMBIE_KNIGHT_SKULL.asItem())) {
							for(int x = -3; x <= 3; ++x) {
								for(int z = -3; z <= 3; ++z) {
									for(int y = -2; y < 2; ++y) {
										BlockPos current = blockPos.offset(x, y, z);
										if(level.getFluidState(current).is(RPMFluidTags.MAGIC_POOL_WATER)) {
											level.setBlock(current, DARK_MAGIC_POOL_WATER_FLUID.getBlock().defaultBlockState(), UPDATE_ALL);
										}
									}
								}
							}
							level.explode(itemEntity, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 0.25F, Level.ExplosionInteraction.NONE);
							itemEntity.discard();
						}
					}
					super.entityInside(blockState, level, blockPos, entity);
				}
			}
	);
	public static final FluidEntry<MagicPoolWaterFluid> DARK_MAGIC_POOL_WATER_FLUID = FluidEntry.register(
			"dark_magic_pool_water",
			new ResourceLocation(MODID, "block/fluid/dark_magic_pool_water_still"), new ResourceLocation(MODID, "block/fluid/dark_magic_pool_water_flowing"),
			RPMFluidTags.DARK_MAGIC_POOL_WATER, MagicPoolWaterFluid.Source::new, MagicPoolWaterFluid.Flowing::new,
			(entry, props) -> new LiquidBlock(entry::getStill, props)
	);

	public record FluidEntry<T extends Fluid>(RegistryObject<T> still, RegistryObject<T> flowing,
											  RPMBlocks.BlockEntry<LiquidBlock> fluidBlock, RPMItems.ItemEntry<BucketItem> bucket,
											  RegistryObject<FluidType> type) {
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
			RegistryObject<FluidType> type = TYPE_REGISTER.register(name, () -> buildFluidTypeWithTextures(builder, stillTex, flowingTex));
			Mutable<FluidEntry<T>> thisMutable = new MutableObject<>();
			RegistryObject<T> still = REGISTER.register(name, () -> makeFluid(
					stillMaker, thisMutable.getValue(), fluidTag
			));
			RegistryObject<T> flowing = REGISTER.register("flowing_" + name, () -> makeFluid(
					flowingMaker, thisMutable.getValue(), fluidTag
			));
			RPMBlocks.BlockEntry<LiquidBlock> block = new RPMBlocks.BlockEntry<>(
					name,
					() -> BlockBehaviour.Properties.copy(Blocks.WATER),
					p -> blockMaker.apply(thisMutable.getValue(), p)
			);
			RPMItems.ItemEntry<BucketItem> bucket = RPMItems.ItemEntry.register(name+"_bucket", () -> makeBucket(still));
			FluidEntry<T> entry = new FluidEntry<>(still, flowing, block, bucket, type);
			thisMutable.setValue(entry);
			return entry;
		}

		private static FluidType buildFluidTypeWithTextures(FluidType.Properties builder, ResourceLocation stillTex, ResourceLocation flowingTex) {
			return new FluidType(builder) {
				@Override
				public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
					consumer.accept(new IClientFluidTypeExtensions() {
						@Override
						public ResourceLocation getStillTexture() {
							return stillTex;
						}
						@Override
						public ResourceLocation getFlowingTexture() {
							return flowingTex;
						}
					});
				}
			};
		}

		private static <T extends Fluid> T makeFluid(BiFunction<FluidEntry<T>, TagKey<Fluid>, T> maker, FluidEntry<T> entry, TagKey<Fluid> fluidTag) {
			return maker.apply(entry, fluidTag);
		}

		private static <T extends Fluid> BucketItem makeBucket(RegistryObject<T> still) {
			return new BucketItem(still, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)) {
				@Override
				public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
					return new FluidBucketWrapper(stack);
				}
			};
		}
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
		TYPE_REGISTER.register(bus);
	}
}
