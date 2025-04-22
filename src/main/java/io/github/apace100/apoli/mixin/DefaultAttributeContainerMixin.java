package io.github.apace100.apoli.mixin;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.apace100.apoli.access.OwnableAttributeContainer;
import io.github.apace100.apoli.access.OwnableAttributeInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DefaultAttributeContainer.class)
public abstract class DefaultAttributeContainerMixin implements OwnableAttributeContainer {

    @Unique
    private static final ThreadLocal<Cache<DefaultAttributeContainerMixin, Entity>> apoli$ownerMap =
            ThreadLocal.withInitial(() -> CacheBuilder
                    .newBuilder()
                    .concurrencyLevel(1)
                    .weakKeys()
                    .weakValues()
                    .build()
            );

    @Override
    @Nullable
    public Entity apoli$getOwner() {
        return apoli$ownerMap.get().getIfPresent(this);
    }

    @Override
    public void apoli$setOwner(Entity owner) {
        if (owner != null) {
            apoli$ownerMap.get().put(this, owner);
        } else {
            apoli$ownerMap.get().invalidate(this);
        }
    }

    @ModifyExpressionValue(method = "getValue", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/DefaultAttributeContainer;require(Lnet/minecraft/registry/entry/RegistryEntry;)Lnet/minecraft/entity/attribute/EntityAttributeInstance;"))
    private EntityAttributeInstance apoli$setAttributeInstanceOwner(EntityAttributeInstance original) {

        if (original instanceof OwnableAttributeInstance ownableAttributeInstance) {
            ownableAttributeInstance.apoli$setOwner(this.apoli$getOwner());
        }

        return original;

    }

}
