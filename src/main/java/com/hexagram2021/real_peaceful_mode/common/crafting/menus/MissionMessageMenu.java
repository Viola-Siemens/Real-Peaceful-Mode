package com.hexagram2021.real_peaceful_mode.common.crafting.menus;

import com.hexagram2021.real_peaceful_mode.common.crafting.ClientSideMessagedMission;
import com.hexagram2021.real_peaceful_mode.common.crafting.MessagedMission;
import com.hexagram2021.real_peaceful_mode.common.register.RPMMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class MissionMessageMenu extends AbstractContainerMenu {
	private final MessagedMission mission;

	public MissionMessageMenu(int counter, Inventory inventory) {
		this(counter, new ClientSideMessagedMission(inventory.player));
	}

	public MissionMessageMenu(int counter, MessagedMission mission) {
		super(RPMMenuTypes.MISSION_MESSAGE_MENU.get(), counter);
		this.mission = mission;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.mission.player().closerThan(this.mission.npc(), 24.0D);
	}

	public MessagedMission getMission() {
		return this.mission;
	}
}
