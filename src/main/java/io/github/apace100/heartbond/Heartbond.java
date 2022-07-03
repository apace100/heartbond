package io.github.apace100.heartbond;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Heartbond implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger(Heartbond.class);

	public static final EnderHeartItem HEART = new EnderHeartItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
	public static final EnderSoulItem SOUL = new EnderSoulItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE));

	public static final TagKey<Item> SOUL_CRAFTING_ITEMS = TagKey.of(Registry.ITEM_KEY, new Identifier("heartbond", "soul_crafting_items"));

	public static final Identifier PACKET_HEART_LIST_UPDATE = new Identifier("heartbond", "heart_list_update");
	public static final Identifier PACKET_HEART_LIST_ADD = new Identifier("heartbond", "heart_list_add");
	public static final Identifier PACKET_HEART_LIST_REMOVE = new Identifier("heartbond", "heart_list_remove");
	public static final Identifier PACKET_TELEPORT_EVENT = new Identifier("heartbond", "teleport");

	public static Optional<UUID> getHeartUUID(PlayerEntity player) {
		Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
		if(component.isPresent()) {
			List<Pair<SlotReference, ItemStack>> list = component.get().getEquipped(Heartbond.HEART);
			if(list.size() > 0) {
				ItemStack heart = list.get(0).getRight();
				return EnderHeartItem.getHeartUUID(heart);
			}
		}
		return Optional.empty();
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Heartbond was initialized. Enjoy bonding!");
		SoulRecipe.SERIALIZER.toString();
		TrinketsApi.registerTrinket(Heartbond.HEART, new EnderHeartTrinket());
		Registry.register(Registry.ITEM, new Identifier("heartbond:ender_heart"), HEART);
		Registry.register(Registry.ITEM, new Identifier("heartbond:ender_soul"), SOUL);
	}
}
