package io.github.apace100.apoli.util.keybinding;

import io.github.apace100.apoli.ApoliClient;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;

import java.util.Objects;

public record KeyBindingReference(String key, boolean continuous) {

	public static final KeyBindingReference NONE = new KeyBindingReference("none");

	public static final TypedDataObjectFactory<KeyBindingReference> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("key", SerializableDataTypes.STRING)
			.add("continuous", SerializableDataTypes.BOOLEAN, false),
		data -> new KeyBindingReference(
			data.get("key"),
			data.get("continuous")
		),
		(keyBindingReference, serializableData) -> serializableData.instance()
			.set("key", keyBindingReference.key())
			.set("continuous", keyBindingReference.continuous())
	);

	public KeyBindingReference(String id) {
		this(id, false);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		else if (obj instanceof KeyBindingReference that) {
			return this.key().equals(that.key())
				&& this.continuous() == that.continuous();
		}

		else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return Objects.hash(this.key(), this.continuous());
	}

	@Environment(EnvType.CLIENT)
	public KeyBinding asKeyBinding() {
		return ApoliClient.getKeyBinding(key());
	}

}
