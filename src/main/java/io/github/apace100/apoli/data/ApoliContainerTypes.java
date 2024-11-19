package io.github.apace100.apoli.data;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.container.ContainerType;
import io.github.apace100.apoli.data.container.PresetContainerType;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class ApoliContainerTypes {

	//	Presets
	public static final PresetContainerType DOUBLE_CHEST = register("double_chest", new PresetContainerType(9, 6, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, inventory, rows)));
	public static final PresetContainerType CHEST = register("chest", new PresetContainerType(9, 3, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, inventory, rows)));
	public static final PresetContainerType HOPPER = register("hopper", new PresetContainerType(5, 1, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new HopperScreenHandler(syncId, playerInventory, inventory)));
	public static final PresetContainerType DROPPER = register("dropper", new PresetContainerType(3, 3, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new Generic3x3ContainerScreenHandler(syncId, playerInventory, inventory)));
	public static final PresetContainerType DISPENSER = register("dispenser", new PresetContainerType(3, 3, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new Generic3x3ContainerScreenHandler(syncId, playerInventory, inventory)));

	public static void register() {

	}

	public static <T extends ContainerType> T register(String name, T containerType) {
		return Registry.register(ApoliRegistries.CONTAINER_TYPE, Apoli.identifier(name), containerType);
	}

}
