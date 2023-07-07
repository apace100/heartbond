package io.github.apace100.heartbond;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings("unchecked")
public class SoulRecipe extends SpecialCraftingRecipe {
    public static final SpecialRecipeSerializer<SoulRecipe> SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("heartbond", "crafting_special_createsoul"), new SpecialRecipeSerializer<>(SoulRecipe::new));

    public SoulRecipe(Identifier identifier, CraftingRecipeCategory category) {
        super(identifier, category);
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        DefaultedList<ItemStack> stacks = super.getRemainder(inventory);
        for(int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() == Heartbond.HEART) {
                    stacks.set(i, itemStack.copy());
                }
            }
        }
        return stacks;
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        int hearts = 0;
        int souls = 0;
        for(int i = 0; i < inventory.size() && hearts < 3 && souls < 2; ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                if(itemStack.getItem() == Heartbond.HEART) {
                    if(itemStack.hasNbt() && itemStack.getNbt().containsUuid("HeartUUID")) {
                        hearts++;
                    } else {
                        return false;
                    }
                } else if(itemStack.isIn(Heartbond.SOUL_CRAFTING_ITEMS)) {
                    souls++;
                } else {
                    return false;
                }
            }
        }

        return souls == 1 && hearts == 2;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        List<ItemStack> hearts = Lists.newArrayList();
        int souls = 0;
        for(int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                if(itemStack.getItem() == Heartbond.HEART) {
                    hearts.add(itemStack);
                } else if(itemStack.isIn(Heartbond.SOUL_CRAFTING_ITEMS)) {
                    souls++;
                } else {
                    return ItemStack.EMPTY;
                }
            }
        }

        if(souls > 0 && hearts.size() == 2) {
            ItemStack result = new ItemStack(Heartbond.SOUL);
            NbtCompound tag = result.getOrCreateSubNbt("Heartbonds");
            tag.putUuid("Bond0", hearts.get(0).getNbt().getUuid("HeartUUID"));
            tag.putUuid("Bond1", hearts.get(1).getNbt().getUuid("HeartUUID"));
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Environment(EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
