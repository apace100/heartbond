package io.github.apace100.heartbond;

import io.netty.buffer.ByteBufUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.mixin.object.builder.ModelPredicateProviderRegistryAccessor;
import net.fabricmc.fabric.mixin.object.builder.ModelPredicateProviderRegistrySpecificAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.*;

public class HeartbondClient implements ClientModInitializer {

    public static final Set<UUID> HEART_LIST = new HashSet<>();

    @Override
    @Environment(EnvType.CLIENT)
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
        ClientSidePacketRegistry.INSTANCE.register(Heartbond.PACKET_TELEPORT_EVENT, (context, buffer) -> {
            Vec3d from = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            Vec3d to = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            context.getTaskQueue().execute(() -> {
                World world = MinecraftClient.getInstance().world;
                Random random = new Random();
                if(world != null) {
                    world.playSound(from.getX(), from.getY(), from.getZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1F, 1F, false);
                    world.playSound(to.getX(), to.getY(), to.getZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1F, 1F, false);
                    for(int i = 0; i < 48; ++i) {
                        world.addParticle(ParticleTypes.PORTAL, from.getX(), from.getY() + random.nextDouble() * 2.0D, from.getZ(), random.nextGaussian(), 0.0D, random.nextGaussian());
                        world.addParticle(ParticleTypes.PORTAL, to.getX(), to.getY() + random.nextDouble() * 2.0D, to.getZ(), random.nextGaussian(), 0.0D, random.nextGaussian());
                    }
                }
            });
        });
    }
}
