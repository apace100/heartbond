package io.github.apace100.heartbond;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.mixin.object.builder.client.ModelPredicateProviderRegistrySpecificAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class HeartbondClient implements ClientModInitializer {

    public static final Set<UUID> HEART_LIST = new HashSet<>();

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        /*ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)
            .register(((spriteAtlasTexture, registry) -> {
                registry.register(new Identifier("heartbond:item/empty_ender_heart_slot"));
            }));
        */ModelPredicateProviderRegistrySpecificAccessor.callRegister(Heartbond.SOUL, new Identifier("active"), (stack, world, entity, seed) -> {
            if(entity instanceof PlayerEntity) {
                PlayerEntity user = (PlayerEntity)entity;
                Optional<UUID> userHeart = Heartbond.getHeartUUID(user);
                if(userHeart.isPresent()) {
                    Optional<UUID> pair = EnderSoulItem.getPairedUUID(stack, userHeart.get());
                    if(pair.isPresent()) {
                        return HeartbondClient.HEART_LIST.contains(pair.get()) ? 1.0F : 0.0F;
                    }
                }
            }
            return 0.0F;
        });
        ClientPlayNetworking.registerGlobalReceiver(Heartbond.PACKET_HEART_LIST_UPDATE, (client, handler, buf, responseSender) -> {
                HEART_LIST.clear();
                int count = buf.readInt();
                List<UUID> uuids = new ArrayList<>(count);
                for(int i = 0; i < count; i++) {
                    uuids.add(buf.readUuid());
                }
                client.execute(() -> {
                    HEART_LIST.addAll(uuids);
                });
        });
        ClientPlayNetworking.registerGlobalReceiver(Heartbond.PACKET_HEART_LIST_ADD, (client, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            client.execute(() -> HEART_LIST.add(uuid));
        });
        ClientPlayNetworking.registerGlobalReceiver(Heartbond.PACKET_HEART_LIST_REMOVE, (client, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            client.execute(() -> HEART_LIST.remove(uuid));
        });
        ClientPlayNetworking.registerGlobalReceiver(Heartbond.PACKET_TELEPORT_EVENT, (client, handler, buf, responseSender) -> {
            Vec3d from = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            Vec3d to = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            client.execute(() -> {
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
