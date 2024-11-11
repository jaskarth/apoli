package io.github.apace100.apoli.util;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.registry.DataObjectFactories;
import io.github.apace100.calio.registry.DataObjectFactory;
import net.minecraft.inventory.SlotRange;
import net.minecraft.item.ItemStack;

public record IndexedStack(ItemStack stack, SlotRange slot) {

	public static final DataObjectFactory<IndexedStack> DATA_FACTORY = DataObjectFactory.simple(
		DataObjectFactories.ITEM_STACK.getSerializableData().copy()
			.add("slot", ApoliDataTypes.SINGLE_SLOT_RANGE),
		data -> new IndexedStack(
			DataObjectFactories.ITEM_STACK.fromData(data),
			data.get("slot")
		),
		(indexedStack, serializableData) -> DataObjectFactories.ITEM_STACK
			.toData(indexedStack.stack(), serializableData)
			.set("slot", indexedStack.slot())
	);

	public static final SerializableDataType<IndexedStack> DATA_TYPE = SerializableDataType.compound(
		DataObjectFactories.ITEM_STACK.getSerializableData().copy()
			.add("slot", ApoliDataTypes.SINGLE_SLOT_RANGE),
		data -> new IndexedStack(
			DataObjectFactories.ITEM_STACK.fromData(data),
			data.get("slot")
		),
		(indexedStack, serializableData) -> DataObjectFactories.ITEM_STACK.toData(indexedStack.stack(), serializableData)
			.set("slot", indexedStack.slot())
	);

	public IndexedStack {
		SlotRangesUtil.validateSingleSlot(slot).getOrThrow(IllegalArgumentException::new);
	}

	public int slotId() {
		return slot.getSlotIds().getFirst();
	}

}
