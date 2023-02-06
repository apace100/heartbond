package io.github.apace100.heartbond.mixin;

import io.github.apace100.heartbond.HeartList;
import io.github.apace100.heartbond.Heartbond;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class HeartListSync extends World {

    protected HeartListSync(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Inject(method = "removePlayer", at = @At("TAIL"))
    private void removeHeartFromList(ServerPlayerEntity player, Entity.RemovalReason reason, CallbackInfo ci) {
        Heartbond.getHeartUUID(player).ifPresent(uuid -> HeartList.removeFromWorld(this, uuid));
    }

    @Inject(method = "onPlayerChangeDimension", at = @At("TAIL"))
    private void addHeartToList(ServerPlayerEntity player, CallbackInfo ci) {
        Heartbond.getHeartUUID(player).ifPresent(uuid -> HeartList.addToWorld(this, uuid));
        HeartList.updateForPlayer(player);
    }

    @Inject(method = "addPlayer", at = @At("TAIL"))
    private void addHeartOnJoin(ServerPlayerEntity player, CallbackInfo ci) {
        Heartbond.getHeartUUID(player).ifPresent(uuid -> HeartList.addToWorld(player.world, uuid));
        HeartList.updateForPlayer(player);
    }
}
