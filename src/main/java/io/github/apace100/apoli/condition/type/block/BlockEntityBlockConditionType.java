package io.github.apace100.apoli.condition.type.block;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.BlockConditionContext;
import io.github.apace100.apoli.condition.type.BlockConditionType;
import io.github.apace100.apoli.condition.type.BlockConditionTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BlockEntityBlockConditionType extends BlockConditionType {

	@Override
	public boolean test(BlockConditionContext context) {
		return context.blockEntity().isPresent();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BlockConditionTypes.BLOCK_ENTITY;
	}

}
