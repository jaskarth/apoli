package io.github.apace100.apoli.util;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.registry.DataObjectFactories;
import net.minecraft.inventory.SlotRange;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public record IndexedStack(ItemStack stack, Optional<SlotRange> slot) {

	public static final TypedDataObjectFactory<IndexedStack> DATA_FACTORY = TypedDataObjectFactory.simple(
		DataObjectFactories.ITEM_STACK.getSerializableData().copy()
			.add("slot", ApoliDataTypes.SINGLE_SLOT_RANGE.optional(), Optional.empty()),
		data -> new IndexedStack(
			DataObjectFactories.ITEM_STACK.fromData(data),
			data.get("slot")
		),
		(indexedStack, serializableData) -> DataObjectFactories.ITEM_STACK
			.toData(indexedStack.stack(), serializableData)
			.set("slot", indexedStack.slot())
	);

	public static final SerializableDataType<IndexedStack> DATA_TYPE = DATA_FACTORY.getDataType();

	public IndexedStack {
		slot.map(SlotRangesUtil::validateSingleSlot).map(result -> result.getOrThrow(IllegalArgumentException::new));
	}

	public Optional<Integer> slotId() {
		return slot.map(slotRange -> slotRange.getSlotIds().getFirst());
	}

}
