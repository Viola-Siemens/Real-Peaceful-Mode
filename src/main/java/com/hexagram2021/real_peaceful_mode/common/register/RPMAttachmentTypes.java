package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.entity.capability.ItemEntityConvertible;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMAttachmentTypes {
	private static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
	public static final String ITEM_ENTITY_CONVERSION_TAG = "conversion";

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<ItemEntityConvertible>> ITEM_ENTITY_CONVERSION = REGISTER.register(
			ITEM_ENTITY_CONVERSION_TAG,
			() -> AttachmentType.builder(() -> new ItemEntityConvertible(1200))
					.serialize(ItemEntityConvertible.CODEC)
					.copyHandler((attachment, holder, provider) -> new ItemEntityConvertible(attachment.maxRemainingTicks(), attachment.getRemainingTicks(), attachment.getToConvert()))
					.build()
	);

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private RPMAttachmentTypes() {
	}
}
