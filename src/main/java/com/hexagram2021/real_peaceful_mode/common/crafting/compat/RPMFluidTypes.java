package com.hexagram2021.real_peaceful_mode.common.crafting.compat;

import com.hexagram2021.emeraldcraft.api.fluid.FluidType;
import com.hexagram2021.emeraldcraft.api.fluid.FluidTypes;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;

public enum RPMFluidTypes implements FluidType {
	melted_aluminum {
		@Override
		public int getGUIID() {
			return 6;
		}
	},
	melted_silver {
		@Override
		public int getGUIID() {
			return 7;
		}
	},
	melted_manganese {
		@Override
		public int getGUIID() {
			return 8;
		}
	};

	public static void init() {
		FluidTypes.addFluidType(melted_aluminum, RPMItems.ECCompatItems.MELTED_ALUMINUM_BUCKET, RPMItems.ECCompatItems.MELTED_ALUMINUM_BUCKET.getId());
		FluidTypes.addFluidType(melted_silver, RPMItems.ECCompatItems.MELTED_SILVER_BUCKET, RPMItems.ECCompatItems.MELTED_SILVER_BUCKET.getId());
		FluidTypes.addFluidType(melted_manganese, RPMItems.ECCompatItems.MELTED_MANGANESE_BUCKET, RPMItems.ECCompatItems.MELTED_MANGANESE_BUCKET.getId());
	}
}
