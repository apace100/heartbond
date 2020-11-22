package io.github.apace100.heartbond;

import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import nerdhub.cardinal.components.api.event.ItemComponentCallbackV2;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tag.FabricTag;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosComponent;
import top.theillusivec4.curios.api.SlotTypeInfo;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.component.ICurio;

import java.util.Optional;
import java.util.UUID;

public class Heartbond implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger(Heartbond.class);

	public static final EnderHeartItem HEART = new EnderHeartItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
	public static final EnderSoulItem SOUL = new EnderSoulItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE));

	public static final Tag<Item> SOUL_CRAFTING_ITEMS = TagRegistry.item(new Identifier("heartbond", "soul_crafting_items"));

	public static final Identifier PACKET_HEART_LIST_UPDATE = new Identifier("heartbond", "heart_list_update");
	public static final Identifier PACKET_HEART_LIST_ADD = new Identifier("heartbond", "heart_list_add");
	public static final Identifier PACKET_HEART_LIST_REMOVE = new Identifier("heartbond", "heart_list_remove");

	public static Optional<UUID> getHeartUUID(PlayerEntity player) {
		Optional<ImmutableTriple<String, Integer, ItemStack>> curio = CuriosApi.getCuriosHelper().findEquippedCurio(Heartbond.HEART, player);
		if(curio.isPresent()) {
			ItemStack heart = curio.get().right;
			if (heart.hasTag() && heart.getTag().containsUuid("HeartUUID")) {
				return Optional.of(heart.getTag().getUuid("HeartUUID"));
			}
		}
		return Optional.empty();
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Heartbond was initialized. Enjoy bonding!");
		SoulRecipe.SERIALIZER.toString();
		CuriosApi.enqueueSlotType(SlotTypeInfo.BuildScheme.REGISTER,
			new SlotTypeInfo.Builder("ender_heart")
			.icon(new Identifier("heartbond", "item/empty_ender_heart_slot"))
			.size(1)
			.build()
		);
		Registry.register(Registry.ITEM, new Identifier("heartbond:ender_heart"), HEART);
		Registry.register(Registry.ITEM, new Identifier("heartbond:ender_soul"), SOUL);
		ItemComponentCallbackV2.event(HEART).register(
			((item, itemStack, componentContainer) -> componentContainer
				.put(CuriosComponent.ITEM, new EnderHeartCurio(itemStack))));

	}
}
