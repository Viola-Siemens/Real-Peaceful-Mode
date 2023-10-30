package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.enchantments.CrackingEnchantment;
import com.hexagram2021.real_peaceful_mode.common.enchantments.UndeadFlameEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@SuppressWarnings("unused")
public class RPMEnchantments {
	public static final DeferredRegister<Enchantment> REGISTER = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);

	public static final RegistryObject<Enchantment> CRACKING = REGISTER.register(
			"cracking", () -> new CrackingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND)
	);

	public static final RegistryObject<Enchantment> UNDEAD_FLAME = REGISTER.register(
			"undead_flame", () -> new UndeadFlameEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND)
	);

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
