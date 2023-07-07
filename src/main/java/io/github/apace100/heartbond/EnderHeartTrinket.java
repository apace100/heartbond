package io.github.apace100.heartbond;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class EnderHeartTrinket implements Trinket {

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        Optional<UUID> uuid = EnderHeartItem.getHeartUUID(stack);
        if(uuid.isPresent() && !entity.getWorld().isClient()) {
            HeartList.addToWorld(entity.getWorld(), uuid.get());
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        Optional<UUID> uuid = EnderHeartItem.getHeartUUID(stack);
        if(uuid.isPresent() && !entity.getWorld().isClient()) {
            HeartList.removeFromWorld(entity.getWorld(), uuid.get());
        }
    }
}
