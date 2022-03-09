package io.github.stuff_stuffs.linedrawexample.entity;

import io.github.stuff_stuffs.linedrawexample.mixin.WorldInvoker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

//Living entity so i dont have to manually create the sync packets
public class LineAnchorEntity extends LivingEntity {
    //tracked data to track the holder of this leash
    private static final TrackedData<Optional<UUID>> HOLDER;

    public LineAnchorEntity(final EntityType<? extends LivingEntity> entityType, final World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(HOLDER, Optional.empty());
    }

    public @Nullable UUID getHolderUuid() {
        return dataTracker.get(HOLDER).orElse(null);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return Collections.emptySet();
    }

    @Override
    public ItemStack getEquippedStack(final EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(final EquipmentSlot slot, final ItemStack stack) {

    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    public ActionResult interact(final PlayerEntity player, final Hand hand) {
        dataTracker.set(HOLDER, Optional.of(player.getUuid()));
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean shouldRender(final double cameraX, final double cameraY, final double cameraZ) {
        final UUID holderUuid = getHolderUuid();
        if (holderUuid != null) {
            final Entity otherEntity = ((WorldInvoker) world).getEntityLookup().get(uuid);
            if (otherEntity != null) {
                return super.shouldRender(cameraX, cameraY, cameraZ) || otherEntity.shouldRender(cameraX, cameraY, cameraZ);
            }
        }
        return super.shouldRender(cameraX, cameraY, cameraZ);
    }

    static {
        HOLDER = DataTracker.registerData(LineAnchorEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    }
}
