package io.github.apace100.apoli.action.type.item.meta;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.ItemAction;
import io.github.apace100.apoli.action.context.ItemActionContext;
import io.github.apace100.apoli.action.type.ItemActionType;
import io.github.apace100.apoli.action.type.ItemActionTypes;
import io.github.apace100.apoli.action.type.meta.SequenceMetaActionType;
import net.minecraft.inventory.StackReference;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SequenceItemActionType extends ItemActionType implements SequenceMetaActionType<ItemActionContext, ItemAction> {

	private final List<ItemAction> actions;

	public SequenceItemActionType(List<ItemAction> actions) {
		this.actions = actions;
	}

	@Override
	protected void execute(World world, StackReference stackReference) {
		executeActions(new ItemActionContext(world, stackReference));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return ItemActionTypes.SEQUENCE;
	}

	@Override
	public List<ItemAction> actions() {
		return actions;
	}

}
