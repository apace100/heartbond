package io.github.apace100.heartbond.mixin;

import io.github.apace100.heartbond.HeartList;
import io.github.apace100.heartbond.Heartbond;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class HeartListTest {

    @Inject(method = "respawnPlayer", at = @At("TAIL"))
    private void syncListOnJoin(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
       // Heartbond.getHeartUUID(player).ifPresent(uuid -> HeartList.addToWorld(player.world, uuid));
        //HeartList.updateForPlayer(player);
    }
}
