package io.github.apace100.heartbond;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.type.component.ICurio;

import java.util.UUID;

public class EnderHeartCurio implements ICurio {

    private final ItemStack stack;
    private final UUID uuid;

    public EnderHeartCurio(ItemStack stack) {
        this.stack = stack;
        if(stack.hasTag() && stack.getTag().containsUuid("HeartUUID")) {
            uuid = stack.getTag().getUuid("HeartUUID");
        } else {
            uuid = null;
        }
    }

    @Override
    public void onEquip(String identifier, int index, LivingEntity livingEntity) {
        if(uuid != null && !livingEntity.world.isClient()) {
            HeartList.addToWorld(livingEntity.world, uuid);
        }
    }

    @Override
    public void onUnequip(String identifier, int index, LivingEntity livingEntity) {
        if(uuid != null && !livingEntity.world.isClient()) {
            HeartList.removeFromWorld(livingEntity.world, uuid);
        }
    }
}
