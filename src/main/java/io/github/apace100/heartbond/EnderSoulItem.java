package io.github.apace100.heartbond;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;
import java.util.UUID;

public class EnderSoulItem extends Item {

    public EnderSoulItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity livingUser) {
        if(livingUser instanceof PlayerEntity) {
            PlayerEntity user = (PlayerEntity)livingUser;
            Optional<PlayerEntity> bond = getBond(stack, user);
            if(bond.isPresent()) {
                user.teleport(bond.get().getX(), bond.get().getY(), bond.get().getZ());
                user.getItemCooldownManager().set(this, 100);
            } else {
                user.getItemCooldownManager().set(this, 20);
            }
        }
        return stack;
    }

    public boolean isActive(ItemStack stack, PlayerEntity user) {
        return getBond(stack, user).isPresent();
    }

    @SuppressWarnings("unchecked")
    public Optional<PlayerEntity> getBond(ItemStack stack, PlayerEntity user) {
        Pair<UUID, UUID> bond = getBoundUUIDs(stack);
        Optional<UUID> userHeartUUID = Heartbond.getHeartUUID(user);
        if(userHeartUUID.isPresent()) {
            UUID targetHeartUUID = null;
            if(bond.getLeft().equals(userHeartUUID.get())) {
                targetHeartUUID = bond.getRight();
            } else if(bond.getRight().equals(userHeartUUID.get())) {
                targetHeartUUID = bond.getLeft();
            }
            if(targetHeartUUID == null) {
                return Optional.empty();
            }
            UUID finalTargetHeartUUID = targetHeartUUID;
            return (Optional<PlayerEntity>) user.world.getPlayers().stream().filter(otherPlayer -> {
                Optional<UUID> otherPlayerHeartUUID = Heartbond.getHeartUUID(otherPlayer);
                return otherPlayerHeartUUID.map(uuid -> uuid.equals(finalTargetHeartUUID)).orElse(false);
            }).findFirst();
        }
        return Optional.empty();
    }

    public Pair<UUID, UUID> getBoundUUIDs(ItemStack stack) {
        CompoundTag heartbonds = stack.getSubTag("Heartbonds");
        if(heartbonds == null) {
            return new Pair<>(null, null);
        }
        return new Pair<>(heartbonds.getUuid("Bond0"), heartbonds.getUuid("Bond1"));
    }
}
