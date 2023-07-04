package com.hexagram2021.real_peaceful_mode.common.crafting.menus;

import com.hexagram2021.real_peaceful_mode.common.crafting.ClientSideMessagedMission;
import com.hexagram2021.real_peaceful_mode.common.crafting.MessagedMission;
import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import com.hexagram2021.real_peaceful_mode.common.register.RPMMenuTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MissionMessageMenu extends AbstractContainerMenu {
	private final MessagedMission mission;
	private final Runnable onRemoved;

	public MissionMessageMenu(int counter, Inventory inventory) {
		this(counter, new ClientSideMessagedMission(inventory.player), () -> {});
	}

	public MissionMessageMenu(int counter, MessagedMission mission, Runnable onRemoved) {
		super(RPMMenuTypes.MISSION_MESSAGE_MENU.get(), counter);
		this.mission = mission;
		this.onRemoved = onRemoved;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		LivingEntity npc = this.mission.npc();
		if(npc == null) {
			return true;
		}
		return this.mission.player().closerThan(npc, 24.0D);
	}

	public MessagedMission getMission() {
		return this.mission;
	}

	@Nullable
	public LivingEntity getSpeaker(MissionManager.Mission.Message.Speaker speaker) {
		return switch (speaker) {
			case PLAYER -> this.mission.player();
			case NPC -> this.mission.npc();
		};
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		this.onRemoved.run();
	}
}
