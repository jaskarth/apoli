package io.github.apace100.apoli.action.type.entity.meta;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.action.type.EntityActionTypes;
import io.github.apace100.apoli.action.type.meta.SequenceMetaActionType;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SequenceEntityActionType extends EntityActionType implements SequenceMetaActionType<EntityActionContext, EntityAction> {

	private final List<EntityAction> actions;

	public SequenceEntityActionType(List<EntityAction> actions) {
		this.actions = actions;
	}

	@Override
	protected void execute(Entity entity) {
		executeActions(new EntityActionContext(entity));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.SEQUENCE;
	}

	@Override
	public List<EntityAction> actions() {
		return actions;
	}

}
