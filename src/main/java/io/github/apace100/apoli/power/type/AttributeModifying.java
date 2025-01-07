package io.github.apace100.apoli.power.type;

import com.mojang.datafixers.util.Pair;
import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public interface AttributeModifying {

	List<AttributedEntityAttributeModifier> attributedModifiers();

	boolean shouldUpdateHealth();

	default void processModifiers(LivingEntity entity, BiPredicate<AttributedEntityAttributeModifier, EntityAttributeInstance> filter, BiConsumer<AttributedEntityAttributeModifier, EntityAttributeInstance> processor) {

		if (entity.getWorld().isClient()) {
			return;
		}

		float prevMaxHealth = entity.getMaxHealth();
		float prevMaxHealthPercent = entity.getHealth() / prevMaxHealth;

		attributedModifiers()
			.stream()
			.map(mod -> Pair.of(mod, entity.getAttributeInstance(mod.attribute())))
			.filter(pair -> pair.getSecond() != null)
			.filter(pair -> filter.test(pair.getFirst(), pair.getSecond()))
			.forEach(pair -> processor.accept(pair.getFirst(), pair.getSecond()));

		float currMaxHealth = entity.getMaxHealth();
		if (shouldUpdateHealth() && currMaxHealth != prevMaxHealth) {
			entity.setHealth(currMaxHealth * prevMaxHealthPercent);
		}

	}

	default void addTemporaryModifiers(LivingEntity entity) {
		processModifiers(entity, (attributedModifier, attributeInstance) -> !attributeInstance.hasModifier(attributedModifier.modifier().id()),  (attributedModifier, attributeInstance) -> attributeInstance.addTemporaryModifier(attributedModifier.modifier()));
	}

	default void addPersistentModifiers(LivingEntity entity) {
		processModifiers(entity, (attributedModifier, attributeInstance) -> !attributeInstance.hasModifier(attributedModifier.modifier().id()), (attributedModifier, attributeInstance) -> attributeInstance.addPersistentModifier(attributedModifier.modifier()));
	}

	default void removeModifiers(LivingEntity entity) {
		processModifiers(entity, (attributedModifier, attributeInstance) -> attributeInstance.hasModifier(attributedModifier.modifier().id()), (attributedModifier, attributeInstance) -> attributeInstance.removeModifier(attributedModifier.modifier()));
	}

}
