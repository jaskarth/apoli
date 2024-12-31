package io.github.apace100.apoli.power.type;

import io.github.apace100.apoli.ApoliClient;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.util.keybinding.KeyBindingReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface Active {

    KeyBindingReference getKey();

    void onUse();

    @Environment(EnvType.CLIENT)
    static <P extends PowerType & Active> void integrateCallback(MinecraftClient client) {

        if (client.player == null) {
            return;
        }

        List<PowerType> powerTypes = PowerHolderComponent.getOptional(client.player).orElseThrow().getPowerTypes();
        List<P> triggeredPowerTypes = new LinkedList<>();

        Map<String, Boolean> currentKeybindingStates = new HashMap<>();
        for (PowerType powerType : powerTypes) {

            if (!(powerType instanceof Active activePowerType)) {
                continue;
            }

            KeyBindingReference keyBindingReference = activePowerType.getKey();
            KeyBinding keyBinding = keyBindingReference.asKeyBinding();

            if (keyBinding == null) {
                continue;
            }

            if (currentKeybindingStates.computeIfAbsent(keyBindingReference.key(), k -> keyBinding.isPressed()) && (keyBindingReference.continuous() || !ApoliClient.lastKeyBindingStates.getOrDefault(keyBindingReference.key(), false))) {
				//noinspection unchecked
				triggeredPowerTypes.add((P) powerType);
            }

        }

        ApoliClient.lastKeyBindingStates.putAll(currentKeybindingStates);
        ApoliClient.performActivePowerTypes(triggeredPowerTypes);

    }

}
