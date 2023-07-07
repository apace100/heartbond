package io.github.apace100.heartbond;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class EnderSoulItem extends Item {

    public EnderSoulItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
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
        if(livingUser instanceof PlayerEntity user && !livingUser.getWorld().isClient()) {
            Optional<PlayerEntity> bond = getBond(stack, user);
            if(bond.isPresent()) {
                PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
                buffer.writeDouble(user.getX());
                buffer.writeDouble(user.getY());
                buffer.writeDouble(user.getZ());
                PlayerEntity target = bond.get();
                double targetX = target.getX();
                double targetY = target.getY();
                double targetZ = target.getZ();
                user.teleport(targetX, targetY, targetZ);
                buffer.writeDouble(targetX);
                buffer.writeDouble(targetY);
                buffer.writeDouble(targetZ);
                world.getPlayers().forEach(playerEntity -> ServerPlayNetworking.send((ServerPlayerEntity)playerEntity, Heartbond.PACKET_TELEPORT_EVENT, buffer));
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
        Optional<UUID> userHeartUUID = Heartbond.getHeartUUID(user);
        if(userHeartUUID.isPresent()) {
            Optional<UUID> targetHeartUUID = getPairedUUID(stack, userHeartUUID.get());
            if(targetHeartUUID.isPresent()) {
                UUID finalTargetHeartUUID = targetHeartUUID.get();
                return (Optional<PlayerEntity>) user.getWorld().getPlayers().stream().filter(otherPlayer -> {
                    Optional<UUID> otherPlayerHeartUUID = Heartbond.getHeartUUID(otherPlayer);
                    return otherPlayerHeartUUID.map(uuid -> uuid.equals(finalTargetHeartUUID)).orElse(false);
                }).findFirst();
            }
        }
        return Optional.empty();
    }

    public static Pair<UUID, UUID> getBoundUUIDs(ItemStack stack) {
        NbtCompound heartbonds = stack.getSubNbt("Heartbonds");
        if(heartbonds == null) {
            return new Pair<>(null, null);
        }
        return new Pair<>(heartbonds.getUuid("Bond0"), heartbonds.getUuid("Bond1"));
    }

    public static Optional<UUID> getPairedUUID(ItemStack stack, UUID userUuid) {
        Pair<UUID, UUID> bond = getBoundUUIDs(stack);
        UUID targetHeartUUID = null;
        if(bond.getLeft().equals(userUuid)) {
            targetHeartUUID = bond.getRight();
        } else if(bond.getRight().equals(userUuid)) {
            targetHeartUUID = bond.getLeft();
        }
        return Optional.ofNullable(targetHeartUUID);
    }
}
