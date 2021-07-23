package io.github.apace100.heartbond;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings("unchecked")
public class SoulRecipe extends SpecialCraftingRecipe {
    public static final SpecialRecipeSerializer<SoulRecipe> SERIALIZER = (SpecialRecipeSerializer<SoulRecipe>)Registry.register(Registry.RECIPE_SERIALIZER, new Identifier("heartbond", "crafting_special_createsoul"), new SpecialRecipeSerializer<>(SoulRecipe::new));

    public SoulRecipe(Identifier identifier) {
        super(identifier);
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingInventory inventory) {
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

    public boolean matches(CraftingInventory craftingInventory, World world) {
        int hearts = 0;
        int souls = 0;
        for(int i = 0; i < craftingInventory.size() && hearts < 3 && souls < 2; ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
            if (!itemStack.isEmpty()) {
                if(itemStack.getItem() == Heartbond.HEART) {
                    if(itemStack.hasNbt() && itemStack.getNbt().containsUuid("HeartUUID")) {
                        hearts++;
                    } else {
                        return false;
                    }
                } else if(Heartbond.SOUL_CRAFTING_ITEMS.contains(itemStack.getItem())) {
                    souls++;
                } else {
                    return false;
                }
            }
        }

        return souls == 1 && hearts == 2;
    }

    public ItemStack craft(CraftingInventory craftingInventory) {
        List<ItemStack> hearts = Lists.newArrayList();
        int souls = 0;
        for(int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
            if (!itemStack.isEmpty()) {
                if(itemStack.getItem() == Heartbond.HEART) {
                    hearts.add(itemStack);
                } else if(Heartbond.SOUL_CRAFTING_ITEMS.contains(itemStack.getItem())) {
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
