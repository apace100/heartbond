package io.github.apace100.heartbond;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.mixin.object.builder.ModelPredicateProviderRegistryAccessor;
import net.fabricmc.fabric.mixin.object.builder.ModelPredicateProviderRegistrySpecificAccessor;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.*;

public class HeartbondClient implements ClientModInitializer {

    public static final Set<UUID> HEART_LIST = new HashSet<>();

    @Override
    public void onInitializeClient() {
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)
            .register(((spriteAtlasTexture, registry) -> {
                registry.register(new Identifier("heartbond:item/empty_ender_heart_slot"));
            }));
        ModelPredicateProviderRegistrySpecificAccessor.callRegister(Heartbond.SOUL, new Identifier("active"), (itemStack, clientWorld, livingEntity) -> {
            if(livingEntity instanceof PlayerEntity) {
                PlayerEntity user = (PlayerEntity)livingEntity;
                Optional<UUID> userHeart = Heartbond.getHeartUUID(user);
                if(userHeart.isPresent()) {
                    Optional<UUID> pair = EnderSoulItem.getPairedUUID(itemStack, userHeart.get());
                    if(pair.isPresent()) {
                        return HeartbondClient.HEART_LIST.contains(pair.get()) ? 1.0F : 0.0F;
                    }
                }
            }
            return 0.0F;
        });
        ClientSidePacketRegistry.INSTANCE.register(Heartbond.PACKET_HEART_LIST_UPDATE, (context, buffer) -> {
            HEART_LIST.clear();
            int count = buffer.readInt();
            List<UUID> uuids = new ArrayList<>(count);
            for(int i = 0; i < count; i++) {
                uuids.add(buffer.readUuid());
            }
            context.getTaskQueue().execute(() -> {
                HEART_LIST.addAll(uuids);
            });
        });
        ClientSidePacketRegistry.INSTANCE.register(Heartbond.PACKET_HEART_LIST_ADD, (context, buffer) -> {
            UUID uuid = buffer.readUuid();
            context.getTaskQueue().execute(() -> HEART_LIST.add(uuid));
        });
        ClientSidePacketRegistry.INSTANCE.register(Heartbond.PACKET_HEART_LIST_REMOVE, (context, buffer) -> {
            UUID uuid = buffer.readUuid();
            context.getTaskQueue().execute(() -> HEART_LIST.remove(uuid));
        });
    }
}
