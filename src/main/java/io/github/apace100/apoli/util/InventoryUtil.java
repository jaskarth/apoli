package io.github.apace100.apoli.util;

import io.github.apace100.apoli.access.MutableItemStack;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.mixin.ItemSlotArgumentTypeAccessor;
import io.github.apace100.apoli.power.InventoryPower;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.util.ArgumentWrapper;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class InventoryUtil {
    private static final IntSet ALL_SLOTS = new IntOpenHashSet(113);
    private static final int[] SLOTS_ARY;
    static {
        ALL_SLOTS.add(0);
        ALL_SLOTS.add(512);
        ALL_SLOTS.add(1);
        ALL_SLOTS.add(513);
        ALL_SLOTS.add(2);
        ALL_SLOTS.add(514);
        ALL_SLOTS.add(3);
        ALL_SLOTS.add(4);
        ALL_SLOTS.add(5);
        ALL_SLOTS.add(6);
        ALL_SLOTS.add(7);
        ALL_SLOTS.add(8);
        ALL_SLOTS.add(9);
        ALL_SLOTS.add(10);
        ALL_SLOTS.add(11);
        ALL_SLOTS.add(12);
        ALL_SLOTS.add(13);
        ALL_SLOTS.add(14);
        ALL_SLOTS.add(15);
        ALL_SLOTS.add(16);
        ALL_SLOTS.add(17);
        ALL_SLOTS.add(18);
        ALL_SLOTS.add(19);
        ALL_SLOTS.add(20);
        ALL_SLOTS.add(21);
        ALL_SLOTS.add(22);
        ALL_SLOTS.add(23);
        ALL_SLOTS.add(24);
        ALL_SLOTS.add(25);
        ALL_SLOTS.add(26);
        ALL_SLOTS.add(27);
        ALL_SLOTS.add(28);
        ALL_SLOTS.add(29);
        ALL_SLOTS.add(30);
        ALL_SLOTS.add(31);
        ALL_SLOTS.add(32);
        ALL_SLOTS.add(33);
        ALL_SLOTS.add(34);
        ALL_SLOTS.add(35);
        ALL_SLOTS.add(36);
        ALL_SLOTS.add(37);
        ALL_SLOTS.add(38);
        ALL_SLOTS.add(39);
        ALL_SLOTS.add(40);
        ALL_SLOTS.add(41);
        ALL_SLOTS.add(42);
        ALL_SLOTS.add(43);
        ALL_SLOTS.add(44);
        ALL_SLOTS.add(300);
        ALL_SLOTS.add(45);
        ALL_SLOTS.add(301);
        ALL_SLOTS.add(46);
        ALL_SLOTS.add(302);
        ALL_SLOTS.add(47);
        ALL_SLOTS.add(303);
        ALL_SLOTS.add(48);
        ALL_SLOTS.add(304);
        ALL_SLOTS.add(49);
        ALL_SLOTS.add(305);
        ALL_SLOTS.add(50);
        ALL_SLOTS.add(306);
        ALL_SLOTS.add(51);
        ALL_SLOTS.add(307);
        ALL_SLOTS.add(52);
        ALL_SLOTS.add(53);
        ALL_SLOTS.add(98);
        ALL_SLOTS.add(99);
        ALL_SLOTS.add(100);
        ALL_SLOTS.add(101);
        ALL_SLOTS.add(102);
        ALL_SLOTS.add(103);
        ALL_SLOTS.add(105);
        ALL_SLOTS.add(400);
        ALL_SLOTS.add(200);
        ALL_SLOTS.add(201);
        ALL_SLOTS.add(202);
        ALL_SLOTS.add(203);
        ALL_SLOTS.add(204);
        ALL_SLOTS.add(205);
        ALL_SLOTS.add(206);
        ALL_SLOTS.add(207);
        ALL_SLOTS.add(208);
        ALL_SLOTS.add(209);
        ALL_SLOTS.add(210);
        ALL_SLOTS.add(211);
        ALL_SLOTS.add(212);
        ALL_SLOTS.add(213);
        ALL_SLOTS.add(214);
        ALL_SLOTS.add(215);
        ALL_SLOTS.add(216);
        ALL_SLOTS.add(217);
        ALL_SLOTS.add(218);
        ALL_SLOTS.add(219);
        ALL_SLOTS.add(220);
        ALL_SLOTS.add(221);
        ALL_SLOTS.add(222);
        ALL_SLOTS.add(223);
        ALL_SLOTS.add(224);
        ALL_SLOTS.add(225);
        ALL_SLOTS.add(226);
        ALL_SLOTS.add(499);
        ALL_SLOTS.add(500);
        ALL_SLOTS.add(501);
        ALL_SLOTS.add(502);
        ALL_SLOTS.add(503);
        ALL_SLOTS.add(504);
        ALL_SLOTS.add(505);
        ALL_SLOTS.add(506);
        ALL_SLOTS.add(507);
        ALL_SLOTS.add(508);
        ALL_SLOTS.add(509);
        ALL_SLOTS.add(510);
        ALL_SLOTS.add(511);
        SLOTS_ARY = ALL_SLOTS.toIntArray();
    }

    public enum InventoryType {
        INVENTORY,
        POWER
    }

    public enum ProcessMode {
        STACKS(stack -> 1),
        ITEMS(ItemStack::getCount);

        private final Function<ItemStack, Integer> processor;

        ProcessMode(Function<ItemStack, Integer> processor) {
            this.processor = processor;
        }

        public Function<ItemStack, Integer> getProcessor() {
            return processor;
        }
    }

    public static Set<Integer> getSlots(SerializableData.Instance data) {

        Set<Integer> slots = new HashSet<>();

        data.<ArgumentWrapper<Integer>>ifPresent("slot", iaw -> slots.add(iaw.get()));
        data.<List<ArgumentWrapper<Integer>>>ifPresent("slots", iaws -> slots.addAll(iaws.stream().map(ArgumentWrapper::get).toList()));

        if (slots.isEmpty()) slots.addAll(ALL_SLOTS);

        return slots;

    }

    public static int checkInventory(SerializableData.Instance data, Entity entity, @Nullable InventoryPower inventoryPower, Function<ItemStack, Integer> processor) {

        Predicate<ItemStack> itemCondition = data.get("item_condition");
        Set<Integer> slots = getSlots(data);
        deduplicateSlots(entity, slots);

        int matches = 0;
        slots.removeIf(slot -> slotNotWithinBounds(entity, inventoryPower, slot));
        for (int slot : slots) {

            ItemStack stack = getStack(entity, inventoryPower, slot);
            if ((itemCondition == null && !stack.isEmpty()) || (itemCondition == null || itemCondition.test(stack))) {
                matches += processor.apply(stack);
            }

        }

        return matches;

    }

    public static void modifyInventory(SerializableData.Instance data, Entity entity, InventoryPower inventoryPower, Function<ItemStack, Integer> processor, int limit) {

        if(limit <= 0) {
            limit = Integer.MAX_VALUE;
        }

        Set<Integer> slots = getSlots(data);
        deduplicateSlots(entity, slots);

        Consumer<Entity> entityAction = data.get("entity_action");
        Predicate<ItemStack> itemCondition = data.get("item_condition");
        ActionFactory<Pair<World, ItemStack>>.Instance itemAction = data.get("item_action");

        int processedItems = 0;
        slots.removeIf(slot -> slotNotWithinBounds(entity, inventoryPower, slot));

        modifyingItemsLoop:
        for (int slot : slots) {

            ItemStack stack = getStack(entity, inventoryPower, slot);
            if (stack.isEmpty() || !(itemCondition == null || itemCondition.test(stack))) {
                continue;
            }

            int amount = processor.apply(stack);
            for (int i = 0; i < amount; i++) {

                if (entityAction != null) {
                    entityAction.accept(entity);
                }

                itemAction.accept(new Pair<>(entity.getWorld(), stack));
                ++processedItems;

                if (processedItems >= limit) {
                    break modifyingItemsLoop;
                }

            }

        }

    }

    public static void replaceInventory(SerializableData.Instance data, Entity entity, InventoryPower inventoryPower) {

        Set<Integer> slots = getSlots(data);
        deduplicateSlots(entity, slots);

        Consumer<Entity> entityAction = data.get("entity_action");
        Predicate<ItemStack> itemCondition = data.get("item_condition");
        Consumer<Pair<World, ItemStack>> itemAction = data.get("item_action");

        ItemStack replacementStack = data.get("stack");
        boolean mergeNbt = data.getBoolean("merge_nbt");

        slots.removeIf(slot -> slotNotWithinBounds(entity, inventoryPower, slot));
        for (int slot : slots) {

            ItemStack stack = getStack(entity, inventoryPower, slot);
            if (!(itemCondition == null || itemCondition.test(stack))) {
                continue;
            }

            if (entityAction != null) {
                entityAction.accept(entity);
            }

            ItemStack stackAfterReplacement = replacementStack.copy();
            if (mergeNbt && stack.hasNbt()) {
                stack.getOrCreateNbt().copyFrom(stackAfterReplacement.getOrCreateNbt());
                stackAfterReplacement.setNbt(stack.getOrCreateNbt());
            }

            setStack(entity, inventoryPower, stackAfterReplacement, slot);
            if (itemAction != null) {
                itemAction.accept(new Pair<>(entity.getWorld(), stackAfterReplacement));
            }

        }

    }

    public static void dropInventory(SerializableData.Instance data, Entity entity, InventoryPower inventoryPower) {

        Set<Integer> slots = getSlots(data);
        deduplicateSlots(entity, slots);

        int amount = data.getInt("amount");
        boolean throwRandomly = data.getBoolean("throw_randomly");
        boolean retainOwnership = data.getBoolean("retain_ownership");

        Consumer<Entity> entityAction = data.get("entity_action");
        Predicate<ItemStack> itemCondition = data.get("item_condition");
        Consumer<Pair<World, ItemStack>> itemAction = data.get("item_action");

        slots.removeIf(slot -> slotNotWithinBounds(entity, inventoryPower, slot));
        for (int slot : slots) {

            ItemStack stack = getStack(entity, inventoryPower, slot);
            if (stack.isEmpty() || !(itemCondition == null || itemCondition.test(stack))) {
                continue;
            }

            if (entityAction != null) {
                entityAction.accept(entity);
            }

            if (itemAction != null) {
                itemAction.accept(new Pair<>(entity.getWorld(), stack));
            }

            ItemStack droppedStack = ItemStack.EMPTY;
            if (amount != 0) {
                int newAmount = amount < 0 ? amount * -1 : amount;
                droppedStack = stack.split(newAmount);
            }

            throwItem(entity, droppedStack.isEmpty() ? stack : droppedStack, throwRandomly, retainOwnership);
            setStack(entity, inventoryPower, droppedStack.isEmpty() ? ItemStack.EMPTY : stack, slot);

        }

    }

    public static void throwItem(Entity thrower, ItemStack itemStack, boolean throwRandomly, boolean retainOwnership) {

        if (itemStack.isEmpty()) {
            return;
        }

        if (thrower instanceof PlayerEntity playerEntity && playerEntity.getWorld().isClient) {
            playerEntity.swingHand(Hand.MAIN_HAND);
        }

        double yOffset = thrower.getEyeY() - 0.30000001192092896D;
        ItemEntity itemEntity = new ItemEntity(thrower.getWorld(), thrower.getX(), yOffset, thrower.getZ(), itemStack);
        itemEntity.setPickupDelay(40);

        Random random = new Random();

        float f;
        float g;

        if (retainOwnership) itemEntity.setThrower(thrower.getUuid());
        if (throwRandomly) {
            f = random.nextFloat() * 0.5F;
            g = random.nextFloat() * 6.2831855F;
            itemEntity.setVelocity(- MathHelper.sin(g) * f, 0.20000000298023224D, MathHelper.cos(g) * f);
        }
        else {
            f = 0.3F;
            g = MathHelper.sin(thrower.getPitch() * 0.017453292F);
            float h = MathHelper.cos(thrower.getPitch() * 0.017453292F);
            float i = MathHelper.sin(thrower.getYaw() * 0.017453292F);
            float j = MathHelper.cos(thrower.getYaw() * 0.017453292F);
            float k = random.nextFloat() * 6.2831855F;
            float l = 0.02F * random.nextFloat();
            itemEntity.setVelocity(
                (double) (- i * h * f) + Math.cos(k) * (double) l,
                (-g * f + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F),
                (double) (j * h * f) + Math.sin(k) * (double) l
            );
        }

        thrower.getWorld().spawnEntity(itemEntity);

    }

    private static final Map<Entity, ItemStack> ENTITY_EMPTY_STACK_MAP = new WeakHashMap<>();

     public static ItemStack getEntityLinkedEmptyStack(Entity entity) {
        // A crack in the earth opens. Peering down I can see the molten magma, the same shade of orange as the midday sky.
        // Nothing of value lies beyond here.

//        return ENTITY_EMPTY_STACK_MAP.computeIfAbsent(entity, e -> new ItemStack((Void) null));
        return new ItemStack((Void) null);
     }

    public static void forEachStack(Entity entity, Consumer<ItemStack> itemStackConsumer) {
        forEachStack(entity, itemStackConsumer, null);
    }

    public static void forEachStack(Entity entity, Consumer<ItemStack> itemStackConsumer, @Nullable Consumer<ItemStack> emptyStackConsumer) {

        int slotToSkip = getDuplicatedSlotIndex(entity);
        ItemStack linkedStack = null;
        for (int slot : SLOTS_ARY) {
//        for (int slot : ItemSlotArgumentTypeAccessor.getSlotMappings().values()) {

            if (slot == slotToSkip) {
                slotToSkip = Integer.MIN_VALUE;
                continue;
            }

            StackReference stackReference = entity.getStackReference(slot);
            if (stackReference == StackReference.EMPTY) {
                continue;
            }

            ItemStack stack = stackReference.get();

            if (linkedStack == null) linkedStack = getEntityLinkedEmptyStack(entity);

            if (!stack.isEmpty() || emptyStackConsumer != null && stack == linkedStack) {
                itemStackConsumer.accept(stack);
                continue;
            }

            if (emptyStackConsumer == null) {
                continue;
            }

            if (stack == ItemStack.EMPTY) {
                ItemStack newStack = linkedStack;
                emptyStackConsumer.accept(newStack);

                ((MutableItemStack) stack).apoli$setFrom(newStack);
            }

        }

        PowerHolderComponent component = PowerHolderComponent.KEY.maybeGet(entity).orElse(null);
        if (component == null) {
            return;
        }

        List<InventoryPower> inventoryPowers = component.getPowers(InventoryPower.class);
        for (InventoryPower inventoryPower : inventoryPowers) {
            for (int index = 0; index < inventoryPower.size(); index++) {

                ItemStack stack = inventoryPower.getStack(index);
                if (!stack.isEmpty()) {
                    itemStackConsumer.accept(stack);
                    continue;
                }

                if (emptyStackConsumer == null) {
                    continue;
                }

                if (linkedStack == null) linkedStack = getEntityLinkedEmptyStack(entity);

                ItemStack newStack = linkedStack;
                emptyStackConsumer.accept(newStack);

            }
        }

    }

    private static void deduplicateSlots(Entity entity, Set<Integer> slots) {
        int hotbarSlot = getDuplicatedSlotIndex(entity);
        if(hotbarSlot != Integer.MIN_VALUE && slots.contains(hotbarSlot)) {
            Integer mainHandSlot = 98;
            slots.remove(mainHandSlot);
        }
    }

    /**
     *      <p>For players, their selected hotbar slot will overlap with the `weapon.mainhand` slot reference. This
     *      method returns the slot ID of the selected hotbar slot.</p>
     *
     *      @param entity   The entity to get the slot ID of its selected hotbar slot
     *      @return         The slot ID of the hotbar slot or {@link Integer#MIN_VALUE} if the entity is not a player
     */
    private static int getDuplicatedSlotIndex(Entity entity) {
        if(entity instanceof PlayerEntity player) {
            int selectedSlot = player.getInventory().selectedSlot;
            return selectedSlot;
        }
        return Integer.MIN_VALUE;
    }

    /**
     *      <p>Check whether the specified slot is <b>not</b> within the bounds of the entity's {@linkplain
     *      StackReference stack reference} or the specified {@link InventoryPower}.</p>
     *
     *      @param entity           The entity check the bounds of its {@linkplain StackReference stack reference}
     *      @param inventoryPower   The {@link InventoryPower} to check the bounds of
     *      @param slot             The slot
     *      @return                 {@code true} if the slot is within the bounds of the {@linkplain
     *      StackReference stack reference} or the {@link InventoryPower}
     */
    public static boolean slotNotWithinBounds(Entity entity, @Nullable InventoryPower inventoryPower, int slot) {
        return inventoryPower == null ? entity.getStackReference(slot) == StackReference.EMPTY
            : slot < 0 || slot >= inventoryPower.size();
    }

    /**
     *      <p>Get the item stack from the entity's {@linkplain StackReference stack reference} or the inventory of
     *      the specified {@link InventoryPower} (if it's not null).</p>
     *
     *      <p><b>Make sure to only call this method after you filter out the slots that aren't within the bounds
     *      of the entity's {@linkplain StackReference stack reference} or {@link InventoryPower} using {@link
     *      #slotNotWithinBounds(Entity, InventoryPower, int)}</b></p>
     *
     *      @param entity            The entity to get the item stack from its {@linkplain StackReference stack reference}
     *      @param inventoryPower    The {@link InventoryPower} to get the item stack from (can be null)
     *      @param slot              The (numerical) slot to get the item stack from
     *      @return                  The item stack from the specified slot
     */
    public static ItemStack getStack(Entity entity, @Nullable InventoryPower inventoryPower, int slot) {
        return inventoryPower == null ? entity.getStackReference(slot).get() : inventoryPower.getStack(slot);
    }

    /**
     *      <p>Set the item stack on the specified slot of the entity's {@linkplain StackReference stack reference}
     *      or the inventory of the specified {@link InventoryPower} (if it's not null).</p>
     *
     *      <p><b>Make sure to only call this method after you filter out the slots that aren't within the bounds
     *      of the entity's {@linkplain StackReference stack reference} or {@link InventoryPower} using {@link
     *      #slotNotWithinBounds(Entity, InventoryPower, int)}</b></p>
     *
     *      @param entity           The entity to modify the {@linkplain StackReference stack reference} of
     *      @param inventoryPower   The {@link InventoryPower} to set the item stack to (can be null)
     *      @param stack            The item stack to set to the specified slot
     *      @param slot             The (numerical) slot to set the item stack to
     */
    public static void setStack(Entity entity, InventoryPower inventoryPower, ItemStack stack, int slot) {
        if (inventoryPower == null) {
            entity.getStackReference(slot).set(stack);
        } else {
            inventoryPower.setStack(slot, stack);
        }
    }

}
