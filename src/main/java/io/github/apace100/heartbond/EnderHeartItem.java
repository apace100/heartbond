package io.github.apace100.heartbond;

import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class EnderHeartItem extends TrinketItem {

    public EnderHeartItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        stack.getOrCreateNbt().putUuid("HeartUUID", UUID.randomUUID());
    }

    public static Optional<UUID> getHeartUUID(ItemStack stack) {
        if(stack.getItem() instanceof EnderHeartItem) {
            if (stack.hasNbt() && stack.getNbt().containsUuid("HeartUUID")) {
                return Optional.of(stack.getNbt().getUuid("HeartUUID"));
            }
        }
        return Optional.empty();
    }
}
