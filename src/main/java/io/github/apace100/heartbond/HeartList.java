package io.github.apace100.heartbond;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.*;

public final class HeartList {

    private static final HashMap<World, Set<UUID>> list = new HashMap<>();

    public static void addToWorld(World world, UUID uuid) {
        if(world.isClient) {
            return;
        }
        if(uuid == null) {
            Heartbond.LOGGER.warn("Null UUID detected! Discarding.");
            return;
        }
        Set<UUID> uuids;
        if(list.containsKey(world)) {
            uuids = list.get(world);
        } else {
            uuids = new HashSet<>();
            list.put(world, uuids);
        }
        uuids.add(uuid);
        world.getPlayers().forEach(player -> {
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            buffer.writeUuid(uuid);
            ServerPlayNetworking.send((ServerPlayerEntity)player, Heartbond.PACKET_HEART_LIST_ADD, buffer);
        });
    }

    public static void removeFromWorld(World world, UUID uuid) {
        if(world.isClient) {
            return;
        }
        if(list.containsKey(world)) {
            Set<UUID> uuids = list.get(world);
            uuids.remove(uuid);
            world.getPlayers().forEach(player -> {
                PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
                buffer.writeUuid(uuid);
                ServerPlayNetworking.send((ServerPlayerEntity)player, Heartbond.PACKET_HEART_LIST_REMOVE, buffer);
            });
        }
    }

    public static void updateForPlayer(PlayerEntity player) {
            if (player instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                Set<UUID> uuids;
                if (list.containsKey(player.world)) {
                    uuids = list.get(player.world);
                } else {
                    uuids = new HashSet<>();
                }
                PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
                buffer.writeInt(uuids.size());
                uuids.forEach(buffer::writeUuid);
                ServerPlayNetworking.send(serverPlayer, Heartbond.PACKET_HEART_LIST_UPDATE, buffer);
            }
    }
}
