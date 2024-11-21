package io.github.apace100.apoli.action.type.entity.meta;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.action.type.EntityActionTypes;
import io.github.apace100.apoli.action.type.meta.NothingMetaActionType;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class NothingEntityActionType extends EntityActionType implements NothingMetaActionType {

	@Override
	protected void execute(Entity entity) {

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.NOTHING;
	}

}
