package io.github.apace100.heartbond;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.mixin.object.builder.ModelPredicateProviderRegistryAccessor;
import net.fabricmc.fabric.mixin.object.builder.ModelPredicateProviderRegistrySpecificAccessor;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypePreset;

public class HeartbondClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)
            .register(((spriteAtlasTexture, registry) -> {
                registry.register(new Identifier("heartbond:item/empty_ender_heart_slot"));
            }));
        ModelPredicateProviderRegistrySpecificAccessor.callRegister(Heartbond.SOUL, new Identifier("active"), (itemStack, clientWorld, livingEntity) -> {
            return (livingEntity instanceof PlayerEntity && Heartbond.SOUL.isActive(itemStack, (PlayerEntity)livingEntity)) ? 1.0F : 0.0F;
        });
    }
}
