package io.github.apace100.heartbond;

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
import top.theillusivec4.curios.api.SlotTypeInfo;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.Optional;
import java.util.UUID;

public class Heartbond implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger(Heartbond.class);

	public static final EnderHeartItem HEART = new EnderHeartItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
	public static final EnderSoulItem SOUL = new EnderSoulItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE));

	public static final Tag<Item> SOUL_CRAFTING_ITEMS = TagRegistry.item(new Identifier("heartbond", "soul_crafting_items"));

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
		CuriosApi.enqueueSlotType(SlotTypeInfo.BuildScheme.REGISTER, new SlotTypeInfo.Builder("ender_heart").icon(new Identifier("heartbond", "item/empty_ender_heart_slot")).size(1).build());
		Registry.register(Registry.ITEM, new Identifier("heartbond:ender_heart"), HEART);
		Registry.register(Registry.ITEM, new Identifier("heartbond:ender_soul"), SOUL);
	}
}
